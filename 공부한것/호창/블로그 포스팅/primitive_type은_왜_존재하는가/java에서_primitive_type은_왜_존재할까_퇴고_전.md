# primitive type은 왜 존재할까?

## **서론**

자바에는 primitive type과 wrapper type이 존재한다.  
하지만 여기서 궁금증이 생긴다. 자바에서 wrapper type과 primitive type 둘 다 존재하는 이유는 무엇일까??  
java는 객체 지향 언어이다. 따라서 객체를 중심으로 하기 때문에 객체인 wrapper type으로 숫자나 문자를 표현하는 것은 당연하다고 생각한다.  
하지만,  wrapper type이 존재하는데 primitive type는 왜 사용할까? wrapper type로 대체하면 안될까?

이러한 궁금증을 해결해보기 위해 primitive type이 존재하는 이유를 알아보자

**primitive type 존재 이유는 성능과 간단함(Simplicity)**이다.

먼저 **간단함 측면**에서 알아보고 **성능 측면**에서 2가지 이유를 알아보자  
그 후 **primitive type과 wrapper type의 성능 측정 결과**를 통해 primitive type이 존재하는 이유를 알아보자

<br>

## primitive type 존재 이유

### 1. 간단함(Simplicity)

wrapper type에 비해 primitive type이 쉽게 이해할 수 있고, 개발자가 사용할 때 쉽게 사용될 수 있기 때문이다.

### 2. 성능

- **생성 비용**

  wrapper type은 객체이기 때문에 heap 영역에 메모리를 할당하고 객체를 생성한다. 객체를 생성하기 위해서는 객체를 생성하는데 비용이 들게 된다. 물론 객체를 생성하는 비용이 성능에 큰 영향을 미치지 않을 수도 있다.  
  하지만 수많은 객체를 생성하거나, 멀티 스레딩 환경에서 동시에 객체를 생성해야 하는 경우에는 객체를 생성하는데 시간이 걸려 성능이 저하될 수 있다.  
  따라서 이러한 객체 생성 비용을 줄이기 위해서 wrapper type이 필요한 경우가 아니라면 primitive type을 사용한다.

- **접근 속도**

  primitive type과 wrapper type을 사용하기 위해 각각의 type에 접근할 때 메모리 관점에서 접근 속도에 대해 생각해보자.  
  primitive type은 stack 영역에 할당되고, 리터럴이기 때문에 메모리 자체에 값이 저장된다. 따라서  stack 영역에 접근하면 해당 primitive type 값을 바로 접근할 수 있다.  
  그에 반해 wrapper type은 객체이기 때문에 stack 영역에 접근하여 wrapper 객체가 저장된 heap 영역의 위치를 구한 후, heap 영역에 위치하는 wrapper객체에 접근할 수 있다.  
  이처럼 wrapper class는 stack 영역에 접근한 후 heap 영역에 추가로 접근해야 하지만, primitive type은 stack 영역에만 접근하면 되기 때문에 접근 속도가 primitive type이 더 빠르다.

<br>

## **primitive type과 wrapper type 성능 비교**

위에서 **primitive type이 wrapper 타입보다 접근 속도가 더 빠르기 때문**에 즉, **성능이 더 좋기 때문**에 primitive type이 존재한다고 했다.  
그렇다면 과연 정말로 primitive type이 wrapper type보다 빠른 것일까??

[Baldung의 **Java Primitives Versus Object** 라는 아티클](https://www.baeldung.com/java-primitives-vs-objects)에서 primitive type과 wrapper type간 성능을 비교한 내용이 있다.  
java Primitives Versus Object 아티클에 나온 primitive type과 wrapper type간 성능 비교 그래프 결과를 통해 한번 알아보자

### **조건**

- 500만 개의 요소를 가진 `elements` 라는 배열을 생성하고, 배열의 요소들은 마지막 요소를 제외하고는 모두 동일한 값을 가진다고 하자.
- 각각 primitive type과 wrapper type으로 elements라는 이름의 배열을 생성하고 아래 코드를 대상으로 JMH 벤치마킹 툴을 이용해 테스트를 진행했다고 한다.

```java
while (!pivot.equals(elements[index])) {
    index++;
}
```

### **결과**

![image](https://github.com/HoChangSUNG/mentoring/assets/76422685/e84b5818-4727-432b-b307-8aaaf6b5b2eb)
- 간단한 연산임에도 불구하고 wrapper class가 더 오랜 시간이 걸리는 것을 알 수 있다.
- 덧셈, 곱셈, 나눗셈 등 더 복잡한 연산을 수행한다면 primitive type과 wrapper 타입 간 속도 차이는 더 크게 날 것이다.

## 결론 및 생각

### 결론

- primitive type은 **성능 상의 이점** 때문에 존재한다.

### 생각

- primitive type이 필요한 이유를 메모리 구조 측면에서 생각해보았는데, 이전에 공부한 것들이 다른 것들과 밀접하게 연관되어 있었다.

  이런 이론 지식들이 단편적으로 구성된 것이 아니라 다른 이론 지식들과 연결되어 있다는 점을 알게 되었다.


**참고 자료**

- https://stackoverflow.com/questions/14477743/why-are-there-primitive-datatype-in-java
- https://www.baeldung.com/java-primitives-vs-objects
- https://www.quora.com/Why-does-Java-have-primitive-data-types