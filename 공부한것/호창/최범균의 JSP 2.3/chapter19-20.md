# CHAPTER19-20

# CHAPTER 19 필터

- HTTP 요청과 응답을 변경할 수 있는 재사용 가능한 클래스
- 여러개의 필터가 모여 하나의 필터 체인 형성
- 기능
    - HTTP 요청과 응답 정보를 변경 가능
        - 클라이언트의 요청(request)과 최종 자원(JSP, 서블릿) 사이, 최종 자원(JSP, 서블릿)과 클라이언트 응답(response) 사이에 위치하여 요청 정보 또는 응답 정보를 변경할 수 있음
    - 흐름 변경 가능
        - 필터가 클라이언트 요청(request)을 필터 체인의 다음 필터로 보내는 것이 아니라 다른 자원의 결과를 클라이언트에 전송할 수 있음
        
- 필터 구현
    - javax.servlet.Filter 인터페이스 : 필터를 나타내는 객체가 구현해야 하는 인터페이스
    - javax.servlet.ServletRequestWrapper 클래스 : 필터가 요청을 변경하기 위해 사용하는 래퍼 클래스
    - javax.servlet.ServletResonseWrapper 클래스 :  필터가 응답을 변경하기 위해 사용하는 래퍼 클래스

# CHAPTER 20 ServletContextListener 구현

### **ServletContextListener 인터페이스**

- 웹 어플리케이션이 시작되거나 종료되는 시점에 호출할 메서드를 정의한 인터페이스
    - 웹 어플리케이션이 시작될 때 호출
        - `public void contextInitialized(ServletContextEvent sce)` : 웹 어플리케이션을 초기화할 때 호출됨
    - 웹 어플리케이션이 종료될 때 호출
        - `public void contextDestroyed(ServletContextEvent sce)` : 웹 어플리케이션을 종료할 때 호출됨
