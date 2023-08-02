# CHAPTER6 에러처리

### 예외 페이지 지정하는 3가지 방법

- **page 디렉티브의 errorPage 속성에 에러 페이지를 지정**
    - JSP 실행 중 Exception 발생시 보여줄 에러 페이지를 page 디렉티브의 errorPage 속성을 사용해 지정
    
    ```java
    <%@ page contentType = "text/html%>
    <%@ page errorPage = "/error/viewErrorMessage.jsp%>
    ```
    

- **에러 코드 별로 사용할 에러 페이지를 web.xml 파일에 지정**
    - web.xml 파일에 설정 추가하여 에러 페이지 지정
        
        ```java
        <?xml version="1.0" encoding="utf-8">
        
        <web-app ...>
        	...
        	<error-page>
        		<error-code>에러코드</error-code>
        		<location>에러 페이지의 URL</location>
        	</error-page>
        </web-app>
        ```
        
    - `<error-page>` : 한 개의 에러 페이지를 지정
    - `<error-code>` : 에러 상태 코드 지정
    - `<location>` : 에러 페이지로 사용할 JSP 파일의 경로
    
- **발생하는 예외 타입 별로 사용할 에러 페이지를 web.xml 파일에 지정**
    - JSP 페이지에서 발생하는 예외 종류별로 에러 페이지 지정 가능
        
        ```java
        	<error-page>
        		<exception-type>예외 타입</exception-type>
        		<location>에러 페이지의 URL</location>
        	</error-page>
        ```
        
        - `exception-type` : 에러 타입 지정
    

### 예외 페이지로 지정된 JSP

- 예외 페이지로 지정된 JSP는 exception 기본 객체를 사용 가능
- page 디렉티브의 `isErrorPage` 속성의 값을 `true`로 지정 → 예외 페이지로 지정

**주요 응답 상태 코드**

- 200 : 요청 정상 처리
- 307 : 임시로 페이지 리다이렉트
- 400: 클라이언트 요청이 잘못된 구문으로 구성
- 401 : 접근 허용 안함
- 404: 요청한 URL이 올바르지 않음
- 500 : 서버 내부에서 에러 발생

### 예외 페이지 우선 순위

1. page 디렉티브의 errorPage 속성에 에러 페이지를 지정
2. 발생하는 예외 타입 별로 사용할 에러 페이지를 web.xml 파일에 지정
3. 에러 코드 별로 사용할 에러 페이지를 web.xml 파일에 지정

### 예외 페이지와 버퍼

- 버퍼가 최초로 플러시 된 이후에 예외 발생 시 에러 페이지가 원하는 형태로 출력되지 않아 버퍼의 크기를 증가시켜 **버퍼가 플러시 되기 전에 예외가 발생해 에러 페이지를 처리**해야 함
    - **왜 버퍼가 최초로 플러시 되기 전에 예외를 처리해야 하는가?**
        - 버퍼가 최초로 플러시 될 때 응답 상태 코드가 전송되는데, 버퍼가 플러시된 이후 에러가 발생하면, 200 상태 코드와 일부 응답 결과 화면이 전송되고, 이후에 에러 페이지 내용이 붙기 때문
