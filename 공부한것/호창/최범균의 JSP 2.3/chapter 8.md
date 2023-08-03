# CHAPTER8 자바빈과 <jsp:useBean>액션 태그

### 자바빈(javaBeans)

- 속성(데이터), 변경 이벤트, 객체 직렬화를 위한 표준
- 자바빈 규약을 따르는 클래스

### 자바빈 프로퍼티

- 자바 빈에 저장되는 값
    - 메소드 이름을 이용해서 프로퍼티의 이름 결정
    - 프로퍼티 값 변경하는 메소드 : `setXxx` → property 이름의 첫 글자를 대문자로 변환
    - 프로퍼티 값 읽어오는 메소드 :  `getXxx` → property 이름의 첫 글자를 대문자로 변환
    
- 프로퍼티 값
    - 읽기 프로퍼티
        - get 또는 is 메서드만 존재하는 프로퍼티
        - 프로퍼티 값을 변경할 필요 없이 읽기만 필요한 경우 사용
    - 읽기/쓰기 프로퍼티
        - get/set 또는 is/set 메소드가 존재하는 프로퍼티
        - 프로퍼티 값을 변경하고 읽기가 필요한 경우 사용

### **<jsp:useBean> 액션 태그**

- JSP 페이지에서 사용할 자바 빈 객체를 지정할 때 사용
- scope 옵션으로 영역별(page, request, session, application)로 공유할 데이터를 쉽게 저장 가능
- **객체를 생성하여 지정한 영역에 저장하고, 이미 지정한 영역에 객체가 존재하면 존재하던 객체를 사용**
    - request 영역에 이미 student라는 이름의 객체가 존재하면 객체를 새로 만들지 않고 기존의 student 객체를 사용한다.
- **<jsp:useBean> 액션 태그 사용 감소 이유**
    - MVC 사용시 로직은 자바 클래스에서 처리하고 JSP를 통해 결과를 보여주어 <jsp:useBean>태그를 클래스에서 사용할 수 없기 때문
    - 대신 request.getPrameter()를 사용하여 파라미터 값을 읽거와 자바 객체에 저장하는 방시긍로 사용됨

- 사용법
    - `<jsp:useBean id=”[빈이름]” class=”[자바빈클래스이름]” scope=”[범위]” />`
        - id: jsp 페이지에서 자바빈 객체에 접근할 때 사용할 이름 지정
        - class : 패키지 이름을 포함한 자바빈 클래스의 완전한 이름
        - scope : 자바빈 객체를 저장할 영역 지정 → page, request, session, applicatoin 중 하나의 값, 기본값 : page
    - `<jsp:useBean id=”info” class=”chap08.member.MemberInfo” scope=”request” />`
        - 위 자바 빈 액션 태그는 다음과 동일한 기능을 함
            
            ```java
            MemberInfo info = (MemberInfo)request.getAttribute("info");
            if(info==null){
            	info = new MemberInfo ();
            	request.setAttribute(info);
            }
            ```
            

- **<jsp:setProperty> 액션 태그**
    - 생성한 자바 빈 객체의 프로퍼티 값을 변경
    - `<jsp:setProperty name="자바빈 이름" property="이름" value="값"/>`
        - name
            - 프로퍼티 값을 변경할 자바빈 객체의 이름 지정
            - <jsp:useBean> 태그의 id속성에서 지정한 값 사용
        - property
            - 값을 지정할 프로퍼티 이름 지정
            - `property=”*”`로 지정 시 각 프로퍼티의 값을 같은 이름을 가진 파라미터의 값으로 설정
        - value : 프로퍼티 값 지정
        
- **<jsp:getProperty> 액션 태그**
    - 생성한 자바 빈 객체의 프로퍼티 값을 출력
    - `<jsp:getProperty name="자바빈 이름" property="프로퍼티 이름" />`
        - name : <jsp:useBean>의 id속성에서 지정한 자바빈 객체의 이름 지정
        - property : 출력할 프로퍼티의 이름 지정
