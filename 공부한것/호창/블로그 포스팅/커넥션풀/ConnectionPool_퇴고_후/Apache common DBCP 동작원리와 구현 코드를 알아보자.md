서론
Commons-dbcp의 속성들을 알아보자 그 후, Commons-dbcp의 동작 방식과 그 동작 방식이 진짜인지 구현 코드를 알아보자.
속성
initialSize
커넥션 풀에 초기에 생성되는 커넥션 개수
maxActive
동시에 사용할 수 있는 최대 커넥션 개수
maxIdle
커넥션 풀에 최대로 유지될 수 있는 커넥션 개수
minIdle
커넥션 풀에 최소한으로 유지할 커넥션 개수
조건
maxActive>=initialSize
maxActive = 10, initialSize=20일 경우 동시에 최대 10개가 실행중일 때 풀에 10개가 계속 유휴상태이기 때문
maxActive==maxIdle
maxActive=10, maxIdle= 5일 경우, 동시에 실행되는 커넥션 수가 6개이고, 실행중인 커넥션 6개가 동시에 반납되면, 1개는 반드시 커넥션을 종료해야 함
maxIdle>=minIdle
maxIdle < minIdle 설정은 가능하지만 최솟값이 최대값보다 커서 논리적으로 오류
동작 방식
커넥션 획득
커넥션 풀에 커넥션을 요청한다
커넥션 풀 내부에 CursorableLinkedList 내의 커넥션이 존재하면 LIFO 방식으로 커넥션을 커넥션 풀에서 꺼내 스레드에 할당한다.
커넥션 반납
커넥션의 사용을 종료하고 커넥션 풀로 반납을 요청한다
커넥션을 CursorableLinkedList에 LIFO 방식으로 반납한다.
유효성 검사
EvictionTimer를 사용하여 keepaliveTime 주기마다 스레드 풀의 스레드가 정상적으로 동작하는지를 확인하고 정상적이지 않으면 스레드 풀에서 제거
<BR>
Commons DBCP 내부 코드
Commons DBCP 내부 코드는 Connection과 timestamp를 wraping한 ObjectTimeStampPair라는 클래스가 존재하고, CursorableLinkedList의 요소를 삽입, 삭제하고 다음과 같은 그림으로 이루어져 있다.

이제 실제 코드로 CursorableLinkedList에서 요소를 pop하여 스레드에 할당하고, 스레드가 CursorableLinkedList에 커넥션을 반환하는지 확인해보자

커넥션 획득
GenericKeyedObjectPool 클래스의 borrowObject()
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

​
커넥션 풀의 커넥션을 얻기 위해 CursorableLinkedList의 요소를 remove 하는 부분은 allocate() 메소드에 구현되어 있다
GenericKeyedObjectPool 클래스의 allocate()
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

​
위 코드에서 pool의 queue에서 removeFirst()를 통해 CursorableLinkedList의 요소를 삭제한다.

커넥션 반납
GenericKeyedObjectPool 클래스의 returnObject()에서 addObjectToPool() 메소드를 호출해 커넥션을 커넥션 풀에 반환한다.
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

​
pool.queue에 요소를 추가하여 사용이 종료된 커넥션을 반납한다.

CommonsDBCP 정리
커넥션 획득 시 커넥션 풀의 CursorableLinkedList에서 요소를 pop하여 스레드에 할당
커넥션 반환 시 커넥션 사용을 종료하고 커넥션 풀의 CursorableLinkedList에 요소를 리턴한다.
commons-dbcp 참고 자료
동작 원리 : https://d2.naver.com/helloworld/5102792
commons dbcp 코드 :https://github.com/mariusae/commons-pool/tree/master/src/java/org/apache/commons/pool/impl
