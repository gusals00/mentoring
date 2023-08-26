> 마틴 파울러의 IoC, DI 관련 글을 읽고 정리한 글이다.
>> https://martinfowler.com/articles/injection.html, https://martinfowler.com/bliki/InversionOfControl.html

# **Inversion of Control Containers and the Dependency Injection pattern**

오픈 소스들에서 복잡한 J2EE 기술에 대한 반작용으로 J2EE 기술을 대신할 수 있는 새로운 것들을 만들어내고 있는 다양한 움직임이 있다.

이들이 다루는 공통적인 이슈 중 하나는 데이터베이스 인터페이스와 함께 작동하는 웹 컨트롤러 아키텍쳐를 서로 다른 팀이 만들었을 때 두 제품을 어떻게 결합 하는가 같은, 서로 다른 요소들을 어떻게 연결할 것인가 하는 문제이다.

이런 문제를 해결하기 위해 많은 프레임워크들이 등장했다. 몇몇 프레임워크는 서로 다른 레이어에 있는 컴포넌트들을 조립할 수 있도록 하는 방식을 제공한다. 이런 방식을 경량 컨테이너라고 부르고, PicoContainer이나 Spring 등이 있다.

## Components and Services

여기서 의미하는 **컴포넌트**는 애플리케이션에서 사용될 목적으로 만들어진 소프트웨어의 구성 요소를 의미한다. 컴포넌트의 기능을 확장하려는 목적이 아닌 이상 애플리케이션은 컴포넌트의 소스 코드를 수정하지 않으며, 확장하더라도 컴포넌트 작성자가 허용한 방식으로만 확장한다.

**서비스**도 외부 애플리케이션에 의해서 사용된다는 점에서 컴포넌트와 유사하다. 둘의 가장 큰 차이점은 컴포넌트는 로컬에서(jar 파일, dll, assembly, source import 형태로) 사용되지만, 서비스는 지정한 원격 인터페이스(웹서비스, 메세징 시스템, RPC, 소켓)을 통해 원격으로 동기/비동기 방식으로 사용된다는 것이다.

이 글에서는 서비스를 주로 사용하지만 같은 로직을 컴포넌트에도 동일하게 적용할 수 있다. 원격 서비스에 쉽게 접근하기 위해 로컬 컴포넌트가 필요할 때도 있다. 하지만 매번 ‘컴포넌트 또는 서비스’ 라고 작성할 수 없기 때문에 서비스라는 용어를 사용한다.

## A Naive Example

이 예제에서는 특정 감독이 제작한 영화 목록을 제공하는 컴포넌트를 작성한다.

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

이 코드는 앞으로 수정하지 않을 것이며, 이 코드를 기반으로 해서 이 글에서 살펴보고자 하는 실제 내용을 알아볼 것이다.

이 글에서 살펴보고자 하는 핵심 내용은 이 finder 객체를 어떻게 MovieLister 객체와 연결할까 하는 점이다. moviesDirectedBy 메소드가 영화들이 어떻게 저장되어있는지 알 필요 없이 독립적인 메소드가 되길 원하기 때문이다. 따라서 finder 객체를 참조하는 모든 메소드는 finder 객체의 findAll 메소드를 사용하는 방법을 알아야 한다.

finder를 인터페이스로 정의함으로써 해결할 수 있다.

```java
public interface MovieFinder {
	List findAll();
}
```

이렇게 함으로써 MovieLister와 MovieFinder의 결합도가 낮아졌지만, 실제 Movie 목록을 구하기 위해 MovieFinder 구현 클래스를 알아야 한다. MovieLister 생성자에 다음과 같이 코드를 작성해서 구현 클래스를 명시할 수 있을 것이다.

```java
class MovieLister...
  private MovieFinder finder;
  public MovieLister() {
    finder = new ColonDelimitedMovieFinder("movies1.txt"); // 콤마로 구분된 영화 정보를 담고 있는 파일로부터 영화 목록을 읽어온다.
  }
```

위 코드를 혼자 사용하기에는 괜찮지만, 친구가 이 클래스를 갖고 싶어한다면 어떻게 될까? 친구가 갖고 있는 영화 목록이 콤마를 사용해서 영화를 구분한다면 “movies1.txt”를 수정해서 사용하면 괜찮을 것이다. 하지만 파일이 아닌 데이터베이스, XML 파일, 웹 서비스 또는 다른 형식의 텍스트 파일로부터 영화 목록을 가져와야 한다면 어떻게 될까?

이 경우 데이터를 읽어오기 위해서는 ColonDelimitedMovieFinder가 아니라 다른 클래스가 필요할 것이다. MovieFiner를 인터페이스로 정의해 두었기 때문에 moviesDirectedBy 메소드는 변경할 필요가 없다.

![image](https://github.com/gusals00/mentoring/assets/87007552/853c79c0-acb5-47ce-bec3-21613114e90d)


위 그림은 이런 상황에 해당하는 의존성을 보여주고 있다. MovieLister 클래스는 MovieFinder 인터페이스와 그 구현 클래스에 모두 의존하고 있다. 원하는 것은 인터페이스에만 의존하고, 실제 구현 클래스에는 의존하지 않는 것인데 어떻게 해야 할까?

P of EEA 책에서는 이 상황을 Plugin 패턴으로 설명하고 있다. MovieLister 클래스를 사용하길 원했던 친구가 어떤 구현 클래스를 사용할 지 모르기 때문에, finder를 위한 구현 클래스는 컴파일 타임에 연결되지 않는다. 대신 MovieLister 클래스 작성자와 관계없이, 런타임에 사용할 구현 클래스를 플러그인 할 수 있다.

문제는 MovieLister 클래스가 컴파일 타임에 MovieFinder 구현 클래스를 알 필요가 없으면서, 런타임에 MovieLister 클래스와 MovieFinder 구현 클래스를 어떻게 연결하냐는 것이다.

이제 핵심 문제는 이 플러그인을 어떻게 애플리케이션에 조립해서 넣냐는 것이다. 이것은 경량 컨테이너들이 직면한 주요 문제 중 하나이며, 보편적으로 IoC를 사용함으로써 해결할 수 있다.

## IoC(Inversion Of Control)

경량 컨테이너들이 IoC를 구현했기 때문에 유용하다고 했을 때, 매우 당황스러웠다. IoC는 프레임워크들의 공통된 특징이라서, 경량 컨테이너가 IoC를 갖고 있기 때문에 특별하다고 말하는 것은 자동차가 바퀴를 갖고 있기 때문에 특별하다고 말하는 것과 같기 때문이다.

제어(Control)의 관점이 역전(Inversion)된다는 것이 무엇일까? 처음 IoC를 접할 때, 그것은 유저 인터페이스와 관련된 것이었다. 예전의 유저 인터페이스는 애플리케이션 프로그램에 의해 제어되었다. 프로그램은 이름을 입력하세요 같은 명령어를 순차적으로 출력해서 데이터를 입력하도록 유도한 뒤 응답을 기다렸다. GUI를 사용함으로써, UI 프레임워크가 이런 루프를 포함하게 되었으며 프로그램은 화면에 있는 다양한 필드를 위한 이벤트 핸들러를 제공하는 형태로 바뀌었다. 프로그램의 주요 제어권이 사용자에서 프레임워크로 역전된 것이다.

컨테이너에서 IoC가 적용되는 부분은 컨테이너가 어떻게 플러그인 구현체를 검색하는지에 대한 것이다. 앞의 예제에서 MovieLister는 finder 구현체를 찾기 위해 구현 클래스의 인스턴스를 생성했다. 이것은 finder를 플러그인될 수 없게 만든다. 이 컨테이너들은 별도의 조립 모듈에서 MovieLister에 finder 구현체를 연결할 수 있도록 함으로써 어떤 사용자든지 지정된 방식으로 플러그인 할 수 있도록 해야 한다.

IoC는 너무 범용적인 용어이기 때문에 사람들이 혼동할 가능성이 있기 때문에 여러 사람들과 논의한 끝에 DI(Dependency Injection)이라는 이름을 만들어냈다.

먼저 DI의 다양한 형태에 대해 살펴볼 것이다. 하지만 DI가 애플리케이션 클래스와 플러그인 구현체 사이에 의존성을 없애는 유일한 방법은 아니라는 것을 알아야 한다.

## Forms of Dependency Injection

DI의 기본 아이디어는 객체들을 연결해주는 별도의 객체를 갖는 것이다. 이 조립 객체는 MovieLister 클래스에 알맞은 MovieFinder 구현체를 할당해준다.

![image](https://github.com/gusals00/mentoring/assets/87007552/1d12da44-dbeb-4c93-bcee-b75b257bdf2f)


DI에는 세 종류가 있다. 생성자 주입, Setter 주입, 인터페이스 주입이다.

### Constructor Injection With PicoContainer

경량 컨테이너인 PicoContainer에서 어떻게 생성자 주입을 사용하는지 살펴보자.

PicoContainer는 MoveLister 클래스에 MovieFinder구현체를 주입하기 위해 생성자를 사용한다. 

이를 위해 MoveLister 클래스의 생성자는 전달받은 구현체를 위한 파라미터를 제공해야 한다.

```java
class MovieLister...
  public MovieLister(MovieFinder finder) {
      this.finder = finder;       
  }
```

finder 자체도 PicoContainer에 의해 관리되며, PicoContainer가 ColonMovieFinder에 텍스트 파일 이름을 전달한다.

```java
class ColonMovieFinder...
  public ColonMovieFinder(String filename) {
      this.filename = filename;
  }
```

PicoContainer는 각각의 인터페이스가 어떤 구현 클래스와 연관되는지 그리고 ColonMovieFinder 생성자에 전달될 String값이 무엇인지 알 필요가 있다.

```java
private MutablePicoContainer configureContainer() {
    MutablePicoContainer pico = new DefaultPicoContainer();
    Parameter[] finderParams =  {new ConstantParameter("movies1.txt")};
    pico.registerComponentImplementation(MovieFinder.class, ColonMovieFinder.class, finderParams);
    pico.registerComponentImplementation(MovieLister.class);
    return pico;
}
```

이 설정 관련 코드는 보통 다른 클래스에서 구현된다. 물론 설정 정보를 별도의 설정 파일에 저장하는 것이 일반적이다. 설정 파일에서 정보를 읽어와 컨테이너를 알맞게 설정하는 클래스를 작성할 수도 있을 것이다.

PicoContainer를 사용하기 위해서는 다음과 같이 코드를 작성하면 된다.

```java
public void testWithPico() {
    MutablePicoContainer pico = configureContainer();
    MovieLister lister = (MovieLister) pico.getComponentInstance(MovieLister.class);
    Movie[] movies = lister.moviesDirectedBy("Sergio Leone");
    assertEquals("Once Upon a Time in the West", movies[0].getTitle());
}
```

### Setter Injection with Spring

Spring 프레임워크는 자바 엔터프라이즈 분야에서 널리 사용되는 프레임워크이다. Spring은 트랜잭션, persistence framework, 웹 애플리케이션 개발 그리고 JDBC를 위한 추상 계층을 포함하고 있다. Spring은 PicoContainer와 마찬가지로 생성자 주입과 setter 주입을 모두 제공한다.

MovieLister를 setter 방식으로 작성해보자

```java
class MovieLister...
  private MovieFinder finder;
public void setFinder(MovieFinder finder) {
  this.finder = finder;
}
```

비슷하게 ColonMovieFinder가 참조할 파일명을 입력받는 setter를 정의할 수 있다.

```java
class ColonMovieFinder...
  public void setFilename(String filename) {
      this.filename = filename;
  }
```

세번째 단계는 설정 파일을 작성하는 것이다. Spring은 XML 파일을 통해 설정할 수 있는 기능을 제공하고 있으며 코드에서 직접 설정할 수도 있다. 다음은 설정 정보를 담고 있는 XML 파일의 예이다.

```java
<beans>
        <bean id="MovieLister" class="spring.MovieLister">
            <property name="finder">
                <ref local="MovieFinder"/>
            </property>
        </bean>
        <bean id="MovieFinder" class="spring.ColonMovieFinder">
            <property name="filename">
                <value>movies1.txt</value>
            </property>
        </bean>
    </beans>
```

테스트 코드는 다음과 같다

```java
public void testWithSpring() throws Exception {
    ApplicationContext ctx = new FileSystemXmlApplicationContext("spring.xml");
    MovieLister lister = (MovieLister) ctx.getBean("MovieLister");
    Movie[] movies = lister.moviesDirectedBy("Sergio Leone");
    assertEquals("Once Upon a Time in the West", movies[0].getTitle());
}
```

### Interface Injection

세번째 방식은 인터페이스를 사용하는 방식이다. 인터페이스를 사용하는 프레임워크에는 Avalon을 예로 들 수 있다. 인터페이스 방식을 사용하려면 먼저 주입에 사용할 인터페이스를 정의해야 한다. 

아래는 MovieFinder를 객체에 주입하기 위한 인터페이스이다.

```java
public interface InjectFinder {
    void injectFinder(MovieFinder finder);
}
```

이 인터페이스를 통해서 MovieFinder 구현체를 누구에게나 제공할 수 있다. 예를 들어 MovieLister와 같이 finder를 사용하고자 하는 클래스들은 이 인터페이스를 구현하면 된다.

```java
class MovieLister implements InjectFinder
  public void injectFinder(MovieFinder finder) {
      this.finder = finder;
  }
```

MovieFinder 구현 클래스에 파일 이름을 전달할 때도 같은 방식을 사용한다.

```java
public interface InjectFinderFilename {
    void injectFilename (String filename);
}

class ColonMovieFinder implements MovieFinder, InjectFinderFilename...
  public void injectFilename(String filename) {
      this.filename = filename;
  }
```

보통 구현체를 연결하기 위한 설정 코드를 필요로 한다. 간단하게 설정 정보를 작성하였다.

```java
class Tester...

  private Container container;

   private void configureContainer() {
     container = new Container();
     registerComponents();
     registerInjectors();
     container.start();
  }
```

위 설정은 2단계를 거치는데 1단계에서는 키값을 사용하여 컴포넌트를 등록한다.

```java
class Tester...
  private void registerComponents() {
    container.registerComponent("MovieLister", MovieLister.class);
    container.registerComponent("MovieFinder", ColonMovieFinder.class);
  }
```

2단계는 의존하는 컴포넌트를 주입하기 위한 인젝터를 등록하는 과정이다. 각 의존하는 객체를 주입하는 코드를 필요로 한다. 여기에서는 컨테이너에 인젝터 객체를 등록하는 방식을 사용했다. 각 인젝터 객체는 Injector 인터페이스를 구현한다.

```java
class Tester...
  private void registerInjectors() {
    container.registerInjector(InjectFinder.class, container.lookup("MovieFinder"));
    container.registerInjector(InjectFinderFilename.class, new FinderFilenameInjector());
  }

public interface Injector {
  public void inject(Object target);

}
```

만약 의존하는 코드가 이 컨테이너를 위해 작성된 클래스라면, 해당 컴포넌트가 injector 인터페이스를 구현하게 하면 된다. 이 글에서 MovieFinder가 이런 경우에 해당한다. String같은 범용적인 클래스는 설정 코드 안에서 inner 클래스로 구현하면 된다.

```java
class ColonMovieFinder implements Injector...
  public void inject(Object target) {
    ((InjectFinder) target).injectFinder(this);        
  }

class Tester...
  public static class FinderFilenameInjector implements Injector {
    public void inject(Object target) {
      ((InjectFinderFilename)target).injectFilename("movies1.txt");      
    }
    }
```

컨테이너를 사용하는 테스트 코드는 다음과 같다

```java
class Tester…
  public void testIface() {
    configureContainer();
    MovieLister lister = (MovieLister)container.lookup("MovieLister");
    Movie[] movies = lister.moviesDirectedBy("Sergio Leone");
    assertEquals("Once Upon a Time in the West", movies[0].getTitle());
  }
```

## Using a Service Locator

DI의 주요 장점은 MovieLister 클래스와 MovieFinder 구현체 사이에 의존성을 없앤다는 것이다. MovieLister 클래스를 다른 사람들에게 제공할 수 있었고 그 사람들은 자신에게 맞는 MovieFinder 구현체를 플러그인 할 수 있게 되었다.

DI가 객체 사이에 의존 관계를 없애는 유일한 방법은 아니다. ****서비스 로케이터를 사용하는 방법이 있다.

서비스 로케이터의 기본 아이디어는 애플리케이션이 필요로 하는 모든 서비스를 포함하고 있는 객체를 갖는 것이다. 서비스 로케이터는 위 예제에서는 필요한 MovieFinder를 리턴해주는 메소드를 갖게 될 것이다.  물론 MovieLister가 서비스 로케이터를 참조해야 한다. 

아래는 서비스 로케이터를 사용할 때의 의존관계이다.

![image](https://github.com/gusals00/mentoring/assets/87007552/267f55bf-0bc3-433b-9496-f16c9e70364a)


여기서는 싱글톤 레지스트리를 사용해 ServiceLocator를 구현할 것이다. MovieLister는 ServiceLocator를 이용해  MovieFinder를 얻을 수 있다.

```java
class MovieLister...
  MovieFinder finder = ServiceLocator.movieFinder();

class ServiceLocator...
  public static MovieFinder movieFinder() {
      return soleInstance.movieFinder;
  }
  private static ServiceLocator soleInstance;
  private MovieFinder movieFinder;
```

ServiceLocator를 설정해주어야 한다. 여기서는 코드에서 직접 설정해주는 방식을 사용한다.

```java
class ServiceLocator...
  public static void load(ServiceLocator arg) {
      soleInstance = arg;
  }

  public ServiceLocator(MovieFinder movieFinder) {
      this.movieFinder = movieFinder;
  }
```

테스트 코드이다.

```java
class Tester...
  public void testSimple() {
      configure();
      MovieLister lister = new MovieLister();
      Movie[] movies = lister.moviesDirectedBy("Sergio Leone");
      assertEquals("Once Upon a Time in the West", movies[0].getTitle());
  }
```

### Using a Segregated Interface for the Locator

서비스 로케이터를 사용할 때의 이슈 중 하나는 MovieLister가 서비스 로케이터 클래스에 의존한다는 것이다. 격리된 인터페이스(segregated interface)를 사용하면 이런 의존성을 줄일 수 있다.

전체 서비스 로케이터 인터페이스를 사용하는 대신에 MovieLister가 필요로 하는 인터페이스의 일부만 MovieLister에 선언하는 것이다.

이 상황에서 MovieLister의 제공자는 MovieFinder를 저장하기 위해 필요한 인터페이스를 제공할 수도 있을 것이다.

```java
public interface MovieFinderLocator {
    public MovieFinder movieFinder();
```

서비스 로케이터는 MovieFinder에 접근할 수 잇도록 하기 위해서 MovieFinderLocator를 구현해야한다.

```java
MovieFinderLocator locator = ServiceLocator.locator();
MovieFinder finder = locator.movieFinder();
public static ServiceLocator locator() {
     return soleInstance;
 }
 public MovieFinder movieFinder() {
     return movieFinder;
 }
 private static ServiceLocator soleInstance;
 private MovieFinder movieFinder;
```

인터페이스를 사용하면 더이상 static 메소드를 사용해서 서비스에 접근할 수 없게 되기 때문에(자바8부터 가능하다), 서비스 로케이터의 인스턴스를 구해주는 클래스를 만들고 원하는 서비스에 접근할 때 그 클래스를 사용해야 한다.

### A Dynamic Service Locator

앞의 서비스 로케이터 예제는 정적이었다. 서비스 로케이터는 각각의 서비스마다 접근하기 위한 메소드를 갖고 있다. 이런 정적인 방식 뿐만 아니라 어떤 서비스든지 필요할 때 접근할 수 있도록 하는 동적인 서비스 로케이터를 만들 수 있다.

이 경우에 서비스 로케이터는 각 서비스를 저장하기 위해 맵을 사용하고 서비스를 로딩하고 구하기 위한 메소드를 제공하게 된다.

```java
class ServiceLocator...
  private static ServiceLocator soleInstance;
  public static void load(ServiceLocator arg) {
      soleInstance = arg;
  }
  private Map services = new HashMap();
  public static Object getService(String key){
      return soleInstance.services.get(key);
  }
  public void loadService (String key, Object service) {
      services.put(key, service);
  }
```

서비스를 로딩할 때는 알맞은 키 값을 서비스와 연결한다.

```java
class Tester...
        private void configure() {
            ServiceLocator locator = new ServiceLocator();
            locator.loadService("MovieFinder", new ColonMovieFinder("movies1.txt"));
            ServiceLocator.load(locator);
        }
```

같은 키 값을 사용해 서비스를 구한다.

```java
class MovieLister...
  MovieFinder finder = (MovieFinder) ServiceLocator.getService("MovieFinder");
```

이 방식은 유연하기는 하나 명시적이지 않기 때문에 선호하지 않는다. 이 방식에서 서비스에 접근할 수 있는 방법은 텍스트로 된 키값을 통한 것 뿐이다. 서비스를 찾기 위한 명시적인 메소드가 있는 것을 선호하는데, 왜냐하면 인터페이스에 메소드가 정의되어 있어서 쉽게 서비스를 찾을 수 있기 때문이다.

## Deciding which option to use

### Service Locator vs Dependency Injection

두 패턴은 맨 처음 살펴봤던 예제에서 문제가 되었던 결합도 문제를 없애준다. 두 패턴의 중요한 차이점은 애플리케이션 클래스에 제공되는 방식이 다르다는 점이다. 

서비스 로케이터를 사용할 경우, 애플리케이션 클래스가 서비스를 사용하기 위해서는 서비스 로케이터에 직접적으로 요청하게 된다.

DI를 사용하는 경우 서비스를 사용하기 위한 어떤 요청도 발생하지 않으며 서비스는 애플리케이션 내에 위치하게 된다. 

IoC는 프레임워크의 일반적인 특징인데, 비용과 관련된 문제가 있다. IoC는 이해하기 어렵고 디버깅을 할 때 문제가 되는 경향이 있다. 그래서 IoC가 필요하지 않는 이상 IoC를 사용하지 않는 편이다.

두 방식의 핵심 차이점은 서비스 로케이터를 사용할 경우 서비스의 모든 사용자가 로케이터에 의존한다는 것이다. 로케이터가 다른 구현체와의 의존성을 숨길 수는 있지만, 애플리케이션은 로케이터에 접근할 수 있어야 한다.

따라서, 의존성이 문제가 되는지에 따라서 로케이터와 DI를 선택할 수 있다.

DI를 사용하다 보면 쉽게 컴포넌트 사이의 의존관계를 확인할 수 있다. DI를 사용할 경우 생성자 같은 인젝션 메커니즘을 조사해서 의존관계를 이해할 수 있다. 서비스 로케이터를 사용할 경우, 로케이터를 호출하는 소스코드를 검색해야 의존관계를 판별할 수 있다.

두가지중 어떤 것을 사용하는지는 서비스를 어떻게 사용하는지에 달렸다. 서비스를 사용하는 다양한 클래스로 구성된 애플리케이션을 구축한다면 애플리케이션 클래스에서 서비스 로케이터로 의존성을 넘기는 것은 문제가 되지 않는다.

사용자들이 DI를 선호하는 이유는 테스트하기가 더 쉽기 때문이다. 여기서 핵심은 테스트를 수행한다는 것이다. 실제 서비스 구현체 대신에 stub이나 mock을 사용한 테스트를 쉽게 수행할 수 있다. 하지만 테스트와 관련해서 실제로는 DI와 서비스 로케이터 사이에 아무런 차이가 없다.

### Constructor vs Setter Injection

인터페이스 인젝션의 경우 다수의 인터페이스를 작성해야 하기 때문에 컴포넌트 코드에 직접적인 영향을 미친다. 컴포넌트와 의존관계를 조립하기 위해 필요한 작업이 많기 때문에 인터페이스 보다는 생성자와 Setter 방식을 사용한다.

생성자와 Setter 중 어떤 것을 선택하느냐의 문제는 생성자 또는 Setter 메소드에서 필드의 값을 채우느냐의 문제와 관련되기 때문에 객체지향 관점에서 생각해봐야 한다.

마틴 파울러는 가능한 객체 생성 시점에 유효한 객체를 생성하고 있다. 생성자에 파라미터를 지정함으로써 유효한 객체를 만들기 위해 무엇이 필요한지 명확하게 알 수 있게 된다. 만약 객체를 생성하는 방법이 여러개 존재한다면 그에 생성하는 여러개의 생성자를 작성하면 된다.

생성자의 초기화의 장점은 setter 메소드를 제공하지 않음으로써 필드를 불변 값으로 지정할 수 있다는 것이다. setter 메소드를 사용해서 초기화를 한다면, 이후에 setter 메소드가 임의로 호출되는 것 때문에 문제를 겪을수도 있다.

하지만 생성자가 항상 좋은것은 아니다. 생성자의 파라미터가 많을 경우 복잡해 보일 수 있고, 생성자가 복잡하고 길다는 것은 클래스가 과도하게 사용된다는 것을 의미하며, 이런 경우 클래스를 여러개로 분리하는 것을 고려해봐야 한다.

그래도 생성자 주입으로 시작하는 것이 좋고, 이후에 문제가 발생하면 그때 Setter 주입으로 전환하는 것이 좋다.

### Separating Configuration from Use

서비스의 구성을 서비스의 사용으로부터 구분하는 것은 중요한 문제이다.

실제로 이것은 구현으로부터 인터페이스를 분리하는 것과 함께 근본적인 디자인의 중요 사항이다.

조건 로직이 어떤 클래스를 인스턴스화 할지를 결정한 뒤 그 조건을 평가할 때 중복된 조건 코드가 아닌 다형성을 통해 수행하는 OOP에서 볼 수 있는 것들이다.

이러한 분리가 한 코드 기반에서 유용하다면, 그것은 컴포넌트나 서비스같은 외부 요소를 사용할 때 특히 중요하다.

만약 특정 환경과 관련된 구현 클래스의 선택을 뒤로 미루고 싶다면 플러그인을 구현해야 한다. 플러그인을 사용하면 플러그인을 조립하는 것은 애플리케이션의 나머지 부분과 독립적으로 실행되며, 따라서 다른 환경을 위해서 구성을 쉽게 교체할 수 있다.

구성 메커니즘은 서비스 로케이터를 사용하거나 직접 객체를 설정하기 위해 인젝션을 사용할 수 있다.

## Concluding Thoughts

경량 컨테이너들은 서비스를 조립하기 위해 공통적으로 DI를 사용하고 있다. DI는 서비스 로케이터를 대체하는 유용한 패턴이다.

서비스 로케이터와 DI 중 어떤것을 선택하는 것 보다 더 중요한 것은 서비스의 사용과 구성을 어떻게 구분할 것인지에 대한 것이다.

# **InversionOfControl**

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

여기서 내가 작성한 코드는 제어권을 가지고 있다. 

언제 질문을 할 것인지? 응답을 언제 읽을 것인지? 결과들을 언제 처리할 것인지? 를 결정한다.

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

윈도우 시스템은 내가 폼을 생성할 때 만든 바인딩을 이용해 내 메소드들을 호출할 시점을 결정한다. 즉, 제어가 역전된 것이다. 내가 프레임워크를 호출하는 것이 아니라 프레임워크가 나를 호출하는 것이다.  이 현상을 IoC라고 한다.

프레임워크의 가장 큰 특징은 사용자가 프레임워크를 사용하기 위해 만든 메소드들이 사용자 애플리케이션 코드에서 호출되는 것보다 프레임워크에서 호출되는 것이 더 자주 일어난다는 것이다. 

프레임워크는 애플리케이션의 활동을 조합하고 순차적으로 수행하는 메인 프로그램의 역할을 수행한다. 이러한 제어의 역전이 프레임워크가 확장가능한 뼈대로서의 기능을 수행할 수 있게 해준다. 사용자는 프레임워크가 정의한 일반적인 알고리즘을 확장하여 특정 애플리케이션을 위한 메소드를 생성한다.

IoC는 프레임워크를 라이브러리와 구분짓게 만드는 핵심이다. 라이브러리는 본질적으로 내가 호출할 수 있는 기능들의 집합이다. 한번 호출되면 작업을 수행하고 클라이언트에게 다시 제어권을 넘긴다.

프레임워크는 일부 추상적인 설계를 가지고 있으며, 미리 정의된 행동 방식을 가지고 있다. 프레임워크를 사용하기 위해서는 프레임워크가 제공하는 클래스를 상속하거나 작성한 클래스를 프레임워크에 삽입함으로써 프레임워크에 존재하는 확장 지점에 행동 방식을 삽입해야 한다. 그러면 프레임워크가 그 지점에서 내 코드를 호출할 것이다.

IoC의 좋은 예는 템플릿 메소드이다.

부모클래스는 제어의 흐름을 정의하고, 자식클래스는 메소드를 재정의 하거나 추상메소드를 구현함으로써 확장할 수 있다.

# 생각

영어로 된 글이라 읽는데 힘들긴 했지만 IoC와 DI에 대해 어느정도 이해할 수 있게 된 것 같다. 

클래스 사이의 의존성을 없애는 방법이 DI가 유일한 것이 아니라 서비스 로케이터 같은 방법도 존재한다는 것을 알게 되었고, 결국 DI의 의도는 객체의 생성과 사용의 관심을 분리하기 위한 것이고, 그렇게 하기 위해서 객체를 컴파일 타임에 연결 하는 게 아니라 런타임에 연결할 수 있도록 하는 것이라고 생각한다.
