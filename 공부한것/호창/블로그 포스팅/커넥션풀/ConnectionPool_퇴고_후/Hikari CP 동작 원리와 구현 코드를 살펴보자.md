## 서론

Hikari CP의 속성들을 알아보자 그 후, Hikari CP의 동작 방식과 그 동작 방식이 진짜인지 구현 코드를 알아보자.

## Hikari CP 속성

이제 Hikari CP에서 제공하는 속성들에 대해서 알아보자

**connectionTimeout**

- 커넥션을 요청하는데 기다리는 최대 대기 시간
- default 값은 30초이다.

**maximumPoolSize**

- 풀에 존재하는 커넥션 최대 개수이다
- 최대 커넥션 개수 = 유휴 커넥션 + 사용 중인 커넥션

**maxLifetime**

- 커넥션이 커넥션 풀에 존재하는 최대 lifetime
- 사용 중인 커넥션은 maxLifetime이 지나도 제거되지 않고, 커넥션이 반납된 이후에만 제거 가능

**keepaliveTime**

- 커넥션이 유효한지 확인하는 주기
- 커넥션 풀에 오래 존재하는 커넥션은 커넥션 연결이 끊길 가능성이 존재하기 때문에 커넥션이 유효한지를 확인
- keepaliveTime 주기마다 커넥션에 쿼리를 보내 커넥션 연결이 올바른지 확인

## 동작 방식

hikari cp의 동작 방식은 다음과 같다.

### **Connection 획득**

1. 커넥션 풀에 커넥션을 요청한다
2. 커넥션 풀을 요청한 스레드가 전에 사용했던 Connection리스트 내에 사용 가능한 Connection이 존재하면 스레드에 해당 커넥션을 할당한다.
3. 전에 사용했던 Connection리스트에서 사용 가능한 Connection이 존재하지 않으면, 전체 Connection들을 조회하고 사용 가능한 Connection이 존재하면 해당 Connection을 스레드에 할당한다
4. 만약 할당 가능한 커넥션이 커넥션 풀이 존재하지 않는다면, handoffQueue를 스레드가 pooling하면서 다른 스레드가 커넥션을 반납하여 handoffQueue에 반납한 커넥션이 들어오기를 기다린다
5. handoffQueue를 정해진 시간만큼 pooling하면서 기다리고, 정해진 시간이 지나면 커넥션 타임 아웃을 발생시키고, 정해진 시간 안에 handoffQueue에 커넥션이 반납되면 스레드가 커넥션을 할당 받는다

### **Connection 반납**

1. 스레드가 커넥션 사용을 종료하여 커넥션 풀에 커넥션을 반납한다.
2. 커넥션에 반납할 때 handoffQueue에 커넥션이 반납되기를 pooling하면서 기다리는 스레드가 존재하면 handoffQueue에 반환한다.

## HikariCP 내부 코드

이제 위에서 설명한 Hikari cp의 동작 방식이 진짜로 맞는지 HikariCP 코드를 보면서 확인해보자.
우선 **PoolEntry**가 무엇인지, **커넥션 획득**, **커넥션 반납**하는 코드 순으로 설명한다.

### PoolEntry

- Connection 객체와 현재 커넥션의 상태, 최근 접근 시간 등을 wrapping하고 있는 PoolEntry 객체로 커넥션 풀이 커넥션을 관리하고 있다.
- 여기서 말한 현재 커넥션 상태는 여러 상태로 구분된다.
**커넥션 상태**
    - `STATE_NOT_IN_USE` : Connection이 현재 사용 가능한 상태
    - `STATE_IN_USE` : Connection이 스레드에 할당되어 있는 상태
    - **IConcurrentBagEntry 인터페이스**
    
    ```java
    public interface IConcurrentBagEntry
    {
       int STATE_NOT_IN_USE = 0;
       int STATE_IN_USE = 1;
       int STATE_REMOVED = -1;
       int STATE_RESERVED = -2;
    
       boolean compareAndSet(int expectState, int newState);
       void setState(int newState);
       int getState();
    }
    ```
    

### 커넥션 획득

이제 커넥션을 획득하기 위해 내부적으로 어떤 메소드가 호출되고 어떤 일이 발생하는지 알아보자
스레드가 스레드 풀에 커넥션을 얻기 위해 요청하면 결과적으로는 **ConcurrentBag 클래스의 borrow() 메소드를 호출**한다.

```java
   public T borrow(long timeout, final TimeUnit timeUnit) throws InterruptedException
   {
      // Try the thread-local list first
      final var list = threadList.get();
      for (int i = list.size() - 1; i >= 0; i--) {
         final var entry = list.remove(i);
         @SuppressWarnings("unchecked")
         final T bagEntry = weakThreadLocals ? ((WeakReference<T>) entry).get() : (T) entry;
         if (bagEntry != null && bagEntry.compareAndSet(STATE_NOT_IN_USE, STATE_IN_USE)) {
            return bagEntry;
         }
      }

```

- 위 코드는 스레드가 전에 사용했던 Connection리스트 내에 사용 가능한 Connection이 존재하면 스레드에 해당 커넥션을 할당하는 부분이다.
- 여기서 Connection 리스트는 `threadList`이고 threadList를 for문으로 반복하면서, `bagEntry.compareAndSet(STATE_NOT_IN_USE, STATE_IN_USE)`를 통해 threadList에 존재하는 Connection이 현재 스레드에 할당 가능한 상태(STATE_NOT_IN_USE)인지, 이미 스레드가 사용하고 있는 상태(`STATE_IN_USE`)인지 확인한다.
- 또한 스레드에 할당할 수 있으면 bagEntry.compareAndSet을 통해 커넥션 상태를 스레드가 사용하고 있는 상태로 업데이트 해준다.

```java
      // Otherwise, scan the shared list ... then poll the handoff queue
      final int waiting = waiters.incrementAndGet();
      try {
         for (T bagEntry : sharedList) {
            if (bagEntry.compareAndSet(STATE_NOT_IN_USE, STATE_IN_USE)) {
               // If we may have stolen another waiter's connection, request another bag add.
               if (waiting > 1) {
                  listener.addBagItem(waiting - 1);
               }
               return bagEntry;
            }
         }
```

- `threadList`를 통해 할당할 수 있는 스레드가 존재하지 않으면 전체 커넥션에서 할당 가능한 커넥션을 조회하고 할당 가능한 커넥션이 존재하면 스레드에 할당해준다

```java
         timeout = timeUnit.toNanos(timeout);
         do {
            final var start = currentTime();
            final T bagEntry = handoffQueue.poll(timeout, NANOSECONDS);
            if (bagEntry == null || bagEntry.compareAndSet(STATE_NOT_IN_USE, STATE_IN_USE)) {
               return bagEntry;
            }

            timeout -= elapsedNanos(start);
         } while (timeout > 10_000);

```

- 스레드 풀의 모든 커넥션(`sharedList`)이 이미 스레드에 할당되어 있다면 handoffQueue에 요소가 삽입될 때까지 스레드가 대기하고, 다른 스레드가 커넥션을 반납하여 handoffQueue에 요소가 삽입되면 해당 요소를 스레드에 할당하고, handoffQueue에서 제거한다.
- 이 때 정해진 connectionTimeout만큼만 handoffQueue를 polling하면서 커넥션을 얻기 위해 대기한다.

### 커넥션 반납

이제 커넥션을 반납하기 위해 내부적으로 어떤 메소드가 호출되는지 알아보자
커넥션을 반납하기 위해 `ConcurrentBag의 requite(final T bagEntry)` 메소드가 호출된다.

```java
   public void requite(final T bagEntry)
   {
      bagEntry.setState(STATE_NOT_IN_USE);

      for (var i = 0; waiters.get() > 0; i++) {
         if (bagEntry.getState() != STATE_NOT_IN_USE || handoffQueue.offer(bagEntry)) {
            return;
         }
         else if ((i & 0xff) == 0xff) {
            parkNanos(MICROSECONDS.toNanos(10));
         }
         else {
            Thread.yield();
         }
      }

      final var threadLocalList = threadList.get();
      if (threadLocalList.size() < 50) {
         threadLocalList.add(weakThreadLocals ? new WeakReference<>(bagEntry) : bagEntry);
      }
   }

```

- Connection 객체를 반납하면 우선 Connection의 상태를 바꾸어준다.(`STATE_NOT_IN_USE`로 )
- 스레드들이 handoffQueue를 대기하고 있으면 handoffQueue에 Connection을 저장(`handoffQueue.offer()`가 true일 경우)하고 반납을 종료한다.
- handoffQueue에 스레드들이 커넥션을 얻기 위해 handoffQueue를 polling하고 있지 않으면(`handoffQueue.offer()`가 false일 경우) threadList에 해당 Connection을 저장한다.

## HikariCP 정리

이제 Hikari CP를 정리해보자.

- **용어 정리**
    - threadList : 커넥션 풀의 커넥션 중 스레드가 이전에 사용했던 커넥션 목록
    - sharedList : 커넥션 풀의 모든 커넥션이 존재, 커넥션이 생성될 때마다 이 리스트에 저장됨
    - handoffQueue : 커넥션 풀의 커넥션 중 스레드에 할당해줄 수 있는 스레드가 없을 경우 해당 큐에 들어오는 요소를 polling하고 있는 스레드에 할당해줌
- **동작 원리 정리**
    - **커넥션 할당**
        - 커넥션 할당시 `threadList`,`sharedList`를 순서대로 확인하고, 할당 가능한 커넥션(상태가 `STATE_NOT_IN_USE`)이 존재하면, 커넥션을 할당(할당 전에 `STATE_IN_USE`로 상태 변경)
        - `threadList`,`sharedList`에 할당 가능한 커넥션이 존재하지 않으면, 스레드가 handoffQueue를 polling하고 handoffQueue에 커넥션을 반납하면 스레드가 해당 커넥션을 할당
    - **커넥션 반납**
        - 커넥션 반납시 handoffQueue를 polling하고 있는 스레드가 존재하면, handoffQueue에 커넥션을 반납 (PoolEntry 상태를 `STATE_NOT_IN_USE`로 변경)
        - handoffQueue를 polling하고 있는 스레드가 존재하지 않으면, threadList에 기록 저장

## 생각

- Hikari cp와 common dbcp 소스코드를 보면서 관련된 클래스들도 많고 코드가 복잡해 이해하는데 어려웠다. 물론 내가 소드코드를 잘못 이해했을 수도 있다.
- 이렇게 동작방식을 이해하고 코드에 어떻게 적용했는지 찾아보는 게 시간이 오래 걸리지만, 다른 코드들도 읽다보면 시간이 줄 거라고 생각한다
- 또한 다른 사람이 짠 코드를 빠르게 이해할 수 있는 능력도 필요하다고 생각하게 되었다. 그 이유는 개발은 나 혼자 하는게 아니라 팀원들과 함께 하기 때문이다.

### hikari cp 참고 자료

- https://steady-coding.tistory.com/564#google_vignette
- [https://velog.io/@blackbean99/SpringBoot-HikariCP를-알아보자](https://velog.io/@blackbean99/SpringBoot-HikariCP%EB%A5%BC-%EC%95%8C%EC%95%84%EB%B3%B4%EC%9E%90)
- https://brunch.co.kr/@growthminder/25
- https://www.javatpoint.com/java-synchronousqueue-offer-method → Synchronous Queue poll(), offer() 메소드 설명