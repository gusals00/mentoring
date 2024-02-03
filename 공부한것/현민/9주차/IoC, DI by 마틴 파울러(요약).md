# **Inversion of Control Containers and the Dependency Injection pattern**

## Components and Service

- 컴포넌트
    - 애플리케이션에서 사용될 목적으로 만들어진 소프트웨어의 구성 요소.
    - 컴포넌트의 기능을 확장하려는 목적이 아닌 이상 애플리케이션은 컴포넌트의 소스코드를 수정하지 않으며, 확장하더라도 컴포넌트 작성자가 허용한 방식으로만 확장한다.
- 서비스
    - 외부 애플리케이션에 의해서 사용된다는 점에서 컴포넌트와 유사하다.
    - 컴포넌트는 로컬에서 사용되지만, 서비스는 지정한 원격 인터페이스를 통해 원격으로 동기/비동기 방식으로 사용된다는 차이가 있다.

## A Naive Example

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

이 예제에서는 특정 감독이 제작한 영화 목록을 제공하는 컴포넌트를 작성한다.

여기서 살펴보고자 하는 핵심 내용은 **finder 객체와 MovieLister 객체를 어떻게 연결할까** 하는 점이다.

MovieLister 객체는 영화들이 어떻게 저장되어있는지 알 필요없이 독립적인 메소드가 되길 원한다. 즉, **finder 객체와의 의존성을 없애고 싶은 것**이다.

문제를 해결하기 위해 finder 객체를 MovieFinder 인터페이스로 정의했다.

```java
public interface MovieFinder {
	List findAll();
}
```

이렇게 함으로써 MovieLister와 MovieFinder의 결합도가 낮아졌지만, 실제 Movie 목록을 구하기 위해 MovieFinder의 구현 클래스를 알아야 한다.

```java
class MovieLister...
  private MovieFinder finder;
  public MovieLister() {
    finder = new ColonDelimitedMovieFinder("movies1.txt"); // 콤마로 구분된 영화 정보를 담고 있는 파일로부터 영화 목록을 읽어온다.
  }
```

코드를 혼자 사용하기에는 괜찮지만, 다른 사람이 클래스를 사용하고 싶어한다면 어떻게 될까?

- 콤마로 구분된 텍스트 파일이 아닌 데이터베이스, XML 파일, 다른 형식의 텍스트 파일 등으로부터 영화 목록을 가져올 수 없다.

![image](https://github.com/gusals00/mentoring/assets/87007552/a565395d-f58c-4f8f-942e-cfe8f9f1af40)


또한 위 그림과 같이 MovieLister가 **MovieFinder 인터페이스와 그 구현 클래스에 모두 의존**하게 된다.

원하는 것은 인터페이스에만 의존하고, 실제 구현 클래스에는 의존하지 않는 것인데 어떻게 해야할까?

- Plugin 패턴으로 해결 할 수 있다.
    - 애플리케이션 코드를 여러 런타임 환경에서 특정 동작에 따른 구현을 적용해 실행해야 하는 경우 인터페이스와 팩토리 메소드를 활용하여 작성할 수 있다.
    - 아래 그림에서 DomainObject 객체가 기본키를 생성해야 할 때 테스트 환경에서는 in-memory counter를 사용하고 Production 환경에서는 database-managed sequence를 사용해야 한다고 가정하자.
    - IdGenerator를 제공하는 팩토리 메소드를 직접 구현한다면 조건문을 사용해서 환경에 따라 다른 IdGenerator 구현체를 일일이 반환해야 할 것이다.
        
        ![image](https://github.com/gusals00/mentoring/assets/87007552/4a0c4103-838f-4866-a062-7aff2bfdfe1d)

        
    - Plugin 패턴은 컴파일이 아닌 런타임에 클래스를 연결함으로써 이런 문제를 해결한다.

이제 문제는 이 플러그인을 어떻게 애플리케이션에 조립해서 넣냐는 것이다. 이것은 경량 컨테이너들이 직면한 주요 문제 중 하나이며, 보편적으로 **IoC**를 사용함으로써 해결할 수 있다.

## IoC(Inversion Of Control)

**IoC가 무엇일까?**

사용자로부터 몇가지 정보를 얻는 커멘드라인 프로그램을 작성한다고 생각해보자.

```ruby
#ruby
  puts 'What is your name?'
  name = gets
  process_name(name)
  puts 'What is your quest?'
  quest = gets
  process_quest(quest)
```

- 여기서 내가 작성한 코드는 제어권을 가지고 있다.
- 언제 질문을 할 것인지? 응답을 언제 읽을 것인지? 결과들을 언제 처리할 것인지? 를 결정한다.

하지만 같을 일을 하기 위해 윈도우 시스템을 사용한다면 다음과 같을 것이다.

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

위 두 프로그램은 제어의 흐름에 있어서 큰 차이점을 가지고 있다. 특히 process_name과 process_quest를 호출하는 시점에 대한 제어가 다르다.

커멘드라인 프로그램의 경우 메소드들이 호출되는 시점을 직접 제어했지만, 윈도우 프로그램은 직접 제어하지 않고 Tk.mainloop() 를 사용하여 윈도우 시스템에게 제어권을 넘겨주었다.

윈도우 시스템은 내가 폼을 생성할 때 만든 바인딩을 이용해 내 메소드들을 호출할 시점을 결정한다. 즉, **제어가 역전된 것이다.** 내가 프레임워크를 호출하는 것이 아니라 프레임워크가 나를 호출하는 것이다.  이 현상을 **IoC**라고 한다.

---

경량 컨테이너에서 IoC가 적용되는 부분은 컨테이너가 어떻게 플러그인 구현체를 검색하는지에 대한 것이다.

앞의 예제에서 MovieLister는 finder 구현체를 찾기 위해 구현 클래스의 인스턴스를 생성했다. 이것은 finder를 플러그인될 수 없게 만든다. 컨테이너들은 별도의 조립 모듈에서 MovieLister에 finder 구현체를 연결할 수 있도록 함으로써 어떤 사용자든지 지정된 방식으로 플러그인 할 수 있도록 해야 한다.

IoC는 너무 범용적인 용어이기 때문에 사람들이 혼동할 가능성이 있어 여러 사람들과 논의한 끝에 DI(Dependency Injection)이라는 이름을 만들어냈다.

## Dependency Injection

DI의 기본 아이디어는 객체들을 연결해주는 별도의 객체를 갖는 것이다. 이 조립 객체는 MovieLister 클래스에 알맞은 MovieFinder 구현체를 할당해준다.

![image](https://github.com/gusals00/mentoring/assets/87007552/a4f1b8b6-13de-4a97-82a7-c2cd478e7e3a)


DI에는 생성자 주입, Setter 주입, 인터페이스 주입이 있다.

어떤 방식을 사용하는게 좋을까?

- 인터페이스 주입의 경우 다수의 인터페이스를 작성해야 하기 때문에 컴포넌트 코드에 직접적인 영향을 미친다. 컴포넌트와 의존관계를 조립하기 위해 필요한 작업이 많기 때문에 인터페이스 보다는 생성자와 Setter 방식을 사용한다.
- 생성자 주입의 장점은 setter 메소드를 제공하지 않으므로써 필드를 불변 값으로 지정할 수 있다는 것이다. setter 메소드를 사용해서 주입을 한다면, 이후에 setter 메소드가 임의로 호출되는것 때문에 문제를 겪을 수 있다.
- 따라서 생성자 주입으로 시작하는게 좋고, 이후에 문제가 발생하면 그때 Setter 주입으로 전환하는 것이 좋다고 한다.
