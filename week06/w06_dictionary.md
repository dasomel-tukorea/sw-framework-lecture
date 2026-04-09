# 6주차 핵심 용어집

강의 자료의 비유와 설명을 바탕으로 정리한 용어집입니다. 처음 접하는 개념은 비유를 통해 먼저 감을 잡고, 정확한 정의로 마무리하세요.

---

## 1. Spring MVC (Model-View-Controller)

**Spring MVC**
> "Controller가 교통정리, Model이 데이터 봉투, View가 화면 그리기"

HTTP 요청을 Controller → Model → View 순서로 처리하여 HTML을 응답하는 서버사이드 렌더링(SSR) 아키텍처입니다.

```
브라우저 → DispatcherServlet → Controller → Model → View(Thymeleaf) → HTML → 브라우저
```

| 구성 요소 | 역할 | 예시 |
|---|---|---|
| **Controller** | 요청 수신, 비즈니스 로직 호출, 뷰 결정 | `@Controller` |
| **Model** | Controller → View 데이터 전달 컨테이너 | `model.addAttribute(...)` |
| **View** | Model 데이터를 HTML로 렌더링 | Thymeleaf 템플릿 |
| **DispatcherServlet** | 요청을 적절한 Controller로 라우팅 | Spring이 자동 구성 |

> @RestController(JSON)와의 차이: @Controller는 반환 문자열을 뷰 이름으로 해석
> `return "student/list"` → `templates/student/list.html` 렌더링

---

## 2. Thymeleaf 핵심 문법

**Thymeleaf**
Java 서버사이드 템플릿 엔진입니다. HTML 파일 안에 `th:*` 속성으로 서버 데이터를 바인딩합니다.

> 비유: HTML을 틀로 두고, 서버 데이터를 찍어내는 도장

---

**th:text**
서버 데이터를 텍스트로 출력합니다. HTML 특수문자를 자동 이스케이프하므로 XSS를 방어합니다.

```html
<td th:text="${student.name}">홍길동</td>
<!-- student.getName() 결과로 교체, <script> 등은 문자열로 표시 -->
```

"홍길동"은 서버 없이 HTML을 직접 열었을 때 보이는 기본값(Natural Template)입니다.

---

**th:each**
컬렉션을 반복하여 태그를 여러 번 생성합니다.

```html
<tr th:each="student : ${students}">
    <td th:text="${student.name}">홍길동</td>
</tr>
<!-- students 리스트 크기만큼 <tr> 반복 생성 -->
```

반복 상태 변수: `th:each="student, stat : ${students}"` → `stat.index`, `stat.count`, `stat.first`, `stat.last`

---

**th:if / th:unless**
조건에 따라 태그를 렌더링하거나 제거합니다.

```html
<table th:if="${!students.isEmpty()}">...</table>
<p th:unless="${!students.isEmpty()}">등록된 학생이 없습니다.</p>
<!-- th:unless = th:if의 반대 조건 -->
```

---

**th:href (@{} 링크 표현식)**
동적 URL을 생성합니다. 컨텍스트 경로를 자동 포함합니다.

```html
<!-- 경로변수 포함 -->
<a th:href="@{/students/{id}(id=${student.id})}">홍길동</a>
<!-- → /students/1 -->

<!-- 쿼리 파라미터 -->
<a th:href="@{/students(page=1, size=10)}">다음</a>
<!-- → /students?page=1&size=10 -->
```

---

**th:object / th:field**
폼과 DTO를 연결합니다. `th:field`가 id/name/value 속성을 자동 생성합니다.

```html
<form th:object="${studentForm}">
    <input th:field="*{name}">
    <!-- → <input id="name" name="name" value="홍길동"> -->
</form>
```

`*{...}`: th:object로 지정된 객체의 필드에 접근 (절대경로 `${}`와 구별)

---

**th:errors / th:classappend**
Bean Validation 검증 에러를 폼에 표시합니다.

```html
<input th:field="*{name}"
       th:classappend="${#fields.hasErrors('name')} ? 'field-error'">
<div th:errors="*{name}">에러 메시지</div>
<!-- 에러 없으면 div 자체가 렌더링되지 않음 -->
```

`#fields`: Thymeleaf 빌트인 객체 — 검증 결과 접근 제공

---

**th:replace / th:insert (Fragment)**
다른 HTML 파일의 조각을 현재 위치에 삽입합니다.

```html
<!-- th:replace: 현재 태그를 Fragment로 완전 교체 -->
<header th:replace="~{fragments/layout :: header}"></header>

<!-- th:insert: 현재 태그 안에 Fragment 삽입 (감싸는 태그 유지) -->
<div th:insert="~{fragments/layout :: header}"></div>
```

`~{파일경로 :: 프래그먼트명}`: templates/ 기준 상대경로, 확장자 생략

---

**th:utext (Unescaped Text)**
HTML을 이스케이프 없이 그대로 렌더링합니다. 사용자 입력에 절대 사용 금지.

```html
<!-- th:text  → 안전: <script> 태그가 문자열로 표시됨 -->
<span th:text="${userInput}">출력값</span>

<!-- th:utext → 위험: <script> 태그가 실행됨! XSS 공격 성공 -->
<span th:utext="${userInput}">출력값</span>
```

---

## 3. Form 바인딩 & @ModelAttribute

**@ModelAttribute**
HTTP 요청 파라미터를 Java 객체에 자동 바인딩합니다.

```java
@PostMapping
public String addStudent(@ModelAttribute("studentForm") StudentForm form) {
    // form.getName() = 요청 파라미터 "name" 값
}
```

동작 원리:
1. `StudentForm` 기본 생성자로 빈 객체 생성
2. 요청 파라미터 이름과 일치하는 Setter 호출하여 값 주입
3. 메서드 파라미터로 전달

> 필수 조건: **기본 생성자 + Setter** 존재

---

## 4. PRG 패턴 (Post-Redirect-Get)

**PRG 패턴**
> "POST 처리 후 반드시 Redirect → GET으로 돌아온다"

POST 요청 후 브라우저가 GET 요청을 재실행하도록 302 응답을 보내는 패턴입니다.

```
❌ PRG 없음: POST → render view
   새로고침(F5) → POST 재전송 → 중복 등록!

✅ PRG 적용: POST → 302 Redirect → GET → render view
   새로고침(F5) → GET만 재실행 → 안전!
```

```java
// POST 처리 완료 후 redirect
return "redirect:/students/{id}"; // 브라우저: 302 → GET /students/1
```

**RedirectAttributes**
redirect 후에도 데이터를 1회 전달합니다.

```java
redirectAttributes.addAttribute("id", saved.getId());         // URL 경로에 치환
redirectAttributes.addFlashAttribute("status", true);          // 1회성 메시지 (F5 후 사라짐)
```

| 메서드 | 전달 방식 | 사라지는 시점 |
|---|---|---|
| `addAttribute` | URL 파라미터 | URL에 남음 |
| `addFlashAttribute` | 세션 임시 저장 | redirect 후 1회 사용 즉시 소멸 |

---

## 5. Bean Validation

**Bean Validation**
객체 필드에 어노테이션으로 검증 규칙을 선언적으로 정의하는 표준 스펙입니다.

```java
public class StudentForm {
    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 20)
    private String name;

    @Pattern(regexp = "\\d{9}", message = "9자리 숫자로 입력하세요.")
    private String studentId;

    @Email(message = "이메일 형식이 아닙니다.")
    private String email; // @NotBlank 없음 → 선택 입력
}
```

| 어노테이션 | 검증 내용 | 빈 값 처리 |
|---|---|---|
| `@NotBlank` | null, "", "  " 거부 | 빈 값 실패 |
| `@Size` | 문자열 길이 범위 | 빈 값 통과 |
| `@Pattern` | 정규표현식 형식 | 빈 값 통과 |
| `@Email` | 이메일 형식 | 빈 값 통과 |
| `@NotNull` | null만 거부 ("" 통과) | "" 통과 |

---

**@Valid + BindingResult**
Controller에서 Bean Validation을 실행하고 결과를 처리합니다.

```java
@PostMapping
public String addStudent(
        @Valid @ModelAttribute("studentForm") StudentForm form,
        BindingResult result) {  // ⚠️ @Valid 파라미터 바로 다음에 선언 필수!

    if (result.hasErrors()) {
        return "student/addForm"; // 검증 실패 → 폼 재렌더링
    }
    // 검증 성공 → 저장 처리
}
```

> 주의: BindingResult 선언 위치가 틀리면 검증 실패 시 예외(400 에러) 발생

---

## 6. XSS (Cross-Site Scripting)

**XSS**
사용자 입력값에 포함된 악성 스크립트가 다른 사용자 브라우저에서 실행되는 공격입니다.

```
공격자 입력: <script>alert('XSS')</script>
th:utext 렌더링: <script> 그대로 삽입 → 모든 방문자 브라우저에서 실행!
th:text 렌더링: &lt;script&gt; 이스케이프 → 문자열로만 표시 (안전)
```

**th:text 이스케이프 변환표:**
| 원본 | 이스케이프 |
|---|---|
| `<` | `&lt;` |
| `>` | `&gt;` |
| `"` | `&quot;` |
| `&` | `&amp;` |

> 결론: 사용자 입력 출력 → 항상 `th:text` / `th:utext`는 신뢰된 관리자 HTML에만 제한적 사용

---

## 7. Thymeleaf Fragment

**Fragment**
HTML 조각을 이름으로 정의하고 여러 템플릿에서 재사용하는 기능입니다.

```html
<!-- layout.html: Fragment 정의 -->
<header th:fragment="header">
    <nav>공통 내비게이션</nav>
</header>

<!-- list.html: Fragment 사용 -->
<header th:replace="~{fragments/layout :: header}"></header>
```

| 방식 | 차이 | 결과 HTML |
|---|---|---|
| `th:replace` | 현재 태그를 Fragment로 교체 | `<header>` (Fragment의 태그) |
| `th:insert` | 현재 태그 안에 Fragment 삽입 | `<div><header>...</header></div>` |

> 비유: 학교 공지사항 양식 — 매번 새로 쓰지 않고 공통 양식을 재사용

---

## 8. Spring Data JDBC — save() INSERT vs UPDATE

**save() 동작 원리**

```java
// INSERT: @Id 필드가 null인 경우
Student newStudent = new Student("홍길동", "202300001", "hong@tukorea.ac.kr");
studentRepository.save(newStudent); // → INSERT SQL

// UPDATE: @Id 필드가 non-null인 경우
Student existing = studentRepository.findById(1L).get(); // id = 1
existing.setName("홍길순");
studentRepository.save(existing); // → UPDATE SQL
```

> Spring Data JDBC는 `@Id` 필드 null 여부만으로 INSERT/UPDATE를 결정합니다.

---

## 9. 비교 정리

| 구분 | @Controller | @RestController |
|---|---|---|
| 반환값 해석 | 뷰 이름(String) → HTML 렌더링 | 객체 → JSON 직렬화 |
| 주 용도 | Thymeleaf SSR | REST API |
| @ResponseBody | 필요 시 개별 추가 | 기본 적용 |

| 구분 | th:text | th:utext |
|---|---|---|
| HTML 이스케이프 | O (안전) | X (위험) |
| XSS 방어 | O | X |
| 사용 가능 대상 | 모든 사용자 입력 | 신뢰된 관리자 HTML만 |

| 구분 | addAttribute | addFlashAttribute |
|---|---|---|
| 전달 방식 | URL 파라미터 | 세션 임시 저장 |
| 유지 기간 | URL에 계속 남음 | redirect 후 1회 사용 후 소멸 |
| 새로고침 후 | 유지 | 사라짐 |

---

## 10. 자주 발생하는 문제 & 해결

| 문제 | 원인 & 해결 |
|---|---|
| Thymeleaf 렌더링 에러 | `th:object` 대상 객체가 Model에 없음 → `addAttribute("studentForm", new StudentForm())` 확인 |
| 검증 실패해도 400 에러 | `BindingResult`가 `@Valid` 파라미터 바로 다음에 없음 → 순서 수정 |
| F5 누르면 중복 등록 | PRG 패턴 미적용 → POST 처리 후 `return "redirect:/..."` 사용 |
| UPDATE 대신 INSERT 실행 | `@Id` 필드가 null → `findById()`로 기존 객체 로드 후 Setter 호출 |
| student_id 컬럼 없음 | `studentId` 필드는 DB 컬럼 `student_id`로 자동 매핑 → `schema.sql`에 `student_id` 컬럼 확인 |
| Fragment 렌더링 안 됨 | `templates/fragments/layout.html` 경로 확인, `th:fragment` 이름 일치 여부 확인 |
| XSS 스크립트 미실행 | `th:text` 사용 중 → 체험용으로 `th:utext`로 교체 (실습 후 원복 필수!) |

---

## 11. Spring Annotation 종합 정리 (6주차 추가)

| 어노테이션 | 설명 | 역할 |
|---|---|---|
| `@Controller` | HTML 뷰를 반환하는 MVC 컨트롤러 | MVC |
| `@RequestMapping` | URL 경로 매핑 (클래스/메서드 레벨) | MVC |
| `@GetMapping` | HTTP GET 요청 매핑 | MVC |
| `@PostMapping` | HTTP POST 요청 매핑 | MVC |
| `@PathVariable` | URL 경로변수 바인딩 (`/{id}`) | MVC |
| `@RequestParam` | 쿼리 파라미터/폼 파라미터 바인딩 | MVC |
| `@ModelAttribute` | 요청 파라미터 → 객체 자동 바인딩 | MVC |
| `@Valid` | Bean Validation 검증 실행 트리거 | Validation |
| `@NotBlank` | null/""/공백 거부 검증 | Validation |
| `@Pattern` | 정규표현식 형식 검증 | Validation |
| `@Email` | 이메일 형식 검증 | Validation |
| `@Size` | 문자열 길이 범위 검증 | Validation |

---

## 12. Servlet (서블릿)

**Servlet**
> 비유: "웹 서버 안에 상주하는 요청 처리 담당자"

HTTP 요청을 받아 처리하고 HTTP 응답을 돌려주는 Java 클래스입니다.
Spring MVC의 `DispatcherServlet`이 대표적인 서블릿입니다.

```
브라우저 HTTP 요청
  → 서블릿 컨테이너(Tomcat)
    → 서블릿(DispatcherServlet)
      → Controller → View → HTML 응답
```

| 용어 | 설명 |
|---|---|
| **서블릿 컨테이너** | 서블릿을 실행하는 환경 (Spring Boot 내장 Tomcat) |
| **DispatcherServlet** | Spring MVC의 프론트 컨트롤러 — 모든 요청의 진입점 |
| **Jakarta EE** | 과거 javax.* → 현재 jakarta.* 패키지명 (Spring Boot 3.x부터 사용) |

> Spring Boot는 서블릿 컨테이너 설정을 자동으로 해주기 때문에
> 개발자는 Servlet을 직접 다루지 않고 `@Controller`만 작성

---

## 13. Servlet Filter (서블릿 필터)

**Servlet Filter**
> 비유: "Controller 에 들어가기 전 보안 검색대"

HTTP 요청이 서블릿(DispatcherServlet)에 도달하기 **전**, 또는 응답이 클라이언트로 나가기 **전**에
공통 로직을 실행하는 컴포넌트입니다.

```
브라우저 요청
  → [Filter 1] → [Filter 2] → DispatcherServlet → Controller
브라우저 응답
  ← [Filter 1] ← [Filter 2] ← DispatcherServlet ← Controller
```

**Filter 인터페이스 핵심 메서드:**

```java
public interface Filter {
    // 초기화 (앱 시작 시 1회)
    default void init(FilterConfig filterConfig) {}

    // 요청/응답 처리 (매 요청마다 실행)
    void doFilter(ServletRequest request,
                  ServletResponse response,
                  FilterChain chain) throws IOException, ServletException;

    // 소멸 (앱 종료 시 1회)
    default void destroy() {}
}
```

**FilterChain:**
```java
// doFilter() 구현 패턴
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
    // ① 요청 전처리 (예: XSS 이스케이프, 인증 확인)
    chain.doFilter(request, response); // ② 다음 필터 또는 서블릿으로 전달
    // ③ 응답 후처리 (예: 응답 헤더 추가)
}
```

`chain.doFilter()` 를 호출해야 요청이 다음 단계로 전달됩니다.
호출하지 않으면 요청 처리가 중단됩니다 (접근 차단 가능).

**AOP와의 차이:**

| 구분 | Servlet Filter | Spring AOP |
|---|---|---|
| 동작 위치 | 서블릿 컨테이너 (Spring 바깥) | Spring 컨텍스트 내부 |
| 적용 대상 | URL 패턴 (`/*`, `/api/*`) | 메서드 (Pointcut 표현식) |
| 접근 가능 | HttpServletRequest/Response | 메서드 파라미터/반환값 |
| 주 용도 | XSS 방어, 인증, 로깅, CORS | 트랜잭션, 로깅, 성능 측정 |

---

## 14. FilterRegistrationBean — Spring 필터 등록

**FilterRegistrationBean**
Spring Boot에서 서블릿 필터를 등록하는 방법입니다.

```java
@Configuration
public class XssFilterConfig {

    @Bean
    public FilterRegistrationBean<XssEscapeFilter> xssFilter() {
        FilterRegistrationBean<XssEscapeFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssEscapeFilter()); // 필터 인스턴스
        registration.setOrder(1);                       // 실행 순서 (낮을수록 먼저)
        registration.addUrlPatterns("/*");              // 적용 URL 패턴
        return registration;
    }
}
```

| 설정 메서드 | 설명 |
|---|---|
| `setFilter()` | 등록할 Filter 인스턴스 |
| `setOrder()` | 필터 체인 내 실행 순서 (1 = 가장 먼저) |
| `addUrlPatterns()` | 필터 적용 URL 패턴 (`/*`, `/students/*` 등) |

---

## 15. HttpServletRequestWrapper — Decorator 패턴

**HttpServletRequestWrapper**
`HttpServletRequest` 를 감싸서 메서드를 오버라이드하는 데코레이터 클래스입니다.
XSS Servlet Filter의 핵심 구현 기법입니다.

```java
public class XssEscapeRequestWrapper extends HttpServletRequestWrapper {

    public XssEscapeRequestWrapper(HttpServletRequest request) {
        super(request); // 원본 요청을 부모에 위임
    }

    // getParameter() 를 오버라이드하여 이스케이프 적용
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name); // 원본 값
        return value != null ? HtmlUtils.htmlEscape(value) : null; // 이스케이프 후 반환
    }
}
```

**동작 원리:**
```
원본 요청: userInput = "<script>alert('XSS')</script>"
          ↓ XssEscapeFilter
Wrapper:  userInput = "&lt;script&gt;alert('XSS')&lt;/script&gt;"
          ↓ Controller (@RequestParam String userInput)
수신값:   "&lt;script&gt;alert('XSS')&lt;/script&gt;"
```

> `@RequestParam`, `@ModelAttribute` 모두 내부적으로 `getParameter()` 를 호출하므로
> Wrapper 하나로 모든 파라미터 바인딩에 자동 적용됩니다.

---

## 16. XSS 방어 계층별 비교

XSS를 방어할 수 있는 계층은 여러 가지이며, 역할과 적용 범위가 다릅니다.

```
요청 흐름:
브라우저 → [Filter] → Controller → Service → [View(Thymeleaf)] → HTML → 브라우저
              ④          ②③                             ①
```

| 계층 | 방법 | 코드 | 특징 |
|---|---|---|---|
| ① View | `th:text` | `<span th:text="${v}">` | Thymeleaf 자동 이스케이프, 가장 간단 |
| ② Controller | `HtmlUtils.htmlEscape()` | `HtmlUtils.htmlEscape(input)` | 모든 HTML 차단, View 독립적 |
| ③ Controller | `OWASP HTML Sanitizer` | `Sanitizers.FORMATTING.sanitize(input)` | allowlist 방식 — 안전한 태그 보존, 위험 태그만 제거 |
| ④ Filter | `XssEscapeRequestWrapper` | `FilterRegistrationBean` 등록 | Controller 코드 변경 없이 전역 적용 |

**HtmlUtils vs OWASP 핵심 차이:**

```
입력: <b>굵은글씨</b><script>alert('XSS')</script>

HtmlUtils  → &lt;b&gt;굵은글씨&lt;/b&gt;&lt;script&gt;...  (모든 HTML 이스케이프, 서식 소실)
OWASP      → <b>굵은글씨</b>                              (script 제거, b 태그 보존 → 굵게 표시)
```

| 구분 | HtmlUtils.htmlEscape() | OWASP HTML Sanitizer |
|---|---|---|
| 방식 | 전체 차단 (모든 HTML 이스케이프) | allowlist (허용 태그만 통과) |
| `<b>굵게</b>` | `&lt;b&gt;굵게&lt;/b&gt;` (서식 소실) | `<b>굵게</b>` (보존) |
| `<script>` | `&lt;script&gt;` (이스케이프) | 완전 제거 |
| 의존성 | Spring 내장 | `owasp-java-html-sanitizer` 추가 필요 |
| 적합한 경우 | 순수 텍스트 입력 | 사용자가 서식(굵게, 링크 등)을 입력하는 경우 |

**HtmlUtils.htmlEscape() — Spring 내장 유틸리티:**
```java
import org.springframework.web.util.HtmlUtils;

String input = "<script>alert('XSS')</script>";
String escaped = HtmlUtils.htmlEscape(input);
// → "&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;"
```

이스케이프 변환표:

| 원본 | 이스케이프 |
|---|---|
| `<` | `&lt;` |
| `>` | `&gt;` |
| `"` | `&quot;` |
| `'` | `&#39;` |
| `&` | `&amp;` |

**언제 어떤 방법을 쓰나:**

```
일반적인 웹 화면 → th:text (기본, 충분)
저장 전 sanitize 필요 → HtmlUtils.htmlEscape() (Controller 레이어)
전역 방어 + 관심사 분리 → XSS Servlet Filter (Filter 레이어)
Spring Boot 3.x (Jakarta EE) → 직접 구현 (XssEscapeFilterConfig 참고)
```

> 결론: 여러 방법을 조합할수록 방어가 강해지지만, **th:text 습관화**가 가장 기본이자 효과적

---

## 17. DTO (Data Transfer Object)

**DTO**
> 비유: "택배 상자 — 필요한 것만 담아서 전달"

계층 간 데이터를 전달하기 위해 만드는 객체입니다.
Domain 객체(Entity)를 그대로 노출하지 않고, 필요한 필드만 골라 담아 전달합니다.

```
브라우저 → [StudentForm DTO] → Controller → Domain(Student) → DB
DB → Domain(Student) → Controller → [StudentResponse DTO] → 브라우저
```

---

**왜 Domain 객체를 그대로 쓰지 않나?**

| 문제 | 설명 |
|---|---|
| **불필요한 필드 노출** | DB 내부 필드(password, createdAt 등)가 API 응답에 노출됨 |
| **검증 규칙 혼재** | `@NotBlank` 등 입력 검증이 Domain 객체에 섞임 |
| **API 형태 변경 어려움** | 화면마다 필요한 필드가 다른데 Domain은 고정됨 |

---

**이 프로젝트의 DTO 2가지:**

```java
// ① 입력 DTO — 폼 데이터를 받아 Controller로 전달
// StudentForm.java (dto/)
public class StudentForm {
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    @Pattern(regexp = "\\d{9}")
    private String studentId;
    @Email
    private String email;
}

// ② 출력 DTO — API 응답에서 필요한 필드만 선택해 반환
// StudentResponse.java (dto/)
public class StudentResponse {
    private Long id;
    private String name;
    private String studentId;
    private String email;
    // ← DB 내부 필드(버전, 생성일 등)는 여기서 제외
}
```

---

**Domain vs DTO 차이:**

| 구분 | Domain (Student) | 입력 DTO (StudentForm) | 출력 DTO (StudentResponse) |
|---|---|---|---|
| 위치 | `domain/` | `dto/` | `dto/` |
| 역할 | DB 매핑, 비즈니스 로직 | 폼 입력 수신 + 검증 | API 응답 데이터 형성 |
| 어노테이션 | `@Table`, `@Id` | `@NotBlank`, `@Email` | 없음 (순수 데이터) |
| Bean Validation | X | O | X |

---

**데이터 흐름:**

```java
// 폼 등록: StudentForm → Student 변환
@PostMapping
public String addStudent(@Valid @ModelAttribute StudentForm form, ...) {
    Student student = new Student(form.getName(), form.getStudentId(), form.getEmail());
    studentRepository.save(student);
}

// API 응답: Student → StudentResponse 변환
Student s = studentRepository.findById(id).get();
return new StudentResponse(s.getId(), s.getName(), s.getStudentId(), s.getEmail());
```

> 핵심: Domain ↔ DTO 변환은 Controller(또는 Service)에서 수행
> 실무에서는 MapStruct, ModelMapper 라이브러리로 자동화하기도 함
