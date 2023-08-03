# CHAPTER7 페이지 모듈화와 요청 흐름 제어

# <jsp:include> 액션 태그와 include 디렉티브

## <jsp:include> 액션 태그

- 태그가 위치한 부분에 지정한 페이지를 포함시키는 태그
- 언제 사용하는가?
    - 다른 JSP의 실행 결과나 코드를 포함할 때 사용
    - 화면 구성 요소의 코드 중복 문제를 없앨 때 사용
        
        → 공통 영역을 별도의 jsp 페이지로 생성하고 <jsp:include> 통해 지정
        
    
- **<jsp:include> 액션 태그 처리 순서**
    ![KakaoTalk_20230731_193701143](https://github.com/HoChangSUNG/mentoring/assets/76422685/422ac770-dfcd-4ffc-9f35-487f4d160c74)
    
    1. main.jsp가 웹 브라우저 요청을 받음
    2. 출력 내용 A를 출력 버퍼에 저장
    3. <jsp:include>가 실행되면 요청 흐름을 sub.jsp(태그가 지정한 페이지)로 이동
    4. 출력 내용 B를 출력 버퍼에 저장
    5. sub.jsp의 실행이 끝나면 요청 흐름이 다시 main.jsp의 <jsp:include>로 돌아옴
    6. <jsp:include> 이후 부분인 출력내용C를 출력 버퍼에 저장
    7. 출력 버퍼에 내용을 응답 데이터로 전송

- **<jsp:include> 액션 태그 사용법**
    - `<jsp:include page=”포함할페이지” flush=”true” />`
        - page : 포함할 JSP 페이지 경로 지정
        - flush : 지정한 JSP를 실행하기 전 출력 버퍼를 플러시할지 여부 결정
            - true : 출력 버퍼 플러시, false : 출력 버퍼 플러시 X
                - flush=”true” 라면 이후 새로운 헤더 정보를 추가해도 반영되지 않음
                
- **<jsp:param>**
    - <jsp:param> 태그를 이용해 포함할 JSP 페이지에 파라미터를 추가
    - **<jsp:include> 액션 태그로 포함하는 페이지에서만 유효**
    - 이미 동일한 파라미터가 존재하면 기존 파라미터 값을 유지하고 새로운 값 추가
        - `request.getParameter(”name”)`을 호출할 때 `<jsp:param>`으로 새로 추가한 파라미터 값이 우선시되어 이 값이 리턴
    - <jsp:param> 태그로 전달할 요청 파라미터 값은 `request.setCharacterEncoding()` 메서드로 설정한 캐릭터 셋으로 파라미터를 인코딩
    - 예시
        
        ```java
        <jsp:include page="/module/top.jsp" flush="false">
        	<jsp:param name="name" value="호창">
        </jsp:include>
        ```
        

## include 디렉티브

- 지정한 페이지를 현재 위치에 포함시키는 기능
- `<%@ include file =”포함할 파일” %>`
- 현재 파일에서 include 디렉티브 이후 위치에서는 include 디렉티브에서 선언한 변수 사용 가능
- 처리 순서  
  ![KakaoTalk_20230803_195300056](https://github.com/HoChangSUNG/mentoring/assets/76422685/122412c1-e56d-476b-be23-f00e89adb821)
    - JSP 파일을 자바 파일로 변환하기 전에 include 디렉티브에서 지정한 파일의 내용을 해당 위치에 삽입하고, 그 결과로 생긴 자바 파일을 컴파일

### <jsp:include> 액션 태그와 include 디렉티브 차이점

- 지정한 페이지를 포함시키는 방식에 차이점이 존재
    - <jsp:include>: 다른 JSP로 실행 흐름을 이동시켜 실행 결과를 현재 위치에 포함시키는 방식
    - include 디렉티브  : 다른 파일의 내용을 현재 위치에 삽입한 후에 JSP 파일을 자바 파일로 변환하고 컴파일하는 방식
- 사용 시기
    - <jsp:include> : 레이아웃의 한 구성 요소를 모듈화하기 위해 사용
    - include 디렉티브
        - 모든 JSP 페이지에서 사용하는 변수 지정
        - 모든 페이지에서 중복되는 간단한 문장(저작권 표시 등)

# <jsp:forward> 액션 태그

## <jsp:forward> 액션 태그

- 하나의 JSP 페이지에서 다른 JSP 페이지로 요청 처리를 전달할 때 사용
- forward된 페이지가 생성한 응답 결과를 웹 브라우저에 전달(forward 되기 전 페이지가 생성한 응답 결과를 전달하는 것 X)
- <jsp:param> 액션 태그 사용 방식 → <jsp:include>와 동일
- `<jsp:forward page=”이동할 페이지”/>`
- 사용 시기
    - 조건에 따라 다른 결과를 보여주어야 할 때
    
- **<jsp:forward> 요청 흐름**  
  ![KakaoTalk_20230803_195300056_01](https://github.com/HoChangSUNG/mentoring/assets/76422685/9f5d1afd-b5a9-4f76-82b1-6411e7ce520a)
    - 웹 브라우저 요청을 from.jsp에 전달
    - from.jsp는 <jsp:forward> 액션 태그 실행하여 요청 흐름이 to.jsp로 이동
        - 요청 흐름이 이동할 때 from.jsp에서 사용한 **request 기본 객체와 response 기본 객체가 to.jsp로 전달**
        - 요청 흐름이 이동하면 **출력 버퍼를 비움**
    - **to.jsp가 응답 결과를 생성**
    - to.jsp가 생성한 결과가 웹 브라우저에 전달

### **<jsp:forward> 액션 태그와 출력 버퍼 관계**

- <jsp:forward> 액션 태그가 실행 시 **출력 버퍼를 비우고** forward된 페이지의 실행 결과를 출력 버퍼에 넣는 식으로 동작
- 주의할 점
    - <jsp:forward> 액션 태그가 실행되기 이전에 플러시되면 <jsp:forward>를 실행해서 실행 흐름을 이동하는 것이 실패

### <jsp:forward> 액션 태그와 <jsp:include> 액션 태그

- **page 속성 경로**
    - page 속성 경로는 2가지 방식으로 지정
        - 절대 경로 : 웹 어플리케이션 폴더를 루트로 사용하는 경로
        - 사용 경로 : 현재 jsp 페이지를 기준으로 경로 결정
        
- **<jsp:param> 액션 태그의 한계**
    - 파라미터를 이용해서 데이터를 전달하기 때문에 String 타입의 값만 전달 가능하다는 제약
        - **해결 방법**
            - reuest 기본 객체는 하나의 요청을 처리하는데 사용하는 모든 JSP에서 공유되기 때문에 위 문제를 해결 가능
