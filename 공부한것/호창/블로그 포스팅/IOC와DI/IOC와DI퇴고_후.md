## 서론

멘토님의 피드백을 바탕으로 [기존 IoC와 DI 관련 글](https://github.com/HoChangSUNG/mentoring/blob/main/%EA%B3%B5%EB%B6%80%ED%95%9C%EA%B2%83/%ED%98%B8%EC%B0%BD/%EB%B8%94%EB%A1%9C%EA%B7%B8%20%ED%8F%AC%EC%8A%A4%ED%8C%85/IOC%EC%99%80DI/IOC%EC%99%80DI%ED%87%B4%EA%B3%A0_%EC%A0%84.md)을 수정하여 다시 작성하였다.  
또한 기존 글에 대한 [멘토님의 피드백과 어떻게 피드백을 반영했는지 깃허브](https://github.com/HoChangSUNG/mentoring/blob/main/%EA%B3%B5%EB%B6%80%ED%95%9C%EA%B2%83/%ED%98%B8%EC%B0%BD/%EB%B8%94%EB%A1%9C%EA%B7%B8%20%ED%8F%AC%EC%8A%A4%ED%8C%85/IOC%EC%99%80DI/IOC%EC%99%80DI%20%EA%B8%80%20%ED%94%BC%EB%93%9C%EB%B0%B1%20%EB%B0%8F%20%EB%B0%98%EC%98%81%EC%82%AC%ED%95%AD.md)에 정리해두었다.  
<BR>
이제 IoC와 DI에 관련된 마틴 파울러의 글 2개를 읽고 DI라는 개념이 어떤 과정을 거쳐 나오게 되었는지를 이야기해보자

**마틴 파울러의 아티클**

- **[Inversion of Control Containers and the Dependency Injection  pattern](https://martinfowler.com/articles/injection.html)**
- **[InversionOfControl](https://martinfowler.com/bliki/InversionOfControl.html)**

글의 순서는 다음과 같다

1. 컴포넌트와 서비스
2. 해결해야 하는 문제 예시
3. 이러한 문제를 해결하기 위한 방법
4. 이 방법을 애플리케이션에 어떻게 적용할지
5. dependency injection의 형태
<BR>

## 컴포넌트와 서비스

- 컴포넌트
    - 컴포넌트 작성자의 제어를 벗어나는 애플리케이션에서 변경 없이 사용되도록 의도된 소프트웨어 덩어리
    - ‘변경 없이’ 라는 말은 구성 요소 작성자가 허용하는 방식으로 컴포넌트를 확장하여 컴포넌트의 동작을 변경할 수 있지만 사용하는 어플리케이션이 컴포넌트의 소스 코드를 변경하지 않는다는 의미이다.
- 서비스
    - 외부 어플리케이션에서 사용된다는 점에서 컴포넌트와 비슷
- 차이점
    - 컴포넌트는 로컬에서 사용될 것으로 예상되고, 서비스는 동기식 또는 비동기식의 일부 원격 인터페이스를 통해 원격으로 사용
<BR>

## native example(해결해야 하는 문제)

이제  해결해야 할 주요 문제를 알아보자

**코드 1**

```java
class MovieLister...
  public Movie[] moviesDirectedBy(String arg) {
      List allMovies = finder.findAll();
      for (Iterator it = allMovies.iterator(); it.hasNext();) {
          Movie movie = (Movie) it.next();
          if (!movie.getDirector().equals(arg)) it.remove();
      }
      return (Movie[]) allMovies.toArray(new Movie[allMovies.size()]);
  }
```

위 코드는 finder 객체가 알고 있는 모든 영화 목록을 반환(`finder.findAll()`)하고 특정 감독이 지시한 항목을 반환하도록 하는 메소드이다.

이때 `movieDirectedBy()` 메소드가 **영화가 저장되는 방식**(finder 구현체)**과 독립적으로 동작**하기를 바란다면 아래와 같이 **finder 객체의 인터페이스를 정의**하여 `MoiveFinder`의 변경 사항이 생겨도 `movieDirectedBy()` 메소드가 변경되지 않고 `MoiveFinder`와 **독립적으로 동작**하게 된다.

```java
public interface MovieFinder {
    List findAll();
}
```

이제 실제 `MovieFinder`의 구체적인 클래스를 생성해주어야 `MovieLister`가 `MovieFinder` 구현체의 메소드를 사용할 수 있다

```java
class MovieLister...

  private MovieFinder finder;
  public MovieLister() {
    finder = new ColonDelimitedMovieFinder("movies1.txt");
  }
```

위 코드처럼 생성자에 구체적인 클래스를 생성한다. 또한 아래 그림은 현재 의존 관계를 표현한 것이다
![Untitled (1)](https://github.com/HoChangSUNG/mentoring/assets/76422685/9e9df579-1444-45fa-8d8a-8299c27e7716)  
만약 `MovieFinder`가 영화를 찾을 때 텍스트 파일에서 찾는 것이 아니라 db, xml 파일에서 데이터를 가져오려면 **다른 적절한 MovieFinder 인터페이스 구현체를 얻기 위해 기존 MoiveLister 코드를 수정해야 한다.**  
기존 코드를 수정하지 않고, 다른 구현 방법을 얻기 위해서는 위의 의존 관계처럼 인터페이스와 구현체 둘 다에 의존하면 안된다

즉, **상황에 따라 적절한 구현체를 얻기 위해서 인터페이스에만 의존하고 인터페이스 구현체에는 의존하지 않도록 하는 방법이 필요**하다.
<BR>
<BR>

## 문제 해결 방법은 무엇일까?

위에서 말한 적절한 구현 클래스를 얻기 위한 방법으로 마틴 파울러는 **플러그인 방식**이 있다고 한다.  
**플러그인 방식**은 아래 사진과 같이 **컴파일 시점이 아닌 구성하는 시점(런타임)에 사용할 클래스(인터페이스 구현체)를 연결**해주는 것이다.  
![Untitled (2)](https://github.com/HoChangSUNG/mentoring/assets/76422685/23802a71-d1fa-427b-ad4b-9ce33e648481)  
이렇게  `MovieLister` 클래스가 구현 클래스를 알지 못하지만 작업을 수행하기 위해 특정 구현체와 통신할 수 있도록 링크를 만드는 방법이 플러그인이다.
<BR><BR>

## 문제 해결 방법을 어떻게 어플리케이션에 적용해야 할까?

**플러그인 방식**을 애플리케이션에 적용시킬 수 있는 방법은 **IoC**를 사용하는 것이다.  
**IoC**는 동작의 제어권이 내가 작성한 애플리케이션 코드에 있지 않는 것, 즉 제어 권한이 나에게 있지 않고, 나를 호출하는 것을 말한다.  
여기서 **중요한 점은 IoC를 사용하여 어떤 측면의 제어를 뒤집고 있는지** 여부이다.  

**IoC를 사용하여 플러그인을 찾는 방법을 역전**한 것이다.  
코드 1에서는 `MovieLister`가 직접 `MovieFinder`의 구현체를 인스턴스화하여 컴파일 시점에 사용할 클래스를 연결했다  
하지만 IOC를 사용하여 플러그인을 MovieLister가 찾는 것이 아니라 **별도의 어셈블러 모듈이 이를 찾도록 하여 플러그인을 찾는 방법의 측면을 역전**해준 것이다.  

이렇게 **분리되어 있는 어셈블러 모듈이 플러그인 사용자에게 플러그인 구현체를 주입할 수 있도록 허용하는 일부 규칙을 따르게 하는 것**을 마틴 파울러는 **dependency injection**이라고 말한다.

<BR>
<BR>

## dependency injection의 형태

dependency injection의 기본 아이디어는 finder 인터페이스에 대한 적절한 구현체를 별도의 객체인 assembler를 이용하여 finder 인터페이스에 채워주고 아래와 같은 형태로 종속성 다이어그램을 만드는 것이다.

![Untitled (3)](https://github.com/HoChangSUNG/mentoring/assets/76422685/af79972d-4b58-4a4d-bf0b-b5f6a0b71354)

dependency injection은 생성자 방식, setter 방식, 인터페이스 방식이 있다.

## **정리 및 생각**

### **정리**

- 각 객체가 독립적으로 작동하고 상황에 따라 다른 구현체를 얻기 위해서 인터페이스에만 의존하고 인터페이스 구현체에는 의존하지 않아야 한다.  
- 위 문제를 해결하기 위해 IoC 방식을 사용하여 플러그인 방식을 애플리케이션에 적용했다.  
- IoC에서 **어셈블러(assembler)** 를 사용하여 런타임 시 플러그인 구현체를 결정한다.  
    즉,  **플러그인을 찾는 방법을 역전시켜 어셈블러가 구현체를 결정하는 방식**으로 동작할 수 있게 해주는 것이다. 
- 이러한 고민의 과정을 거쳐 **분리되어 있는 어셈블러 모듈이 플러그인 사용자에게 플러그인 구현체를 주입할 수 있도록 허용하는 DI**가 등장하게 된 것이다.  
- 이 글에서 제일 중요한 점은 오브젝트의 사용과 구성(객체 생성 방법)을 분리하는 것이다.  

### **생각**

- 한글이 아닌 외국어로 작성되어 있던 글이기 때문에 번역하는데 어려움이 있고, 마틴 파울러의 글을 지금은 완전히 이해하지는 못하지만, 시간이 지나고 다시 이 글들을 읽었을 때 조금은 더 발전했을 거라고 생각한다.
- 이 글에서 di가 필요하게 된 계기는 오브젝트의 사용과 구성(객체 생성 방법)을 분리하기 위한 방법을 제공하기 위해 나타났고, 오브젝트의 사용과 구성을 분리한다는 점이 가장 핵심이라고 생각하게 되었다  
    그 이유는 사용과 구성을 분리하지 않으면 하나의 오브젝트에 두 개의 관심사가 존재하기 때문에 오브젝트가 다른 오브젝트의 변경이나 확장이 발생했을 때 결합도가 높아 독립적으로 변경이 불가능하고, 연관관계를 맺고 있는 객체에 영향을 미치기 때문이다.
