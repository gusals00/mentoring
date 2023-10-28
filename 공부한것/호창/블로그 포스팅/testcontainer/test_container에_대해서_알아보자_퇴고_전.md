# test container에 대해서 알아보자

## 서론

저번에는 db를 통제할 수 있는 방법들에 대해 알아보았고, 이번에는 통합 테스트에서 사용할 수 있는 testcontainer에 대해 알아보자. 

설명할 내용은 다음과 같다

- 왜 test container인가?
- test container란?
- test container의 특징
- test container는 어떻게 멱등성을 유지할까?
- test container의 장단점

<br>

## 왜 test container인가?

통합 테스트를 작성하기 어렵게 만드는 요인들은 다음과 같다.  
- 환경 설정과 관리
- 비결정적 테스트 결과

각 모듈마다 환경 설정 값과 사용 방법이 다를 수 있기 때문에 통합 테스트를 수행하는 개발자는 각 모듈별 환경 설정과 관리 방법을 알아야 한다.  
예를 들어 개발, 테스트, 운영 등의 다양한 환경에서 DBMS의 버전이나 사용자 이름, 비밀번호 등 설정이 환경에 따라 달라져야 한다. **(환경 설정과 관리)**  
<br>

상황에 따라 비결정적인 테스트 결과를 얻을 수도 있다. 예를 들어 개발, 테스트, 운영 환경이 서로 다른 DBMS에서 동일한 쿼리를 사용하더라도 예상치 못한 결과를 가져올 수 있다. 
또한 DB를 이용해 테스트를 실행할 경우 테스트마다 DB의 데이터를 초기화시켜주어야 비결정적인 테스트 결과가 나오지 않는다. **(비결정적 테스트 결과)**  
<br>

위와 같은 **통합 테스트를 작성하기 어렵게 만드는 요인들을 쉽게 해결**해주기 때문에 test container를 이용하는 것이 좋다.  

<br>

## test container란?

통합 테스트를 지원하기 위해 개발된 오픈 소스 Java 라이브러리이다.  
도커 컨테이너를 활용하여 외부 의존성들을 포함한 테스트 환경을 구축하고 관리하는 것을 간편하게 해준다.  
Testcontainers는 다양한 유형의 DBMS를 지원하고, Kafka와 RabbitMQ와 같은 메시지 브로커(혹은 큐)를 포함하여 Nginx 등의 웹 서버도 지원한다.  

## 특징

- Java로 Container를 동작시킬 수 있다.
- 테스트 실행 전/후로 Container를 start, stop할 수 있다 → 테스트 결과의 멱등성을 보장해줄 수 있다.
- 다양한 module을 제공하고 있다.
- 지속적인 version up이 되고 있다.

<br>

## 어떻게 테스트마다 멱등성을 유지하는가?

- Testcontainer의 lifecycle 중 restarted를 이용해 멱등성을 유지할 수 있다.

- **Testcontainers Lifecycle**
    
    ![Untitled (3)](https://github.com/HoChangSUNG/mentoring/assets/76422685/887e7c44-c475-43a8-94f6-85732699d36a)

    - restarted
        - test method가 실행될 때마다 새로운 Container가 시작하는 방식
        - method마다 Container가 실행되고 method가 종료되면 Container가 종료되기 때문에 멱등성을 보장 가능
        - method가 많아질수록 Container의 start, stop의 반복이 많아 테스트 실행 시간이 오래 걸릴 수 있음
    - shared
        - 같은 test class 안에서 method가 수행되면 하나의 Container만 생성해서 같은 test class 내의 test method들이 공유  
            즉, 하나의 테스트 클래스 내에 존재하는 테스트 메서드들울 실행할 때 하나의 Container를 공유
        - method마다 container가 실행되지 않고 공유하기 때문에 데이터 관리가 필요
        - method마다 container가 실행되지 않아 테스트 수행 시간을 줄일 수 있음
    

## 장점

1. 개발자 친화적 : 테스트 코드를 작성하는데 필요한 의존성과 환경 구성을 최소화하여, 개발자가 테스트에 집중할 수 있도록 해준다.
2. 환경 독립성 : 도커 사용으로 로컬 환경, 프로덕션 환경 등에서 동일한 테스트 환경을 구축할 수 있다.
3. 높은 확장성 : 다양한 서비스와 애플리케이션을 지원하여, 필요한 경우 새로운 서비스의 추가, 기존 서비스의 업데이트가 쉽다.
4. 멱등성 : 통합 테스트가 container 내부에서 실행되고, 컨테이너는 life cycle에 따라 자동으로 시작되고 종료되기 때문에 멱등성을 유지해주고, 개발자가 환경을 초기화해줄 필요가 없다.

## 단점
1. 로컬 환경의 리소스 고려가 필요 : 통합 테스트에서 사용하는 모듈에 따라 추가적인 시스템 리소스(메모리, CPU,디스크 공간)가 필요할 수 있고, 동시에 여러 테스트를 실행할 경우 리소스 사용량이 급격하게 증가할 수 있다.
2. 독립적인 테스트 환경이 많아질수록 시간이 오래 걸림 : 컨테이너 시작과 종료를 여러 번 수행해야 하기 때문이다.

<br>

## 결론

- 테스트 컨테이너는 환경에 독립적으로 실행 가능하고, 관련 설정을 쉽게 할 수 있게 한다.
- 제일 중요한 점은 통합 테스트할 때 멱등성을 유지해준다는 점이다.

## 생각
- test container를 어떻게 내 프로젝트에 적용할 수 있을지 생각해보게 되었다.

<br>

사진 출처
- 출처 : [deali 기술 블로그](https://dealicious-inc.github.io/2022/01/10/test-containers.html)

참고
- https://dealicious-inc.github.io/2022/01/10/test-containers.html
- https://dev.gmarket.com/76
