# Lab 05 — HttpSession + LoginInterceptor (W07 통합)

> "W07에서 만든 세션·인터셉터가 W09 Student CRUD를 어떻게 보호하는가"
>
> **swframework 현황**: LoginInterceptor + WebConfig + LoginController가 9주차까지 완성 — 본 lab은 동작 분석 + 응용

---

## swframework 적용 현황

### LoginInterceptor (이미 적용됨)

`interceptor/LoginInterceptor.java`:

```java
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            log.info("비로그인 사용자 접근 차단: {}", req.getRequestURI());
            res.sendRedirect("/login");
            return false;
        }
        return true;
    }
}
```

### WebConfig — 인터셉터 등록 + 예외 경로

`config/WebConfig.java`:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")                 // 모두 보호
                .excludePathPatterns(                    // 단, 아래는 예외
                        "/", "/login", "/logout",
                        "/css/**", "/js/**", "/images/**",
                        "/error",
                        "/active-sessions"
                );
    }
}
```

### LoginController — 계정 잠금 기능까지

`controller/LoginController.java`의 핵심:
- **로그인 5회 실패 시 5분 잠금** (Brute Force 방지)
- `failCountMap` (ConcurrentHashMap) — 사용자별 실패 횟수
- `lockTimeMap` — 잠금 시점 기록
- 성공 시 `session.setAttribute("loginUser", LoginForm)` 저장
- `admin` → ADMIN 권한 / 그 외 → USER 권한

> swframework 실 동작 — week10.http의 `[Login]` 요청으로 로그인 후 모든 `/students/**` 접근.

---

## 학습 포인트

- **`HandlerInterceptor.preHandle()`** — Controller 진입 전 횡단 처리
- **`HttpSession`** — 서버 메모리에 사용자 상태 (`loginUser` 키)
- **excludePathPatterns** — 로그인 페이지·정적 리소스는 인터셉터 제외 (순환 차단 방지)
- **계정 잠금 패턴** — 실패 횟수 + 잠금 시간 (5회 5분 — Brute Force 대비)
- **`@SessionAttribute`** — Controller 메서드에서 세션 값 추출

---

## 인증 흐름 다이어그램

```
[Browser]
   │  GET /students/1
   ▼
[DispatcherServlet]
   │
   ▼  HandlerInterceptor.preHandle()
[LoginInterceptor]
   │
   │  session.getAttribute("loginUser") == null?
   │
   ├─ Yes  →  res.sendRedirect("/login");   return false;
   │         (요청 차단 — Controller 진입 X)
   │
   └─ No   →  return true;
                │
                ▼
       [StudentController.detail(1)]
                │
                ▼
       [StudentService.findById(1)]
                │
                ▼  null이면 throw EntityNotFoundException (Lab 02 적용 후)
       [Student] → student/detail.html
```

---

## 계정 잠금 패턴 (LoginController 발췌)

```java
private final Map<String, Integer>       failCountMap = new ConcurrentHashMap<>();
private final Map<String, LocalDateTime> lockTimeMap  = new ConcurrentHashMap<>();
private static final int MAX_FAIL_COUNT = 5;
private static final int LOCK_MINUTES   = 5;

if (isAccountLocked(loginId)) {
    model.addAttribute("error",
            "로그인 " + MAX_FAIL_COUNT + "회 실패로 계정이 잠겼습니다. "
                    + LOCK_MINUTES + "분 후 다시 시도해주세요.");
    return "login";
}

if (userRepository.authenticate(loginId, password)) {
    failCountMap.remove(loginId);              // 성공 시 카운트 초기화
    lockTimeMap.remove(loginId);
    session.setAttribute("loginUser", new LoginForm(loginId, name, role));
} else {
    int fails = failCountMap.merge(loginId, 1, Integer::sum);
    if (fails >= MAX_FAIL_COUNT) {
        lockTimeMap.put(loginId, LocalDateTime.now());
    }
}
```

---

## 세션 사용자 추출 패턴

### Controller에서 직접 추출 (현재 swframework 방식)

```java
@PostMapping
public String addStudent(HttpSession session, ...) {
    LoginForm loginUser = (LoginForm) session.getAttribute("loginUser");
    log.info("학생 등록 by {}", loginUser.getLoginId());
    ...
}
```

### `@SessionAttribute` 어노테이션 (더 깔끔)

```java
@PostMapping
public String addStudent(@SessionAttribute("loginUser") LoginForm loginUser, ...) {
    log.info("학생 등록 by {}", loginUser.getLoginId());
    ...
}
```

---

## Thymeleaf — 로그인 사용자 정보 표시

`student/list.html` 또는 fragments/layout.html:

```html
<nav>
    <span th:if="${session.loginUser != null}">
        [[${session.loginUser.name}]] (<span th:text="${session.loginUser.role}">USER</span>) 님 환영합니다
        <a th:href="@{/logout}">로그아웃</a>
    </span>
    <span th:unless="${session.loginUser != null}">
        <a th:href="@{/login}">로그인</a>
    </span>
</nav>
```

---

## 확인 포인트

- [ ] 비로그인 상태로 `/students` 접속 → `/login`으로 자동 리다이렉트
- [ ] `admin/1234`로 로그인 → `/students` 접속 가능
- [ ] 잘못된 비번 5회 연속 입력 → 계정 5분간 잠금
- [ ] 로그아웃 후 `/students` 다시 접근 → `/login`으로 리다이렉트
- [ ] 정적 리소스(`/css/style.css`)는 비로그인에도 접근 가능 (excludePathPatterns)

---

## week10.http (Lab05 영역)

```http
### [Login] 세션 획득
POST /login
loginId=admin&password=1234
→ 302 /students

### 로그인 후 학생 목록 접근 가능
GET /students
→ 200 + 3건 표시

### 로그아웃 후 다시 접근
POST /logout
→ 302 /login
GET /students  (세션 없음)
→ 302 /login
```

---

## 주차 연결

- **W07** HttpSession + Cookie + LoginInterceptor → swframework의 기반
- **W10 Lab 02** `@ControllerAdvice` → `AccessDeniedException` 도 한 곳에서 처리 가능 (응용)
- **W10 Lab 03** `@Valid` → 로그인 폼도 동일 검증 패턴 적용 가능
- **W11+** Spring Security 도입 시 — `SecurityContext` 가 HttpSession 대체

## 운영 보안 한 줄

> "프론트에서 버튼을 숨기는 건 UX. 백엔드에서 한 번 더 막는 게 보안."
