# CHAPTER9 클라이언트와의 대화 1 :쿠키

## 쿠키

- 웹 브라우저가 보관하는 데이터
- 웹 서버가 쿠키를 생성하여 응답 헤더에 추가하여 웹 브라우저에 보내면,
    
    이후부터 쿠키가 삭제되기 전까지 웹 브라우저가 웹 서버에 요청을 보낼 때마다 쿠키를 전달
    
- **동작 방식**  
    ![KakaoTalk_20230801_232335922](https://github.com/HoChangSUNG/mentoring/assets/76422685/5899ddd8-68ee-489f-a50e-c60e1172c451)
    
    - 쿠키 생성 단계
        - JSP 프로그래밍에서는 웹 서버 측에서 쿠키 생성
        - 생성한 쿠키를 응답 데이터의 헤더에 저장하여 웹 브라우저에 전송
    - 쿠키 저장 단계
        - 웹 브라우저가 응답 데이터의 헤더에 포함된 쿠키를 쿠키 저장소에 보관
    - 쿠키 전송 단계
        - 웹 브라우저는 요청을 보낼 때마다, 저장한 쿠키를 웹 서버에 전송
- **쿠키 헤더와 출력 버퍼**
    - 쿠키는 http 응답의 Set-Cookie 헤더에 저장되어 웹 브라우저에 전달됨
        - 한 개의 Set-Cookie 헤더에는 한 개의 쿠키 값을 전달
        - 아래와 같이 여러 개의  Set-Cookie 헤더를 추가하여 여러 개의 쿠키 값을 전달
            
            ```java
            Set-Cookie: id=modvirus; Domain=.somehost.com
            Set-Cookie: id=invalid;
            Set-Cookie: id=test;
            ```
            
    - **쿠키는 응답 헤더를 사용해 웹 브라우저에 전달하기 때문에 쿠키는 출력 버퍼가 플러시되기 전에 추가해야 한다**
        - 왜 그런가?
            - 응답 헤더는 출력 버퍼에 있는 내용을 처음 플러시할 때 전송하고 플러시 된 이후에는 응답 헤더에 새로운 값을 추가하지 못하기 때문이다.
        
- **쿠키 구성 요소 5 가지**
    - **이름, 값, 유효 시간, 도메인, 경로** 로 구성된다
    - **이름** : 각각의 쿠키를 구별하는데 사용되는 이름
    - **값** : 쿠키의 이름과 관련된 값
    - **유효 시간**
        - 쿠키의 유지 시간
        - 지정하지 않으면 웹 브라우저 종료시 쿠키가 함께 삭제
        - 쿠키 유효시간이 유효시간 동안 쿠키가 존재
            
            (유효시간이 지나지 않았으면  웹 브라우저 종료해도 쿠키 유지)
            
        - `setMaxAge()` 메소드로 지정
    - **도메인**
        - 쿠키를 전송할 도메인 범위 지정
        - 기본적으로 쿠키는 쿠키를 생성한 서버에만 전송
            - 다른 도메인을 사용하는 모든 서버에 쿠키 보내야 하는 경우 `setDomain()` 메소드 사용
                - `setDomain()` : 생성한 쿠키를 전송할 수 있는 도메인 지정
                - **도메인 형식**
                    - **`.`** 으로 시작하는 경우 관련 도메인에 모두 쿠키 전송
                        - `“.somehost.com”`→ `www.somehost.com` , `mai.somehost.com` 등 쿠키 전달
                    - 특정 도메인에만 쿠키 전송
                        - `“www.somehost.com”` → `www.somehost.com` 에만 쿠키 전송 가능
                - 도메인 지정시 주의 사항
                    - `setDomain()` 값으로 현재 서버의 도메인 및 상위 도메인에만 쿠키 전달 가능
                        - jsp 실행되는 서버 주소가 mail.somehost.com이면
                            - `setDomain()`으로 `mail.somehost.com`, `.somehost.com` 가능
                            - `setDomain()`으로 `www.somehost.com` 와 같이 다른 주소는 불가능
            - **웹 브라우저는 타 도메인으로 지정된  쿠키는 보안 문제를 받지 않음**
    
    - **경로**
        - 쿠키를 전송할 요청 경로
        - 경로는 URL에서 도메인 이후의 부분이 해당
        - setPath()로 지정 가능
        - 보통 쿠키는 웹 어플리케이션에 포함된 jsp와 서블릿에서 사용하기 때문에 쿠키 경로를 `“/”`로 지정

- **사용법**
    - 쿠키 생성
        
        ```java
        Cookie cookie = new Cookie(”cookieName”, “cookieValue”);
        response.addCookie(cookie);
        ```
        
    - 쿠키 값 조회
        - `Cookie[] cookies = request.getCookies();`
            - 쿠키가 존재하지 않으면 null 리턴
    - 쿠키 값 변경
        - 같은 이름의 쿠키를 새로 생성해서 응답 데이터로 보내면 됨
            
            ```java
            // 이미 name이라는 쿠키 값이 있는 경우
            Cookie cookie = new Cookie("name”, “sdf”);
            response.addCookie(cookie);
            ```
            
    - 쿠키 값 삭제
        - 유효 시간을 0으로 지정하고 응답헤더에 추가하면, 웹 브라우저가 관련 쿠키를 삭제
            
            ```java
            // 이미 name이라는 쿠키 값이 있는 경우
            Cookie cookie = new Cookie("name”, “sdf”);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            ```
            

**쿠키 종류**

- 영속 쿠키 : 만료 날짜를 입력하면 해당 날짜까지 쿠키 이용
- 세션 쿠키 : 만료 날짜를 생략하면 웹 브라우저 종료시까지만 쿠키 이용
