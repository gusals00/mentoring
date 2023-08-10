# CHAPTER17-18 서블릿 기초

# CHAPTER17 서블릿 기초

### 서블릿

- 자바를 사용하여 웹을 만들기 위해 필요한 기술
- HttpServlet 클래스를 상속받고, 처리하고자 하는 HTTP 메서드에 따라 알맞은 메서드를 재정의하여 구현
    - Get 메서드 → `protected void doGet(HttpServletRequest req, HttpServletResponse resp)` 메서드 재정의
    - Post 메서드 → `protected void doPost(HttpServletRequest req, HttpServletResponse resp)` 메서드 재정의
- **서블릿 로딩 과정**
    - 웹 컨테이너가 서블릿 객체를 생성하고 init() 메서드를 호출하는 과정
        - init() 메서드 : 서블릿 생성 후, 초기화 작업을 수행’
    - 서블릿 컨테이너는 서블릿을 최초 요청할 때 객체를 생성하고, 이후 동일한 서블릿에 요청이 오면 이전에 생성한 객체 사용  
        ![KakaoTalk_20230808_174753872](https://github.com/HoChangSUNG/mentoring/assets/76422685/6c48ca72-bc97-448e-88bd-c92f92e96487)
        
        → **서블릿 컨테이너가 서블릿을 싱글톤으로 관리**
        
        원래 서블릿 최초 요청시 서블릿 객체 생성하지만 <load-on-startup> 태그를 설정하여, 웹 어플리케이션 시작 시점에 서블릿을 로딩할 수 있음
        

# CHAPTER18 MVC 패턴 구현

### 모델1
![KakaoTalk_20230808_174342761](https://github.com/HoChangSUNG/mentoring/assets/76422685/aaef0275-4c2d-4cdc-b432-d078170d1d05)

- JSP 페이지에서 비즈니스 로직과 출력 모두를 처리하는 것
- 웹 브라우저의 요청을 받은 JSP는 자바빈이나 서비스 클래스를 사용하여 웹 브라우저가 요청한 작업을 처리하고 그 결과를 클라이언트에 출력
- 단점
    - 비즈니스 로직과 출력 코드가 분리되어 있지 않아 유지보수가 어려움

### 모델2
![KakaoTalk_20230808_174342761_01](https://github.com/HoChangSUNG/mentoring/assets/76422685/15f4dd13-d3bb-4d34-b440-5c904fdbf492)

- 웹 브라우저의 요청을 하나의 서블릿이 받아 비즈니스 로직을 처리하여 결과를 보여줄 JSP 페이지로 포워딩하고 JSP 페이지는 결과 화면을 클라이언트에 전송
- 특징
    - 웹 브라우저의 모든 요청을 단일 진입점(하나의 서블릿)으로 처리

### MVC 패턴

- model, view, controller로 분리
    - model :  비즈니스 로직 처리
    - view : 사용자에게 보여질 화면 출력
    - controller : 사용자의 입력 처리와 흐름 제어
- 구조
    
    ![KakaoTalk_20230808_174342761_02](https://github.com/HoChangSUNG/mentoring/assets/76422685/b28b6656-d20d-4b59-aead-5096a1290eb4)

    - 사용자의 모든 요청을 컨트롤러에 보내고, 컨트롤러는 사용자의 요청에 맞는 모델을 이용해 비즈니스 로직을 수행한 후, 뷰를 통해 결과 화면을 보여준다
- 핵심
    - 비즈니스 로직을 처리하는 모델과 결과 화면을 보여주는 뷰를 분리
    - 애플리케이션 흐름 제어나 사용자의 처리 요청은 컨트롤러에 집중
- 장점
    - 모델 관련 로직 변경 시 컨트롤러나 뷰가 영향을 받지 않음 → 유지보수 쉬워짐
- MVC 패턴과 모델2 구조 매핑
    - 컨트롤러 : 서블릿 → 웹 브라우저 요청과 웹 어플리케이션의 전체적인 흐름 제어
    - 모델 : 로직 처리 클래스 → 서비스 클래스나 DAO 클래스를 이용해 수행, 결과를 보통 자바 빈에 저장하여 컨트롤러에 전달
    - 뷰 : JSP → 화면 출력하는 역할, JSP는 컨트롤러에서 request 기본객체나 session 기본 객체에 저장한 데이터르 사용하여 웹 브라우저에 알맞은 결과 출력

### command 패턴

- 객체의 행위(메서드)를 클래스로 만들어 캡슐화하는 패턴
- **기능을 요청하는 호출자(invoker)와 그 기능을 실행하는 수신자(receiver) 클래스 사이의 의존성을 제거하는 패턴**
    
    →실행될 기능의 변경에도 호출자 클래스를 수정 없이 그대로 사용  
    ![Untitled](https://github.com/HoChangSUNG/mentoring/assets/76422685/a113056f-0d4f-4472-a431-94770bf79b26)
    

- Command : 실행될 기능에 대한 인터페이스
- ConcreteCommand : 실제로 실행되는 기능 구현
- Invoker : 기능 실행을 요청하는 클래스
- Receiver : ConcreteCommand의 기능을 실행하기 위해 사용되는 클래스

command 패턴 참고 : https://gmlwjd9405.github.io/2018/07/07/command-pattern.html
