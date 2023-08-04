# CHAPTER10 클라이언트와의 대화 2:세션

## 세션

- 클라이언트의 상태를 서버의 웹 컨테이너에 보관하는 데이터
- 웹 브라우저마다 하나의 세션이 존재하며 세션 ID로 세션을 구분
- 세션 ID로 세션을 찾기 때문에 웹 서버와 웹 브라우저간 세션 ID 공유하기 위해 JSESSIONID라는 쿠키를 사용하여 세션 ID 공유
- 세션은 웹 어플리케이션마다 생성되므로 서로 다른 웹 어플리케이션 사이에서는 세션을 공유하지 않음
    - JSESSIONID 쿠키의 경로로 웹 어플리케이션의 컨텍스트 경로를 사용하기 때문에 서로 다른 웹 어플리케이션 사이에서 세션 공유 안됨
- 사용법
    - 세션 생성
        - `request.getSession()`
            - session 존재 시 해당 session 리턴, session 존재하지 않으면 새로운 session 생성 후 리턴
        - `request.getSession(false)`
            - session 존재 시 해당 session 리턴, session 존재하지 않으면 null 리턴
    - 세션 속성 조회
        - `session.getAttribute(”name”)`
    - 세션 속성 추가
        - `session.setAttribute(”name”)`
    - 세션 종료
        - 세션 종료 시 session 기본 객체를 삭제하고, session 기본 객체에 저장했던 속성도 삭제
        - `session.invalidate()`
    - 세션 유효 시간
        - 최근 요청한 시간(`LastAccessedTime`)을 기준으로 타임 아웃 시간 만큼 지나면 종료
        - 세션 유효 시간 지정 방법
            - web.xml에서 `<session-timeout>` 태그 사용
                
                ```xml
                <session-config>
                	<session-timeout>50</session-timeout>
                </session-config>
                ```
                
            - session 기본 객체가 제공하는 `setMaxInactiveInterval()` 메서드 사용
                - `session.setMaxInactiveInterval(60*60)`
                
- 세션 유효 시간 지정시 주의할 점
    - <session-timeout>을 이용해서 세션 유효 시간 지정 시 값을 0이나 음수로 설정하면 세션 유효 시간을 가지지 않음
    - 이 경우 session.invalidate()호출 전까지 세션 객체가 웹 서버에 유지되기 때문에 세션 객체가 계속 메모리에 남게 되어 메모리 부족 현상이 발생할 수 있음
    
    → 세션이 유효시간을 갖지 않으면, 세션 객체가 메모리에서 제거되지 않아 메모리 부족 현상 발생
    

### **Request 기본 객체 vs Session 기본 객체**

- Request 기본 객체는 하나의 요청을 처리하는데 사용되는 JSP 페이지 사이에서 공유
- Session 기본 객체는 하나의 웹 브라우저의 여러 요청을 처리하는 JSP 페이지 사이에서 공유

### 쿠키의 보안 문제와 세션 사용

**쿠키에서의 보안 문제**

- 쿠키 값은 임의로 변경할 수 있다
- 쿠키에 보관된 정보는 훔쳐갈 수 있다.

**쿠키에서의 보안 문제를 세션이 어떻게 해결했는가?**

- 쿠키 값 변조 가능 → 예상 불가능한 복잡한 세션 ID 사용
- 쿠키에 보관된 정보는 훔쳐갈 수 있다. → 세션 ID를 탈취 당해도 중요한 정보가 없음
- 쿠키 탈취 후 사용 → 서버에서 세션 유효 시간을 짧게 해서 유효시간이 지나면 사용할 수 없도록 함
