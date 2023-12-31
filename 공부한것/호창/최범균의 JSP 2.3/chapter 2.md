# CHAPTER2 웹 프로그래밍 기초

## URL과 웹 페이지

- 웹 페이지 : 웹 브라우저에서 URL에 해당하는 내용이 출력된 것
- URL : 웹 페이지의 주소를 표현할 때 사용
    - 구성요소
    ![KakaoTalk_20230729_170130138_01](https://github.com/HoChangSUNG/mentoring/assets/76422685/fd07b02e-1b13-43cd-8b9d-0d0e23ecd3a3)
        - 프로토콜 : 웹 브라우저가 서버와 내용을 주고받을 때 사용할 규칙 이름
        - 서버 이름 : 웹 페이지를 요청할 서버의 이름(도메인 이름, IP주소)
        - 경로 : 웹 페이지의 상세 주소
        - 쿼리 문자열
            - 추가로 서버에 보내는 데이터
            - 같은 경로라 하더라도 입력한 값에 따라 다른 결과를 보내줄 때 사용

## 웹 브라우저와 웹 서버

**웹브라우저와 웹 서버의 통신 과정**
![KakaoTalk_20230729_170130138](https://github.com/HoChangSUNG/mentoring/assets/76422685/fca1f262-e950-4c67-8e66-7fda6623aa4c)
1. DNS 서버에 도메인 이름에 대한 IP 주소 요청
2. IP 주소 응답
3. URL에 맞는 웹 서버에 웹 페이지를 요청
4. URL에 맞는 웹 페이지를 응답으로 제공
5. 웹 브라우저가 응답을 받고 응답 데이터를 렌더링한다.

   렌더링 : HTML 문서를 정해진 규칙에 따라 분석하여 알맞은 화면을 생성하는 것


**HTTP**

- HyperText Transfer Protocol
- 웹 브라우저가 웹 서버가 다양한 데이터(HTML, 이미지, 동영상 등)를 주고받을 때 사용하는 규칙
- **HTTP 요청/응답 데이터**로 구성
    - **HTTP 요청 데이터 구성요소**
        - 요청줄 : HTTP 요청 방식(method → post, get…), 요청하는 자원의 경로 지정
        - 헤더 : 서버가 응답을 생성하는데 참조할 수 있는 정보 전송
        - 바디 : 전송해야 하는 정보
    - **HTTP 응답 데이터 구성요소**
        - 응답줄 : 요청에 대한 응답 코드(200, 404 …)
        - 헤더 : 응답에 대한 정보를 전송
        - 바디 : 웹 브라우저가 요청한 자원의 내용(HTML 문서, 이미지, 파일 등)

### 정적 자원과 동적 자원

- 정적 자원(정적 페이지)
    - 변하지 않는 자원들
    - HTML파일, 이미지 파일과 같이 자주 바뀌지 않는 것들
- 동적 자원(동적 페이지)
    - 시간이나 특정 조건에 따라 응답 데이터가 달라지는 자원

### 웹 프로그래밍과 JSP

**웹 프로그래밍**

- 웹 서버가 웹 브라우저에 응답으로 전송할 데이터를 생성해주는 프로그램을 작성하는 것

**JSP**

- 동적 페이지를 작성하는데 사용되는 자바의 표준 기술

**웹서버 VS 웹 어플리케이션 서버(WAS)**

- 웹 서버
    - HTML, 이미지 같은 정적 자원을 제공
- 웹 어플리케이션 서버
    - 어플리케이션을 구현하는데 필요한 기능(프로그래밍언어,데이터베이스 연동 등), 동적 자원을 제공


클라이언트 : 데이터를 요청하는 쪽

서버 : 요청을 받아 알맞은 기능이나 데이터를 제공하는 쪽
