# 7주차 핵심 용어집

강의 자료의 비유와 설명을 바탕으로 정리한 용어집입니다. 처음 접하는 개념은 비유를 통해 먼저 감을 잡고, 정확한 정의로 마무리하세요.

---

## 1. HTTP 무상태 (Stateless)

**HTTP Stateless**
> "서버는 금붕어 — 매 요청이 독립적이고, 이전 요청을 기억하지 못함"

HTTP 프로토콜은 각 요청을 독립적으로 처리하며, 이전 요청의 상태를 유지하지 않습니다.

```
요청 1: "나 홍길동이야, 로그인할게"  → 서버: "OK, 환영합니다"
요청 2: "내 정보 보여줘"              → 서버: "누구세요?"  ← 이전 요청을 기억 못함!
```

| 장점 | 단점 |
|---|---|
| 서버 확장 쉬움 (Scale-out) | 사용자 식별 불가 |
| 서버 자원 절약 | 매번 인증 필요 |
| 요청 독립적 처리 가능 | 연속적 작업 어려움 |

> 해결 방법: 세션(Session) 또는 쿠키(Cookie)로 상태를 보완

---

## 2. 세션 (Session)

**Session**
> 비유: "보관함 — 짐(데이터)은 서버에 안전 보관, 직원만 접근 가능 (보안 높음)"

서버 측에 사용자 데이터를 저장하는 메커니즘입니다. JSESSIONID 쿠키를 열쇠로 사용하여 특정 보관함을 식별합니다.

```
로그인 → 서버: 세션 생성 + JSESSIONID 발급
       → 브라우저: JSESSIONID 쿠키 저장
이후 요청 → 브라우저: JSESSIONID 자동 전송
         → 서버: JSESSIONID로 세션(보관함) 찾기 → 사용자 식별
```

---

**HttpSession**
Spring Boot에서 서블릿 세션을 다루는 인터페이스입니다.

```java
// 세션에 데이터 저장 (로그인 처리)
session.setAttribute("loginUser", loginId);

// 세션에서 데이터 조회 (로그인 상태 확인)
String loginUser = (String) session.getAttribute("loginUser");

// 세션 무효화 (로그아웃 처리)
session.invalidate();
```

| 메서드 | 용도 | 사용 시점 |
|---|---|---|
| `setAttribute(key, value)` | 세션에 데이터 저장 | 로그인 성공 |
| `getAttribute(key)` | 세션에서 데이터 조회 | 로그인 상태 확인 |
| `invalidate()` | 세션 전체 무효화 | 로그아웃 |
| `getSession(false)` | 세션이 없으면 null 반환 | 인터셉터에서 사용 |
| `getSession(true)` | 세션이 없으면 새로 생성 | 기본값 |

---

**JSESSIONID**
서버가 발급하는 세션 식별자(번호표)입니다. 쿠키로 브라우저에 저장됩니다.

> 비유: "물품보관소 번호표 — 번호표 분실 = 세션 하이재킹!"

```
Set-Cookie: JSESSIONID=abc123; Path=/; HttpOnly
```

개발자 도구 확인: Application 탭 → Cookies → JSESSIONID

---

## 3. 쿠키 (Cookie)

**Cookie**
> 비유: "명찰 — 손님이 직접 들고 다님, 누구나 볼 수 있음 (보안 낮음)"

클라이언트(브라우저)에 저장되는 키-값 쌍 데이터입니다.

| 구분 | 세션 (Session) | 쿠키 (Cookie) |
|---|---|---|
| 저장 위치 | 서버 (메모리 또는 DB) | 클라이언트 (브라우저) |
| 보안성 | 높음 (서버 관리, ID만 노출) | 낮음 (로컬 노출, 탈취 가능) |
| 데이터 형식 | Object (Java 객체 가능) | String (문자열만) |
| 만료 | 브라우저 종료 또는 타임아웃(기본 30분) | Max-Age 설정까지 유지 |
| 용량 | 서버 메모리 의존 (사실상 무제한) | 약 4KB 제한 |
| 접근성 | 서버 측에서만 접근 가능 | JavaScript로 접근 가능 |

---

**Cookie 보안 속성**

| 속성 | 설명 | 방어 대상 |
|---|---|---|
| **HttpOnly** | JavaScript에서 쿠키 접근 차단 (`document.cookie` 불가) | XSS |
| **Secure** | HTTPS에서만 쿠키 전송 | 네트워크 도청 |
| **SameSite** | 외부 도메인 요청 시 쿠키 전송 제어 | CSRF |

```yaml
# application.yml 설정
server.servlet.session.cookie.http-only: true
server.servlet.session.cookie.secure: true      # 운영 환경
server.servlet.session.cookie.same-site: lax    # 기본값
```

---

## 4. 인터셉터 (HandlerInterceptor)

**HandlerInterceptor**
> "Controller 입구의 경비원 — 세션 확인 후 출입 허가/거부"

DispatcherServlet → Controller 사이에서 공통 로직을 실행하는 Spring MVC 컴포넌트입니다.

```
Client 요청 → DispatcherServlet → [Interceptor] → Controller
                                   preHandle()
                                   true → 통과
                                   false → 차단 + 리다이렉트
```

---

**3단계 콜백**

| 메서드 | 실행 시점 | 주 용도 |
|---|---|---|
| `preHandle()` | Controller 실행 전 | 세션 체크, 권한 검증 |
| `postHandle()` | Controller 실행 후 | Model 데이터 후처리 (선택) |
| `afterCompletion()` | 응답 완료 후 | 리소스 정리 (선택) |

```java
@Override
public boolean preHandle(HttpServletRequest request,
                         HttpServletResponse response,
                         Object handler) throws Exception {
    HttpSession session = request.getSession(false); // 세션 없으면 null
    if (session == null || session.getAttribute("loginUser") == null) {
        response.sendRedirect("/login");
        return false; // Controller 진입 차단
    }
    return true; // Controller 진입 허용
}
```

---

**WebConfig 인터셉터 등록**

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
            .addPathPatterns("/**")              // 모든 경로에 적용
            .excludePathPatterns(
                "/login", "/logout",             // 로그인/로그아웃
                "/css/**", "/js/**", "/error"    // 정적 리소스, 에러
            );
    }
}
```

> **주의:** `/login`을 exclude하지 않으면 → 무한 리다이렉트 발생!

---

**인터셉터 vs 서블릿 필터 vs AOP**

| 구분 | 서블릿 필터 | 인터셉터 | AOP |
|---|---|---|---|
| 실행 위치 | DispatcherServlet **이전** | DispatcherServlet **이후** | 메서드 실행 전후 |
| 적용 대상 | 모든 요청 (URL 패턴) | Controller 요청만 | Bean 메서드 (Pointcut) |
| Spring Bean | 사용 어려움 | 사용 가능 | 완전 지원 |
| 주 용도 | 인코딩, XSS 필터 | 로그인 체크, 로깅 | 트랜잭션, 성능 측정 |
| 학습 주차 | W06 Lab05 | **W07 Lab02** | W05 |

> Week 05 AOP가 Service 레벨이라면, Interceptor는 Controller 레벨의 공통 처리

---

## 5. PRG 패턴 (Post-Redirect-Get)

**PRG**
> "POST 처리 후 반드시 Redirect → GET으로 돌아온다"

로그인 성공 후 새로고침(F5)으로 POST가 재전송되는 것을 방지합니다.

```java
// ✅ PRG 적용 (로그인)
@PostMapping("/login")
public String login(...) {
    session.setAttribute("loginUser", loginId);
    return "redirect:/students"; // → 302 → GET /students
}
```

Week 06에서 학생 등록에 PRG를 적용한 것과 동일한 원리입니다.

---

## 6. Serializable (직렬화)

**Serializable**
> "Java 객체를 바이트 스트림으로 변환하여 전송/저장 가능하게 하는 인터페이스"

세션에 저장하는 DTO 객체는 Serializable을 구현하는 것이 권장됩니다.

```java
public class LoginForm implements Serializable {
    private static final long serialVersionUID = 1L; // 버전 관리

    private String loginId;
    private String name;
    private String role;
}
```

| 이유 | 설명 |
|---|---|
| Redis 세션 저장 | 세션을 외부 저장소에 직렬화하여 저장 |
| 서버 클러스터링 | 여러 서버 간 세션 공유 시 필요 |
| 서버 재시작 | 세션 복원에 필요 |

> 주의: **비밀번호는 절대 세션에 저장하지 않음!** ID/이름/권한만 저장

---

## 7. BCrypt 비밀번호 해싱

**BCrypt**
> "단방향 금고 — 넣을 수는 있지만 원본을 꺼낼 수 없음"

단방향 해시 알고리즘으로, 같은 입력값이라도 매번 다른 해시값을 생성합니다 (Salt 자동 포함).

```java
// 회원가입: 평문 → BCrypt 해시
String hashed = PasswordUtil.encode("1234");
// → "$2a$10$xKz..." (매번 다른 값)

// 로그인: 평문 vs 해시 비교
PasswordUtil.matches("1234", hashed);  // → true
PasswordUtil.matches("5678", hashed);  // → false

// ⚠️ 절대 하면 안 되는 것:
PasswordUtil.encode("1234").equals(PasswordUtil.encode("1234")); // → 항상 false!
```

| 구분 | 평문 저장 | BCrypt 해시 저장 |
|---|---|---|
| DB 유출 시 | 모든 비밀번호 즉시 노출 | 원본 역산 불가 |
| 레인보우 테이블 | 무방비 | Salt로 방어 |
| 같은 비밀번호 | 동일 값 저장 | 매번 다른 해시값 |

**의존성:**

```gradle
implementation 'org.springframework.security:spring-security-crypto'
```

> 전체 Spring Security가 아닌 crypto 경량 모듈만 사용

---

## 8. JWT (JSON Web Token)

**JWT**
> "신분증 — 서버가 발급하고 클라이언트가 보관, 서버에 저장소 불필요"

Header.Payload.Signature 점(.)으로 구분된 3파트 구조의 토큰입니다.

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJob25nMTIzIn0.SflKxwRJSM...
[     Header      ]  [       Payload        ]  [ Signature ]
```

| 파트 | 내용 | 특징 |
|---|---|---|
| Header | 알고리즘, 토큰 타입 | Base64 인코딩 |
| Payload | 사용자 정보 (Claims) | **누구나 볼 수 있음! 민감정보 금지** |
| Signature | 위변조 검증 | 서버만 비밀키를 알고 있음 |

> 주의: Base64 인코딩 ≠ 암호화! Payload는 누구나 디코딩 가능

---

**세션 vs JWT 비교**

| 구분 | 세션 기반 (Stateful) | JWT 기반 (Stateless) |
|---|---|---|
| 저장 위치 | 서버 (메모리/Redis) | 클라이언트 (브라우저) |
| 즉시 무효화 | `invalidate()` 가능 | 어려움 (만료까지 대기) |
| 서버 확장 | 세션 공유 필요 | 저장소 불필요 (Scale-out 유리) |
| 적합 | 전통적 웹 앱, **이 수업** | REST API, SPA, 모바일 |

> 이 수업에서는 **세션 방식**으로 구현. JWT는 팀 프로젝트 필요 시 도입

---

## 9. 웹 보안 4대 위협

**평문 비밀번호**
DB 유출 시 모든 비밀번호가 즉시 노출되는 위협입니다.
- **방어:** BCrypt encode() → 해시값 저장, matches()로 검증
- **관련:** Week 07 Lab 04

---

**XSS (Cross-Site Scripting)**
악성 스크립트를 삽입하여 다른 사용자의 브라우저에서 실행하는 공격입니다.
- **방어:** `th:text` 이스케이프 (기본), HttpOnly 쿠키 설정
- **관련:** Week 06 Lab 05

---

**CSRF (Cross-Site Request Forgery)**
인증된 세션을 악용하여 사용자 모르게 위조 요청을 보내는 공격입니다.

```
① 피해자가 은행에 로그인 (세션 유지)
② 공격자가 피싱 메일로 악성 링크 전송
③ 피해자가 클릭 → 악성 사이트 방문
④ 숨겨진 Form이 자동 제출 (JSESSIONID 자동 첨부)
⑤ 서버는 정상 요청으로 판단 → 이체 실행!
```

- **방어:** CSRF 토큰 (Thymeleaf `th:action` 사용 시 자동 삽입), SameSite 쿠키
- **관련:** Week 07 세션 쿠키 보호

---

**SQL Injection**
입력란에 SQL 구문을 삽입하여 DB를 조작하는 공격입니다.

```
입력: admin' OR '1'='1
→ WHERE id='admin' OR '1'='1'  ← 항상 true → 로그인 우회!
```

- **방어:** MyBatis `#{}` 파라미터 바인딩 (PreparedStatement), `${}` 사용 금지
- **관련:** Week 09 MyBatis 실습

---

## 10. 인증(Authentication) vs 인가(Authorization)

| 구분 | 인증 (Authentication) | 인가 (Authorization) |
|---|---|---|
| 질문 | **누구**인가? | **무엇**을 할 수 있는가? |
| 방법 | ID/PW 검증, 로그인 처리 | 권한(ROLE) 기반 접근 제어 |
| 예시 | 로그인 폼 → 세션 저장 | ADMIN만 삭제 가능 |
| 이 수업 | Lab 01~04 | LoginForm.role, isAdmin() |

---

## 11. Spring Security (개념)

**Spring Security**
인증(Authentication)과 인가(Authorization)를 포괄하는 Spring의 보안 프레임워크입니다.

| 구성 요소 | 설명 |
|---|---|
| Security Filter Chain | HTTP 요청 → 보안 필터 체인 → 인증 → 인가 → Controller |
| UserDetailsService | 사용자 정보 조회 인터페이스 |
| BCryptPasswordEncoder | 비밀번호 암호화 (이번 주 PasswordUtil과 동일) |

> 이번 주: HttpSession 직접 구현으로 원리 학습
> 팀 프로젝트: 필요 시 Spring Security 도입 (점진적 전략)

---

## 12. 세션 타임아웃 & 계정 잠금

**세션 타임아웃**
마지막 요청으로부터 지정 시간 동안 요청이 없으면 세션이 자동 만료됩니다.

```yaml
server.servlet.session.timeout: 30m  # 기본값 30분, 최소 1분
```

> 30분 동안 아무 요청이 없으면 → 세션 자동 삭제 → 재로그인 필요

---

**계정 잠금 (Account Lockout)**
Brute Force(무차별 대입) 공격을 방어하기 위해 로그인 실패 횟수를 제한합니다.

```
1~4회 실패: "남은 시도: N회" 메시지 표시
5회 실패:   계정 잠금 (5분간)
5분 경과:   자동 잠금 해제
```

> 실무에서는 실패 횟수를 DB에 저장하고, IP 기반 차단도 병행

---

## 13. 비교 정리

| 구분 | Session | Cookie |
|---|---|---|
| 저장 위치 | 서버 | 클라이언트 |
| 보안 | 높음 | 낮음 |
| 용량 | 사실상 무제한 | 4KB |
| 접근 | 서버만 | JavaScript 가능 |

| 구분 | Session (Stateful) | JWT (Stateless) |
|---|---|---|
| 서버 저장소 | 필요 | 불필요 |
| 즉시 무효화 | 가능 | 어려움 |
| Scale-out | 세션 공유 필요 | 용이 |
| 적합 | 웹 앱 (이 수업) | REST API, SPA |

| 구분 | Interceptor | Servlet Filter | AOP |
|---|---|---|---|
| 위치 | DispatcherServlet 이후 | DispatcherServlet 이전 | 메서드 실행 전후 |
| 대상 | Controller 요청 | 모든 HTTP 요청 | Bean 메서드 |
| 용도 | 로그인 체크 | XSS 필터 | 트랜잭션, 로깅 |
| 주차 | **W07** | W06 | W05 |

---

## 14. 자주 발생하는 문제 & 해결

| 문제 | 원인 & 해결 |
|---|---|
| 무한 리다이렉트 | `excludePathPatterns`에 `/login` 누락 → 추가 |
| 비밀번호 세션 저장 | 세션에 비밀번호 저장 금지 → ID/이름/권한만 |
| PRG 미적용 | POST 성공 후 반드시 `redirect:` → 새로고침 시 재전송 방지 |
| encode() 결과 비교 | `encode()` 끼리 비교 = 항상 false → 반드시 `matches()` 사용 |
| 세션 타임아웃 미설정 | `application.yml`: `session.timeout=30m` (기본값 확인) |
| CSS 깨짐 | `excludePathPatterns`에 `/css/**` 누락 → 정적 리소스 제외 |
| BCrypt 의존성 에러 | `spring-security-crypto` 의존성 추가 + Gradle 리프레시 |
| getSession() null | `getSession(false)` 사용 시 세션이 없으면 null → null 체크 필수 |

---

## 15. Spring Annotation 종합 정리 (7주차 추가)

| 어노테이션 | 설명 | 역할 |
|---|---|---|
| `@Configuration` | Spring 설정 클래스 선언 | 설정 |
| `@Repository` | 데이터 접근 계층 Bean 등록 | 계층 |
| `@RequestParam` | 요청 파라미터 바인딩 | MVC |
| `@Getter/@Setter` | Lombok getter/setter 자동 생성 | Lombok |
| `@NoArgsConstructor` | 기본 생성자 자동 생성 | Lombok |
| `@AllArgsConstructor` | 전체 필드 생성자 자동 생성 | Lombok |
| `@Slf4j` | Logger 자동 생성 (`log.info(...)`) | Lombok |
