## 서론

자바의 데이터 타입에는 primitive type, wrapper type이 존재한다.

<br>

**wrapper type이 필요한 경우는 다음과 같다**

- 객체로서 다루어야 하는 경우
    - primitive type으로는 제네릭 같은 기능을 사용하지 못한다.
- null 값이 필요한 경우
    - primitive type은 null 값을 가질 수 없다.
- wrapper type에서 제공하는 상수나 메소드가 필요한 경우
    - wrapper type은 Integer.parseInt() 같은 메소드나 Integer.MAX_VALUE 같은 상수들을 제공한다.

<br>

**그렇다면 primitive type은 왜 필요한 걸까? 항상 wrapper type을 사용하면 되는 것 아닐까?**

## primitive type을 사용하는 이유

**메모리 효율**

primitive type은 stack 영역에 값 자체만 저장하지만, wrapper type은 객체이기 때문에 heap 영역에 메모리를 할당해 값을 저장하고, stack 영역에서 값이 저장된 heap 영역의 주소를 가지고 있게 된다.

즉, primitive type은 stack 영역에 값 자체만 저장하기 때문에 메모리를 할당할 필요가 없어 메모리 측면에서 wrapper type보다 좋다.

<br>

**처리 속도**

primitive type은 객체를 생성하는 비용이 들지 않고, 메모리를 할당하거나 가비지 컬렉션등의 관리가 필요하지 않기 때문에 처리 속도가 빠르다.

또한 wrapper type의 경우 값에 접근하려면 stack 영역의 주소를 통해 heap 영역에 접근해서 값에 접근해야 하지만, primitive type의 경우 stack 영역에만 접근하면 되기 때문에 더 빠르다.

## 실제로 primitive type이 더 빠를까?

https://www.baeldung.com/java-primitives-vs-objects

위 글에서는 primitive type과 wrapper type의 성능을 비교하고 있다.

<br>

마지막 요소를 제외하고 모든 요소가 동일한 500만개의 요소를 가진 배열을 만들고 조회를 수행하는 테스트를 진행했다고 한다.

![image](https://github.com/gusals00/mentoring/assets/87007552/427378b1-d525-484d-91fb-9617f9a4c92d)


결과를 보면, 간단한 연산임에도 불구하고 wrapper type에 대한 연산을 수행하는데 더 많은 시간이 걸리는 것을 확인할 수 있다.

## 결론

primitive type은 메모리 효율과 처리 속도 측면에서 이점을 가지기 때문에 사용된다.

자바에서는 primitive type과 wrapper type 두 가지 방식을 제공함으로써 개발자가 상황에 맞게 선택해서 사용할 수 있도록 해주는 것이라고 생각한다.

- 코틀린에서는 primitive type과 wrapper type을 따로 구분하지 않고 단일 타입으로 사용하고, 컴파일 시 자바의 primitive type이나 wrapper type으로 자동으로 변환해준다고 한다.
