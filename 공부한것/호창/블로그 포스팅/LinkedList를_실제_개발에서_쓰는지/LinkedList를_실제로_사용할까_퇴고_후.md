## 서론

팀원의 피드백을 바탕으로 [기존 LinkedList를 실제로 사용하는지? 관련 글](https://github.com/HoChangSUNG/mentoring/blob/main/%EA%B3%B5%EB%B6%80%ED%95%9C%EA%B2%83/%ED%98%B8%EC%B0%BD/%EB%B8%94%EB%A1%9C%EA%B7%B8%20%ED%8F%AC%EC%8A%A4%ED%8C%85/LinkedList%EB%A5%BC_%EC%8B%A4%EC%A0%9C_%EA%B0%9C%EB%B0%9C%EC%97%90%EC%84%9C_%EC%93%B0%EB%8A%94%EC%A7%80/LinkedList%EB%A5%BC_%EC%8B%A4%EC%A0%9C%EB%A1%9C_%EC%82%AC%EC%9A%A9%ED%95%A0%EA%B9%8C_%ED%87%B4%EA%B3%A0_%EC%A0%84.md)을 수정하여 다시 작성하였다.  
또한 기존 글에 대한 [팀원의 피드백과 어떻게 피드백을 반영했는지 깃허브](https://github.com/HoChangSUNG/mentoring/blob/main/%EA%B3%B5%EB%B6%80%ED%95%9C%EA%B2%83/%ED%98%B8%EC%B0%BD/%EB%B8%94%EB%A1%9C%EA%B7%B8%20%ED%8F%AC%EC%8A%A4%ED%8C%85/LinkedList%EB%A5%BC_%EC%8B%A4%EC%A0%9C_%EA%B0%9C%EB%B0%9C%EC%97%90%EC%84%9C_%EC%93%B0%EB%8A%94%EC%A7%80/%ED%94%BC%EB%93%9C%EB%B0%B1%20%EB%B0%8F%20%EB%B0%98%EC%98%81%20%EC%82%AC%ED%95%AD.md)에 정리해두었다.

대부분의 블로그에 ArrayList와 LinkedList에 대해 설명할 때 다음과 같은 내용이 많이 보인다.

**중간에 요소의 삽입과 삭제가 자주 일어나는 경우에는 LinkedList를 요소의 탐색이 자주 일어나는 경우에는 ArrayList를 사용하는 것이 좋다**

그렇다면 실제로 LinkedList를 개발할 때 사용할까?  
멘토링 시간에 ArrayList와 LinkedList에 대해 얘기하는데, **멘토님이 실제로 개발에서 LinkedList를 사용할까요?** 라는 질문을 하셨다.  
그리고 이와 관련해서 생각해보면 좋을 아티클을 나와 팀원에게 공유해주시고 고민해보라고 하셨다. 

**아티클**  
https://discuss.kotlinlang.org/t/why-kotlin-does-not-provide-linkedlist-implementation/15991
https://twitter.com/joshbloch/status/583813919019573248

위 첫번째 아티클은 **코틀린에서 List 구현체로 왜 LinkedList 구현체를 제공하지 않는지 사람들이 토론**한 내용이다. 
코틀린과 관련된 내용이지만 코틀린뿐만 아니라 다른 언어에서도 실제로 개발할 때 LinkedList를 사용하는지와 관련해서 토론해보면 좋은 아티클이다.  

이 아티클을 읽고 **실제로 개발할 때 LinkedList를 사용하는지 생각**해보자

<br><br>
## **LinkedList를 실제 개발에서 잘 쓰지 않는 이유**

위 아티클 내용을 기반으로 생각했을 때 LinkedList는 실제 개발에서 거의 사용되지 않는다고 판단된다. 

실제 개발에서 **LinekdList를 사용하지 않는 이유**는 다음 두 가지 이유 때문이라고 생각한다.

1. 애플리케이션에서 리스트 중간에 삽입과 삭제가 일어날 일이 거의 없기 때문
2. CS 관점에서 LinkedList보다 ArrayList가 더 빠르기 때문

이제 이 이유에 대해 자세히 이야기해보자.

<br>

### 애플리케이션에서 리스트 중간에 삽입과 삭제가 일어날 일이 거의 없기 때문

실제 백엔드에서 애플리케이션을 개발하는 상황을 생각해보자.

우리가 웹 어플리케이션을 작성할 때 List는 보통 DB에서 정보를 조회하고 이를 클라이언트에게 전달해주는 경우 많이 사용하고, List의 요소를 중간에 삽입하거나 삭제하는 코드를 개발자가 작성하지는 경우는 거의 없다.

이처럼 프로젝트를 진행할 때 List의 중간에 요소를 삽입, 삭제하는 코드는 사실상 작성하는 경우가 거의 없고, 보통 데이터 목록 조회를 위해서 List를 사용한다.

이런 이유로 LinkedList를 사용할 일 자체가 거의 없다.

<br><br>

### CS 관점에서 LinkedList보다 ArrayList가 더 빠르기 때문

LinkedList보다 ArrayList가 더 빠른 이유는 CPU 캐시의 캐시 미스 때문이다.  
CPU로 메모리를 로딩할 때 캐시 라인 사이즈만큼 연속된 메모리를 읽어 CPU 캐시에 저장한다.  

이는 RAM에서 데이터를 읽어오는 시간이 CPU에서 처리하는 시간보다 훨씬 오래 걸리기 때문에 이 시간 차이를 줄이기 위해 CPU 캐시를 사용한다.  
이 내용을 arrayList와 LinkedList에 대입해 생각해보면 cpu 캐시에 메모리를 로드할 때 캐시 라인 사이즈만큼 ram의 연속된 데이터를 읽어온다  
따라서 배열은 연속적인 메모리 공간을 할당하고 그 공간에 저장하기 때문에 배열의 요소를 CPU로 로드하면 그 이후의 배열의 요소들도 CPU 캐시에 저장되는 것이다. 

즉, ArrayList는 데이터를 로드할 때 연속적인 메모리 공간에 저장되어 있어 ArrayList 요소들이 같이 캐시에 로드되어 ArrayList의 요소에 접근하거나 수정할 때 RAM에 접근하지 않아도 되기 때문에  LinkedList보다 속도가 빠르다는 것이다.  
물론, ArrayList 또한 캐시 미스가 발생할 수 있다. 하지만, **연속적인 공간에 저장**되어 있기 때문에 LinkedList의 캐시 미스가 발생하는 횟수보다는 적을 것이다. 

<br>

**LinkedList의 요소들은 CPU 캐시에 같이 로드되지 않는 것일까?**

**나의 개인적인 생각**으로는 ArrayList의 요소들은 연속된 메모리 공간을 할당되기 때문에  cpu 캐시에 같이 로드되는 것을 보장할 수  있다.

이에 반해 LinkedList는 연속된 메모리 공간에 저장되지 않지만 **cpu 캐시에 같이 로드 될 수도** 있다.

여기서 LinkedList는 연속된 메모리 공간에 저장되지 않지만 cpu 캐시에 같이 로드될 수도 있는 이유는 다음과 같다.

- **LinkedList의 여러 요소가 캐시 사이즈 내에 위치해 있다면 같이 cpu 캐시에 로드될 수 있기 때문**이다
    - 아래 사진과 같이 LinkedList의 첫번째 요소를 cpu가 읽으려 할 때 cpu에 로드하려는 요소를 기준으로 캐시 라인 사이즈보다 작은 위치에 LinkedList의 두번째 요소가 존재한다면  LinkedList의 첫번째 요소와 두번째 요소가 cpu 캐시에 저장될 수 있기 때문이다.
    
    ![Untitled (1)](https://github.com/HoChangSUNG/mentoring/assets/76422685/bb2abd58-37c0-4b65-87c0-bf334d1e3b6f)


이러한 경우도 가능할 것이라 생각했기 때문에 LinkedList는 연속된 메모리 공간에 저장되지 않지만 **cpu 캐시에 같이 로드될 수도 있다**고 한 것이다.  
여기서 중요한 점은 LinkedList 또한 캐시 미스가 항상 발생하는 것은 아니지만, 연속적인 공간에 저장된 ArrayList보다는 캐시 미스가 자주 일어날 수밖에 없다는 점이다.  
즉, 캐시 미스가 ArrayList보다는 LinkedList에서 자주 나타나서 ArrayList의 성능이 더 좋은 것이지, LinkedList라고 해서 캐시 미스가 무조건 발생하는 것은 아니다.

<br>

## 결론 및 생각

### 결론

- 실제 개발할 때 LinkedList를 사용할 일이 없기 때문에 LinkedList를 사용하지 않고, ArrayList를 위주로 사용한다.

### 생각

- 이론적으로는 중간에 삽입, 삭제가 빈번하게 일어나면 LinkedList를 사용한다라고 알고 있었는데, 실제로 이 LinkedList를 사용할까? 라는 생각은 해보지 않은 것 같다
- 이론 내용과 실제 개발할 때 모두 이용하는 것이 아니고 상황에 따라 적절한 이론 내용을 적용해야 한다고 느끼게 되었다.
