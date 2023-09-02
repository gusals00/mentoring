## 서론
이번에는 커넥션 풀이 무엇이고 어떤 구현체들이 존재하는지를 알아보자
또한 커넥션 풀 구현체들이 제공하는 속성을 알아보고, 공부한 동작 방식이 실제로 각각의 커넥션 풀 구현체에서 어떻게 구현되어 있는지를 확인해보자 
<br>

## 커넥션 풀
#### 커넥션 풀이란
커넥션 풀은 Connection 객체를 미리 만들어 pool에 저장해두고, 커넥션 요청이 오면, pool에 미리 생성된 Connection 객체를 사용하고, Connection 사용이 종료되면 pool에 Connection을 반납한다.
#### 커넥션 풀을 사용하는 이유
- DB 연결 요청이 올 때마다 DB 커넥션을 연결하면 커넥션을 생성하고 커넥션을 해제하는 과정에서 비용이 들고 어플리케이션이 느려지는 문제가 발생하기 때문에 사용한다.
- 물론 커넥션 사용 요청이 발생하는 횟수가 적다면 커넥션 생성/해제하는 과정의 비용이 적기 때문에 문제가 없을 수 있지만, 커넥션 사용 요청이 많이 들어올 경우에는 이 비용으로 인해 애플리케이션의 성능이 많이 저하될 수 있다.
- 따라서 커넥션 풀에 있는 미리 생성된 커넥션을 사용하여 커넥션 생성, 해제 비용을 줄이기 위해 사용한다. 
#### 커넥션 풀의 장점
1. Connection 객체를 재사용하여 객체 생성 비용을 줄일 수 있다.
2. Connection 수를 제한할 수 있어 과도한 접속으로 인한 자원 고갈을 방지할 수 있다.
#### 커넥션 풀 구현체들
Apache Commons DBCP, Tomcat DBCP, HikariCP, Oracle UCP등 다양한 커넥션 풀 구현체들이 존재한다.
커넥션 풀의 구현체는 여러가지가 있지만 여기서는 Hikari cp와 Commons-dbcp만 다루어보려고 한다.
또한 spring은 현재 커넥션 풀 구현체로 Hikari cp를 사용하고 다른 구현체들에 비해 월등한 성능을 보이기 때문에 현재 사용하고 있다고 한다. 이에 대한 자세한 내용은 [hikaricp github](https://github.com/brettwooldridge/HikariCP)을 참고하자
이제 Hikari cp와 Commons-dbcp의 동작방식과 구현 코드, 속성에 대해서 알아보자
<br><br>

## Hikari CP
hikari cp의 동작 방식은 다음과 같다.
### 동작 방식
**Connection 획득**
1. 커넥션 풀에 커넥션을 요청한다
2. 커넥션 풀을 요청한 스레드가 전에 사용했던 Connection리스트 내에 사용 가능한 Connection이 존재하면 스레드에 해당 커넥션을 할당한다.
3. 전에 사용했던 Connection리스트에서 사용 가능한 Connection이 존재하지 않으면, 전체 Connection들을 조회하고 사용 가능한 Connection이 존재하면 해당 Connection을 스레드에 할당한다
4. 만약 할당 가능한 커넥션이 커넥션 풀이 존재하지 않는다면, handoffQueue를 스레드가 pooling하면서 다른 스레드가 커넥션을 반납하여 handoffQueue에 반납한 커넥션이 들어오기를 기다린다
5. handoffQueue를 정해진 시간만큼 pooling하면서 기다리고, 정해진 시간이 지나면 커넥션 타임 아웃을 발생시키고, 정해진 시간 안에 handoffQueue에 커넥션이 반납되면 스레드가 커넥션을 할당 받는다

**Connection 반납**
1. 스레드가 커넥션 사용을 종료하여 커넥션 풀에 커넥션을 반납한다.
2. 커넥션에 반납할 때 handoffQueue에 커넥션이 반납되기를 pooling하면서 기다리는 스레드가 존재하면 handoffQueue에 반환한다.

### 속성
이제 Hikari CP에서 제공하는 속성들에 대해서 알아보자
**connectionTimeout**
- 커넥션을 요청하는데 기다리는 최대 대기 시간
- default 값은 30초이다.

**maximumPoolSize**
- 풀에 존재하는 커넥션 최대 개수이다
- 최대 커넥션 개수  = 유휴 커넥션 + 사용 중인 커넥션

**maxLifetime**
- 커넥션이 커넥션 풀에 존재하는 최대 lifetime
- 사용 중인 커넥션은 maxLifetime이 지나도 제거되지 않고, 커넥션이 반납된 이후에만 제거 가능

**keepaliveTime**
- 커넥션이 유효한지 확인하는 주기
- 커넥션 풀에 오래 존재하는 커넥션은 커넥션 연결이 끊길 가능성이 존재하기 때문에 커넥션이 유효한지를 확인
- keepaliveTime 주기마다 커넥션에 쿼리를 보내 커넥션 연결이 올바른지 확인

<br>
<br>

### HikariCP 내부 코드
이제 위에서 설명한 Hikari cp의 동작 방식이 진짜로 맞는지 HikariCP 코드를 보면서 확인해보자.
우선 **PoolEntry**가 무엇인지, **커넥션 획득**, **커넥션 반납**하는 코드 순으로 설명한다.
#### PoolEntry
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


#### 커넥션 획득
이제 커넥션을 획득하기 위해 내부적으로 어떤 메소드가 호출되고 어떤 일이 발생하는지 알아보자
스레드가 스레드 풀에 커넥션을 얻기 위해 요청하면 결과적으로는 **ConcurrentBag 클래스의 borrow() 메소드를 호출**한다
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
- 위 코드는  스레드가 전에 사용했던 Connection리스트 내에 사용 가능한 Connection이 존재하면 스레드에 해당 커넥션을 할당하는 부분이다. 
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
<Br>
  
#### 커넥션 반납
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
<br>
### HikariCP 정리
- 용어 정리
  - threadList : 커넥션 풀의 커넥션 중 스레드가 이전에 사용했던 커넥션 목록
  - sharedList : 커넥션 풀의 모든 커넥션이 존재, 커넥션이 생성될 때마다 이 리스트에 저장됨
  - handoffQueue : 커넥션 풀의 커넥션 중 스레드에 할당해줄 수 있는 스레드가 없을 경우 해당 큐에 들어오는 요소를 polling하고 있는 스레드에 할당해줌
<br> 
- 동작 원리 정리
  - 커넥션 할당
  	- 커넥션 할당시 `threadList`,`sharedList`를 순서대로 확인하고, 할당 가능한 커넥션(상태가 `STATE_NOT_IN_USE`)이 존재하면, 커넥션을 할당(할당 전에 `STATE_IN_USE`로 상태 변경)
  	-  `threadList`,`sharedList`에 할당 가능한 커넥션이 존재하지 않으면, 스레드가 handoffQueue를 polling하고 handoffQueue에 커넥션을 반납하면 스레드가 해당 커넥션을 할당
  - 커넥션 반납
	- 커넥션 반납시 handoffQueue를 polling하고 있는 스레드가 존재하면, handoffQueue에 커넥션을 반납 (PoolEntry 상태를 `STATE_NOT_IN_USE`로 변경)
  	 - handoffQueue를 polling하고 있는 스레드가 존재하지 않으면, threadList에 기록 저장
  
 <br><Br>

## Commons-dbcp
### 동작 방식
#### 커넥션 획득
  1. 커넥션 풀에 커넥션을 요청한다
  2. 커넥션 풀 내부에 CursorableLinkedList 내의 커넥션이 존재하면 LIFO 방식으로 커넥션을 커넥션 풀에서 꺼내 스레드에 할당한다.
#### 커넥션 반납
  1. 커넥션의 사용을 종료하고 커넥션 풀로 반납을 요청한다
  2. 커넥션을 CursorableLinkedList에 LIFO 방식으로 반납한다.

#### 유효성 검사
- EvictionTimer를 사용하여 keepaliveTime 주기마다 스레드 풀의 스레드가 정상적으로 동작하는지를 확인하고 정상적이지 않으면 스레드 풀에서 제거
<BR>

### 속성
**initialSize**
- 커넥션 풀에 초기에 생성되는 커넥션 개수
**maxActive**
- 동시에 사용할 수 있는 최대 커넥션 개수
**maxIdle**
- 커넥션 풀에 최대로 유지될 수 있는 커넥션 개수
**minIdle**
- 커넥션 풀에 최소한으로 유지할 커넥션 개수
#### 조건
  - maxActive>=initialSize
  	- maxActive = 10, initialSize=20일 경우 동시에 최대 10개가 실행중일 때 풀에 10개가 계속 유휴상태이기 때문
  - maxActive==maxIdle
  	- maxActive=10, maxIdle= 5일 경우, 동시에 실행되는 커넥션 수가 6개이고, 실행중인 커넥션 6개가 동시에 반납되면, 1개는 반드시 커넥션을 종료해야 함 
  - maxIdle>=minIdle
  	- maxIdle < minIdle 설정은 가능하지만 최솟값이 최대값보다 커서 논리적으로 오류
<br>
 <br>
### Commons DBCP 내부 코드
Commons DBCP 내부 코드는 Connection과 timestamp를 wraping한 ObjectTimeStampPair라는 클래스가 존재하고, CursorableLinkedList의 요소를 삽입, 삭제하고 다음과 같은 그림으로 이루어져 있다.
![](https://velog.velcdn.com/images/hochang/post/3b6d172a-578b-40c3-9b7b-8fb238e4ecfd/image.png)

이제 실제 코드로 CursorableLinkedList에서 요소를 pop하여 스레드에 할당하고, 스레드가 CursorableLinkedList에 커넥션을 반환하는지 확인해보자
<br>

#### 커넥션 획득
**GenericKeyedObjectPool 클래스의 borrowObject()**
```java
     public Object borrowObject(Object key) throws Exception {
        long starttime = System.currentTimeMillis();
        Latch latch = new Latch(key);
        byte whenExhaustedAction;
        long maxWait;
        synchronized (this) {
            // Get local copy of current config. Can't sync when used later as
            // it can result in a deadlock. Has the added advantage that config
            // is consistent for entire method execution
            whenExhaustedAction = _whenExhaustedAction;
            maxWait = _maxWait;

            // Add this request to the queue
            _allocationQueue.add(latch);

            // Work the allocation queue, allocating idle instances and
            // instance creation permits in request arrival order
            allocate();
        }
		.....
```
- 커넥션 풀의 커넥션을 얻기 위해 CursorableLinkedList의 요소를 remove 하는 부분은 `allocate()` 메소드에 구현되어 있다

**GenericKeyedObjectPool 클래스의 allocate()**
```java
private void allocate() {
        boolean clearOldest = false;
	...
 	while (allocationQueueIter.hasNext()) {
                 ...
                if (!pool.queue.isEmpty()) {
                    allocationQueueIter.remove();
                    latch.setPair(
                            (ObjectTimestampPair) pool.queue.removeFirst());
                    pool.incrementInternalProcessingCount();
                    _totalIdle--;
                    synchronized (latch) {
                        latch.notify();
                    }
                    // Next item in queue
                    continue;
                }
 ...
 }
```
- 위 코드에서 pool의 queue에서 removeFirst()를 통해 CursorableLinkedList의 요소를 삭제한다.
<br>

#### 커넥션 반납
GenericKeyedObjectPool 클래스의 returnObject()에서 addObjectToPool() 메소드를 호출해 커넥션을 커넥션 풀에 반환한다.
```java
private void addObjectToPool(Object key, Object obj, boolean decrementNumActive) throws Exception {
 			 ...
            if (isClosed()) {
                shouldDestroy = true;
            } else {
                // if there's no space in the pool, flag the object for destruction
                // else if we passivated successfully, return it to the pool
                if (_maxIdle >= 0 && (pool.queue.size() >= _maxIdle)) {
                    shouldDestroy = true;
                } else if (success) {
                    // borrowObject always takes the first element from the queue,
                    // so for LIFO, push on top, FIFO add to end
                    if (_lifo) {
                        pool.queue.addFirst(new ObjectTimestampPair(obj));
                    } else {
                        pool.queue.addLast(new ObjectTimestampPair(obj));
                    }
                    _totalIdle++;
                    if (decrementNumActive) {
                        pool.decrementActiveCount();
                    }
                    allocate();
                }
				...
```
- pool.queue에 요소를 추가하여 사용이 종료된 커넥션을 반납한다.
<br>

### CommonsDBCP 정리
- 커넥션 획득 시 커넥션 풀의 CursorableLinkedList에서 요소를 pop하여 스레드에 할당
- 커넥션 반환 시 커넥션 사용을 종료하고 커넥션 풀의 CursorableLinkedList에 요소를 리턴한다.

## 생각
- Hikari cp와 common dbcp 소스코드를 보면서 관련된 클래스들도 많고 코드가 복잡해 이해하는데 어려웠다. 물론 내가 소드코드를 잘못 이해했을 수도 있다.
- 이렇게 동작방식을 이해하고 코드에 어떻게 적용했는지 찾아보는 게 시간이 오래 걸리지만, 다른 코드들도 읽다보면 시간이 줄 거라고 생각한다
- 또한 다른 사람이 짠 코드를 빠르게 이해할 수 있는 능력도 필요하다고 생각하게 되었다. 그 이유는 개발은 나 혼자 하는게 아니라 팀원들과 함께 하기 때문이다.
<br>
<br><br>
hikari cp 참고 자료

- https://steady-coding.tistory.com/564#google_vignette
- [https://velog.io/@blackbean99/SpringBoot-HikariCP를-알아보자](https://velog.io/@blackbean99/SpringBoot-HikariCP%EB%A5%BC-%EC%95%8C%EC%95%84%EB%B3%B4%EC%9E%90)
- https://brunch.co.kr/@growthminder/25
- https://www.javatpoint.com/java-synchronousqueue-offer-method → Synchronous Queue poll(), offer() 메소드 설명
<br>
 
commons-dbcp 참고 자료
- 동작 원리 : https://d2.naver.com/helloworld/5102792
- commons dbcp 코드 :https://github.com/mariusae/commons-pool/tree/master/src/java/org/apache/commons/pool/impl