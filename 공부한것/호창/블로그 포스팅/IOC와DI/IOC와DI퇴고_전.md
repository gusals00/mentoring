# 마틴 파울러의 글을 통해 IOC와 DI를 공부해보자

## 서론

ioc가 무엇인지, ioc와 di가 어떤 관계가 있고 어떤 과정을 거쳐 di라는 용어가 나오게 되었는지 마틴 파울러가 작성한 아래의 2개의 글을 토대로 얘기하고 내 생각을 정리해보려고 한다.

**[Inversion of Control Containers and the Dependency Injection  pattern](https://martinfowler.com/articles/injection.html)**

**[InversionOfControl](https://martinfowler.com/bliki/InversionOfControl.html)**

## Inversion of Control

### 설명

마틴 파울러는 ioc를 설명하기 위해 아래 두 가지 방식의 차이를 통해 ioc에 대해 설명하고 있다.

```ruby
#ruby
puts 'What is your name?'
name = gets
process_name(name)
puts 'What is your quest?'
quest = gets
process_quest(quest)
```

- 위 코드에서는 내가 작성한 코드가 제어권을 가진다.
- 또한 내가 작성한 코드가 언제 질문을 하고, 언제 응답을 읽고, 그 결과를 처리할지를 결정한다.

아래 윈도우 시스템을 사용하여 위와 같은 작업을 수행하려면 위와는 다른 제어 흐름이 나타난다

```ruby
require 'tk'
  root = TkRoot.new()
  name_label = TkLabel.new() {text "What is Your Name?"}
  name_label.pack
  name = TkEntry.new(root).pack
  name.bind("FocusOut") {process_name(name)}
  quest_label = TkLabel.new() {text "What is Your Quest?"}
  quest_label.pack
  quest = TkEntry.new(root).pack
  quest.bind("FocusOut") {process_quest(quest)}
  Tk.mainloop()
```

- Tk.mainloop() 를 호출하여 코드에 제한 제어권을 윈도우 시스템에 넘겨, 내가 작성한 코드가 process_name(name), process_quest(quest)를 언제 호출하는지 시기를 제어하지 않는다.
- 대신 윈도우 시스템에서 폼을 만들 때 만든 바인딩을 기반으로 내 메서드를 호출할 시기를 결정한다. 즉, 제어가 역전되었다.
- **내가 아니라 프레임워크가 나를 호출하는 현상을 제어의 역전 Inversion of Control** 이라고 한다.(Hollywood Principle - Don't call us, we'll call you)

### ioc 예시

ioc의 간단한 예시로는 템플릿 메서드가 존재한다

템플릿 메서드는 슈퍼 클래스에 알고리즘 구조를 작성하고, 서브 클래스에 알고리즘의 특정 step을 재정의하는 패턴이다.

이때 상위 클래스는 제어 흐름을 정의하고, 하위 클래스는 이 재정의 메서드를 확장하거나 추상 메서드를 구현하여 확장을 수행한다.

## 경량 컨테이너와 경량 컨테이너가 직면한 문제, 해결 방법

경량 컨테이너(lightweight container)가 무엇이고, 이 경량 컨테이너들이 당면한 문제들을 어떤 방식으로 해결했는지 해결 과정을 통해 ioc와 di의 대해 알아보자

### **경량 컨테이너는 무엇일까?**

- J2EE의 복잡함을 없애고자 출현했고 공통적인 이슈는 서로 다른 객체들을 어떻게 연결하느냐이다. 예를 들어 웹 컨트롤러 아키텍쳐와 데이터베이스 인터페이스를 다른 팀이 개발할 때. 두 팀이 서로의 코드에 대해 잘 모르는 경우 어떻게 두가지를 연결할 것인가? 라는 문제를 해결하기 위해 다양한 프레임워크들이 등장했다
- 이러한 문제를 해결하기 위한 프레임워크들 중 다른 레이어에 위치하는 컴포넌트를 조립하는 기능을 제공하는 방법을 만들어낸 프레임워크를 경량 컨테이너라고 부른다.
- 경량 컨테이너에는 Spring과 PicoContainer가 해당된다.

### **컴포넌트와 서비스**

- 컴포넌트
    - 컴포넌트 작성자의 제어를 벗어나는 애플리케이션에서 변경 없이 사용되도록 의도된 소프트웨어 덩어리를 의미
    - ‘변경 없이’ 라는 말은 구성 요소 작성자가 허용하는 방식으로 구성요소를 확장하여 구성 요소의 동작을 변경할 수 있지만 사용하는 어플리케이션이 컴포넌트의 소스 코드를 변경하지 않는다는 의미
- 서비스
    - 외부 어플리케이션에서 사용된다는 점에서 컴포넌트와 비슷
- 컴포넌트와 서비스의 차이점
    - 컴포넌트는 로컬에서 사용될 것으로 예상되고, 서비스는 동기식 또는 비동기식의 이룹 원격 인터페이스를 통해 원격으로 사용된다

### **경량 컨테이너에서 해결해야 하는 주요 문제**

이제 이러한 경량 컨테이너에서 해결해야 할 주요 문제를 알아보자

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

- 위 코드는 finder 객체가 알고 있는 모든 영화 목록을 반환(`finder.findAll()`)하고 특정 감독이 지시한 항목을 반환하도록 하는 메소드이다.
- 이때  `movieDirectedBy()` 메소드가 **영화가 저장되는 방식**(finder 구현체)**과 독립적으로 동작**하기를 바란다면 아래와 같이 finder 객체의 인터페이스를 정의하여 `MoiveFinder`의 변경 사항이 생겨도 `movieDirectedBy()` 메소드가 변경되지 않고 `MoiveFinder`와 독립적으로 동작하게 된다.

```java
public interface MovieFinder {
    List findAll();
}
```

- 이제 실제 `MovieFinder`의 구체적인 클래스를 생성해주어야 `MovieLister`가 `MovieFinder` 구현체의 메소드를 사용할 수 있다.

```java
class MovieLister...

  private MovieFinder finder;
  public MovieLister() {
    finder = new ColonDelimitedMovieFinder("movies1.txt");
  }
```

- 위 코드처럼 생성자에 구체적인 클래스를 생성한다.
- 하지만 여기서 문제가 발생한다.만약 `MovieFinder`가 영화를 찾을 때 위 코드처럼 텍스트 파일에서 찾는 것이 아니라 db, xml 파일에서 데이터를 가져오려면 다른 적절한 MovieFinder 인터페이스 구현체를 얻을 수 있는 방법이 필요하다.

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/6656aa90-a106-454d-8758-b23889a5921d/Untitled.png)

위 그림은 위 상황에 해당하는 의존성을 보여준다. `MovieLister` 클래스는 `MovieFinder` 인터페이스와 그 구현 클래스에 의존하고 있다. 

상황에 따라 적절한 구현체를 얻기 위해서 인터페이스에만 의존하고 인터페이스 구현체에는 의존하지 않도록 해야 하는데 어떻게 해야 구현 클래스에는  의존하지 않을 수 있을까?

이런 적절한 인터페이스 구현체를 얻기 위한 방법으로 마틴 파울러는 **플러그인 방식**을 설명한다.

**플러그인 방식**은 아래 사진과 같이 컴파일 시점이 아닌 구성하는 시점(런타임)에 사용할 클래스(인터페이스 구현체)를 연결해주는 것이다.

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/768dcf4f-a4cc-4356-9f37-9715646ddd6d/Untitled.png)

이렇게 플러그인을 사용하여 `MovieLister` 클래스가 구현 클래스를 알지 못하지만 작업을 수행하기 위해 특정 구현체와 통신할 수 있도록 링크를 만드는 방법이 플러그인이다.

따라서 **경량 컨테이너에서 해결해야 하는 핵심 문제는 플러그인을 애플케이션에 어떻게 적용할 것인지 하는 것**이다.

보통 경량 컨테이너들은 보편적으로 **IOC를 사용하여 이러한 문제**들을 해결한다.

### **Inversion of Control을 이용하여 해결(DI)**

이제 이 IOC를 사용하여 이러한 문제들을 해결하는 것들을 알아보자

ioc는 프레임워크에서 공통적으로 사용되는 방식이다. 

따라서 이러한 경량 컨테이너가 ioc를 사용한다고 특별하다는 말은 차가 바퀴를 가지고 있어 특별하다고 말하는 것과 같다.

경량 컨테이너가 ioc를 사용하여 **어떤 측면의 제어를 뒤집고 있는지**가 구현체와 인터페이스를 분리할수 있게 하는 것의 핵심이다.

경량 컨테이너에서 제어의 반전은 **플러그인을 찾는 방법**에 대한 것이다.

코드 1에서는 `MovieLister`가 직접 `MovieFinder`의 구현체를 인스턴스화하여  컴파일 시점에 사용할 클래스를 연결하기 때문에 플러그인 방식을 사용할 수 없다.

따라서 플러그인을 `MovieLister`가 찾는 것이 아니라 별도의 어셈블러 모듈이 찾도록 플러그인을 찾는 방법의 측면을 역전해주는 것이다.

따라서 이러한 경량 컨테이너는 **분리되어 있는 어셈블러 모듈이 플러그인 사용자에게 플러그인 구현체를 주입할 수 있도록 허용하는 일부 규칙을 따르도록 하는 것**이다. 이것을 마틴 파울러는 **dependency injection** 이라고 정했다.

### **dependency injection 형태**

이제 dependency injection의 형태에 대해 알아보자

dependency injection의 기본 아이디어는 finder 인터페이스에 대한 적절한 구현체를 별도의 객체인 assembler를 이용하여 finder 인터페이스에 채워주고 아래와 같은 형태로 종속성 다이어그램을 만드는 것이다.

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/598b6e5a-f63b-4ca2-a2d9-874d11689b35/Untitled.png)

dependency injection은 생성자 방식, setter 방식, 인터페이스 방식이 있다.

### **서비스 사용과 서비스 구성(configuration) 분리하기**

- 서비스의 구성이 서비스의 사용으로부터 분리되어야 한다는 것이 제일 중요한 점이다.

## **정리 및 생각**

### **정리**

- ioc는 동작의 제어권이 작성한 코드에 있지 않고 프레임워크이 내가 작성한 코드에 대한 제어권을 가진다.
- 각 객체가 독립적으로 작동하고 상황에 따라 다른 구현체를 얻기 위해서 인터페이스에만 의존하고 인터페이스 구현체에는 의존하지 않아야 한다.
- 경량 컨테이너에서는 플러그인 방식으로 위 문제를 해결하려 노력했고 이러한 플러그인 방식을 애플리케이션에 적용하기 위해 ioc 방식을 사용했다
- 이런 경량 컨테이너는 플러그인 방식을 구현하기 위해  ioc에서 **어셈블러(assembler)**를 사용하여 런타임시 플러그인 구현체를 결정한다.
    
    즉,  **플러그인을 찾는 방법을 역전시켜 어셈블러가 구현체를 결정하는 방식**으로 동작할 수 있게 해주는 것이다. 
    
- 이러한 고민의 과정을 거쳐 **분리되어 있는 어셈블러 모듈이 플러그인 사용자에게 플러그인 구현체를 주입할 수 있도록 허용하는 DI가 등장하게 된 것이다.**

### **생각**

- 한글이 아닌 외국어로 작성되어 있던 글이기 때문에 번역하는데 어려움이 있고, 마틴 파울러의 글을 지금은 완전히 이해하지는 못하지만, 시간이 지나고 다시 이 글들을 읽었을 때 조금은 더 발전했을 거라고 생각한다.
- 마틴 파울러의 글을 모두 해석하여 정리한 것이 아니라 내가 이해한 부분 위주로 정리했고, 내가 생각했던 DI와 마틴 파울러가 생각한 DI가 어떤 차이가 있을지 생각해보게 되는 계기가 되었다.
- 이 글에서 di가 필요하게 된 계기는 오브젝트의 사용과 구성(객체 생성 방법)을 분리하기 위한 방법을 제공하기 위해 나타났고, 오브젝트의 사용과 구성을 분리한다는 점이 가장 핵심이라고 생각하게 되었다
    
    그 이유는 사용과 구성을 분리하지 않으면 하나의 오브젝트에 두 개의 관심사가 존재하기 때문에 오브젝트가 다른 오브젝트의 변경이나 확장이 발생했을 때 결합도가 높아 독립적으로 변경이 불가능하고, 연관관계를 맺고 있는 객체에 영향을 미치기 때문이다.
    
- 스프링이 ioc를 사용했다고 해서 특별한 것이 아니라 어떤 측면의 제어를 역전해서 DI라는 용어가 나오게 되었는지를 이해하는 것이 중요하다고 생각하게 되었다.