## 서론

ArrayList와 LinkedList 중 뭘 쓰는 게 좋고 그 이유는 무엇일까?

<br>


**보통 다음과 같이 알고 있는 경우가 많을 것이라고 생각한다.**

ArrayList는 상수 시간에 요소에 접근 가능하고, LinkedList는 리스트 내에 있는 요소를 검색하려면 처음 노드부터 순차적으로 찾아야 한다.

ArrayList는 삽입/삭제 시 요소들의 위치를 앞뒤로 이동 시켜야 하고, LinkedList는 삽입/삭제 시 요소들의 위치를 이동 시킬 필요 없이 노드가 가리키는 포인터만 바꿔주면 된다.

따라서 삽입/삭제가 빈번하면 LinkedList를 사용하고, 요소를 가져오는 것이 빈번하면 ArrayList를 사용한다.

<br>

**그렇다면 실제로 개발에서 LinkedList를 많이 사용할까?**

https://discuss.kotlinlang.org/t/why-kotlin-does-not-provide-linkedlist-implementation/15991

위 글에서는 코틀린에서 왜 LinkedList 구현체를 제공하지 않는지 토론하고 있다. 이 글을 통해 ArrayList와 LinkedList 중 뭘 쓰는 게 좋을지 알아보자.

## LinkedList는 잘 사용되지 않는다

![image](https://github.com/gusals00/mentoring/assets/87007552/dcb8cdd5-abb9-4e7c-b627-45a709027e4a)
https://twitter.com/joshbloch/status/583813919019573248

먼저 위 글을 보면 자바 컬렉션 프레임워크 등 수많은 자바 플랫폼의 설계와 구현을 주도한 조슈아 블로치도 본인이 설계했지만 사용하지 않는다고 말하고 있다.

<br>

**왜 LinkedList가 잘 사용되지 않는 걸까?** 위의 토론을 보면 이유는 다음과 같다고 생각한다.

- 메모리 사용량
    - ArrayList는 내부적으로 연속된 메모리 블록을 사용하고 한번에 할당되기 때문에 메모리 사용이 효율적이다.
    - LinkedList는 각 노드마다 연결 정보를 위해 추가 메모리를 필요로 하며, 메모리 사용량이 많아지고 메모리 효율이 떨어진다.
- 엑세스 속도
    - ArrayList는 인덱스 기반으로 요소에 빠르게 접근할 수 있다.
    - LinkedList는 리스트 내에 있는 요소를 검색하려면 처음 노드부터 순차적으로 찾아야 하므로 느리다.
- CPU 캐시 효율(가장 큰 이유라고 생각한다)
    - ArrayList는 요소들이 메모리의 연속된 블록에 할당되므로 CPU 캐시 효율이 좋고 자주 사용되는 데이터가 빠르게 검색되고 성능이 향상된다.
    - LinkedList의 노드들은 메모리의 여러 위치에 할당되고 연결될 수 있으므로 CPU 캐시 효율이 상대적으로 낮다.

<br>

**그렇다면 왜 LinkedList가 ArrayList에 비해 삽입/삭제에서 빠르다고 하는 말이 많은 걸까?**

이전에 CPU 속도가 느릴 때는 LinkedList가 ArrayList에 비해 삽입/삭제에서 더 빠르다고 하는 말이 이론적으로는 맞지만, 현대 컴퓨터의 CPU 속도는 RAM 접근 속도보다 훨씬 빠르다.

따라서 CPU 캐시가 나왔다. RAM의 데이터에 직접 접근하는 대신 CPU는 RAM의 데이터에 L1 캐시를 통해서 간접적으로 접근한다. 하지만 CPU 캐시의 크기는 RAM보다 훨씬 작기 때문에 여전히 RAM이 필요하다.

CPU가 다음에 접근할 데이터가 L1 캐시(또는 L2,L3 캐시)에 없는 경우 RAM으로부터 가져와야 하고, CPU는 오랫동안 기다려야 하게 된다. 이것을 cache miss라고 부른다. 

cache miss를 줄이기 위해 CPU가 RAM의 주소에 있는 데이터에 접근하길 원할 때 해당 주소의 데이터만 가져오는게 아니라 근처의 데이터도 같이 가져온다.

<br>

따라서 요소들이 메모리의 연속된 블록에 할당되는 ArrayList는 cache miss를 줄이기 때문에 빠르다는 것이다.

또한 ArrayList에서 삽입/삭제 시 요소들을 이동시키는 비용은 cache miss의 비용보다 훨씬 싸고, 생각해보면 리스트 중간에 삽입과 삭제가 일어날 일은 많지 않다.

## 결론

LinkedList가 아니라 ArrayList를 사용하자.

큐,스택을 사용해야 할 때도 LinkedList를 사용하는 것 보다 더욱 최적화된 컬렉션인 ArrayDeque를 사용하는 것이 좋다.
