# CHAPTER11-12

# CHAPTER 11 표현 언어

## 표현 언어(Expression Language)

- 값을 표현하는데 사용하는 스크립트 언어, JSP의 스크립트 요소를 보완하는 역할
- 기능
    - JSP의 4가지 기본 객체가 제공하는 영역의 속성 사용
    - 수치 연산, 관계 연산, 논리 연산자 제공
    - 자바 클래스 메서드 호출 기능 제공
    - 쿠키, 기본 객체의 속성 등 JSP를 위한 표현 언어의 기본 객체 제공
    - 람다식을 이용한 함수 정의와 실행
    - 스트림 API를 통한 컬렉션 처리
    - 정적 메서드 실행
- EL 구성
    - `$/#`와 `{, }`와 표현 식을 사용하여 값 표현
    - `${expr}` , `#{expr}`
    - JSP의 스크립트 요소를 제외한 나머지 부분에서 사용 가능
    - `${expr}` , `#{expr}`의 차이점
        - `${expr}`
            - `${expr}` 구문을 분석할 때 곧바로 값을 계산
        - `#{expr}`
            - 실제로 값이 사용될 때 값을 계산
            - 값이 필요할 때 계산을 수행해 Deffered Expression이라 불림
            - Deffered Expression을 허용하는 태그의 속성에만 위치 가능
    

### 표현 언어에서 람다식 사용

- 람다식 : 함수처럼 파라미터를 가진 코드 블록
- `(파라미터1, 파라미터2) → EL식`
- 특정 변수에 할당하지 않고 바로 호출 가능
- 재귀 호출이 가능

### 표현 언어에서 스트림 API

- EL은 for, while 같은 반복문을 제공하지 않고, 스트림 API를 제공
- Map 타입은 스트림 제공 x, Map.entrySet() 처럼 컬렉션 타입 객체를 생성한 뒤에 사용 가능
    - EL에서의 스트림 API
        - 실제 자바 컬렉션 API에서 제공하는 메서드가 아닌 EL에서 제공하는 스트림 API
        - EL에서 stream()을 실행 → EL의 stream() 메소드 실행
        - 스트립트릿에서 stream()을 실행 → java 8의 stream() 메소드 실행
- 스트림 API 구조
    - **스트림 생성**
        - 컬렉션 객체에 대해 stream()을 실행하여 스트림 객체 생성
    - **중간 연산**
        - 스트림 객체를 변환
        - 여러 번 수행 가능
    - **최종 연산**
        - 스트림에서 데이터를 읽어와 최종 결과 생성
- 스트림 API 메소드 종류
    - filter() : 값을 걸러낼 때 사용
    - map()
        - 원소를 변환한 새로운 스트림 생성
        - 스트림의 각 원소에 대해 람다식을 실행하고, 람다식의 결과로 구성된 새로운 스트림을 생성
    - sorted()
        - 스트림을 정렬
        - Comparable 인터페이스를 구현하고 있지 않은 원소 정렬 or 오름차순이 아닌 다른 순서로 정렬하고 싶은 경우
            - sorted() 메서드에 값을 비교할 때 사용할 람다식을 전달하면 됨
                
                ```java
                ${ vals = [20,19,30];
                sortedVals = vals.stream().sorted((x1,x2)-> x1<x2 ? 1:-1).toList()}
                ```
                
    - limit()
        - 지정한 개수를 갖는 새로운 스트림 생성
    - count()
        - 스트림의 원소 개수 리턴
    - max(), min()
        - 요소가 존재하지 않을 수 있으므로 Optional로 리턴해줌
    - sum(),average()
        - average() → Optional 타입 리턴
        - sum() → int타입 리턴
    - anyMatch(), allMatch(), noneMatch()
        - Optional 타입 리턴
        - anyMatch(), allMatch(), noneMatch() → 조건이 충족하는 값이 존재할 경우 true

### 표현 언어 비활성화 방법

- web.xml 파일에 비활성화 옵션 지정
- JSP 페이지에 비활성화 옵션 지정
- web.xml 파일을 서블릿 2.3또는 2.4 버전에 맞게 작성

## CHAPTER 12 표준 태그 라이브러리(JSTL)

- JSP 페이지에서 많이 사용되는 논리적 판단, 반복 처리, 포맷 처리를 위한 커스텀 태그를 표준으로 만들어서 정의한 것
- JSTL이 제공하는 태그 종류
    - 코어 태그
    - XML 태그
    - 국제화 태그
    - 데이터베이스 태그
    - 함수 태그

### **코어 태그**

- 변수 설정이나 if-else 같은 논리 처리에 사용되는 스크립트 코드를 대체하는 태그 제공
- 변수 지원 태그
    - <c:set> 태그 : EL 변수의 값이나 EL 변수의 프로퍼티 값을 지정할 때 사용
    - <c:set> 태그 : set 태그로 지정한 변수를 삭제할 때 사용
- 흐름 제어 태그
    - <c:if> 태그 : if 블록을 대체할 때 사용
    - <c:choose>,  <c:when>, <c:otherwise>태그 : 다수의 조건문을 하나의 블록에서 수행할 때 사용
    - <c:forEach> 태그 : 배열, Collection, Map에 저장되어 있는 값들을 순차적으로 처리할 때 사용
    - <c:forTokens> 태그 : java.util.StringTokenizer 클래스와 같은 기능을 제공하는 태그
- URL 처리 태그
    - <c:url> 태그 : URL을 생성해주는 기능
    - <c:redirect> 태그 : 지정한 페이지로 리다이렉트 시켜주는 기능
- 기타 코어 태그
    - <c:out> 태그 : JspWriter에 데이터를 출력할 때 사용되는 태그
    - <c:catch> 태그 : 발생한 Exception을 EL 변수에 저장할 때 사용되는 태그
    

### 국제화 태그

- 로케일 지정 태그
    - <fmt:setLocale> : 국제화 태그들이 사용할 로케일을 지정
    - <fmt:requestEncoding> : 요청 파라미터의 캐릭터 인코딩을 지정
- 메시지 처리 태그
    - <fmt:bundle> : 태그 몸체에서 사용할 리소스 번들을 지정
    - <fmt:message> : 메시지를 출력
    - <fmt:setBundle> : 특정 메시지 번들을 사용할 수 있도록 로딩
- 숫자 및 날짜 포맷팅 처리 태그
    - 숫자 출력과 파싱 관련 : <fmt:formatNumber>, <fmt:parseNumber>
    - 날짜 출력과 파싱 관련 :  <fmt:formatDate>, <fmt:parseDate>
    - 시간대 설정 관련 : <fmt:setTimeZone>, <fmt:timeZone>

### 함수

- 표현 언어에서 사용할 수 있는 함수 제공
    - length(obj)  obj가 List와 같은 Collection인 경우 지정된 항목의 개수 리턴,obj가 String인 경우 문자열 길이 리턴
    - toUpperCase(str) : str을 대문자로 변환
    - toLowerCase(str) : str을 소문자로 변환
    - substring(str,idx1,idx2)  : str.substring(idx1, idx2)의 결과를 리턴
    - 등등
