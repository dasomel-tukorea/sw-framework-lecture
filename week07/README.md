# Week 07 — 세션 처리 & 웹 보안 기초

> "W06 Form을 로그인으로 확장, 세션으로 사용자 식별"

---

## 이번 주 학습 목표

| # | 목표 | 관련 Lab |
|---|------|---------|
| 1 | HTTP 무상태(Stateless) 특성과 세션/쿠키 필요성 이해 | 전체 |
| 2 | HttpSession 로그인/로그아웃 구현 | Lab 01 |
| 3 | HandlerInterceptor 기반 접근 제어 공통화 | Lab 02 |
| 4 | 세션에 DTO 객체 저장 (Serializable + Lombok) | Lab 03 |
| 5 | BCrypt 비밀번호 암호화 (평문 저장 금지) | Lab 04 |
| 6 | 로그인 실패 처리 & 세션 타임아웃 설정 | Lab 05 |
| 7 | JWT 개념 이해 (세션 vs 토큰 비교) | 이론 |
| 8 | 웹 보안 4대 위협 원리와 방어 방법 | 이론 |

---

## 3~7주차 연결 지도

```
Week 03  Spring MVC & 계층 아키텍처
         Controller → Service → Repository 역할 분리

Week 04  IoC/DI & Bean
         생성자 주입으로 느슨한 결합

Week 05  AOP & Bean 생명주기
         공통 기능 분리 (로깅·보안)

Week 06  View & Form 처리
         Thymeleaf, PRG, Validation, XSS 방어

Week 07  세션 & 웹 보안  ← 이번 주
         로그인/로그아웃 + 인터셉터 + 4대 보안 위협 방어
```

---

## 사전 준비 (swframework 프로젝트)

### 1. build.gradle — BCrypt 의존성 추가 (Lab 04부터 필요)

```gradle
dependencies {
    // 기존 의존성들 ...
    implementation 'org.springframework.security:spring-security-crypto' // 추가
}
```

> Gradle 새로고침 후 진행

### 2. list.html — 레이아웃 헤더 주석 해제 (Lab 03부터 필요)

6주차에서 `templates/student/list.html`의 레이아웃 헤더가 주석 처리되어 있습니다.
7주차 로그인 정보(사용자 이름, 권한, 로그아웃 버튼)를 표시하려면 **주석을 해제**해야 합니다.

```html
<!-- 변경 전 (주석 처리 상태) -->
<!-- Lab 06 완료 후 아래 주석 해제
<header th:replace="~{fragments/layout :: header}"></header>
-->

<!-- 변경 후 (주석 해제) -->
<header th:replace="~{fragments/layout :: header}"></header>
```

> 주석 해제하지 않으면 로그인 후에도 헤더에 사용자 정보가 표시되지 않습니다.

### 3. XssEscapeFilterConfig 중복 파일 확인

6주차 실습에서 `XssEscapeFilterConfig.java`가 `controller/`와 `config/` 두 곳에 존재할 수 있습니다.
중복 시 Spring Bean 이름 충돌로 **서버가 시작되지 않습니다.**

```
ConflictingBeanDefinitionException: 
  Annotation-specified bean name 'xssEscapeFilterConfig' for bean class 
  [kr.ac.tukorea.swframework.controller.XssEscapeFilterConfig] conflicts with 
  existing, non-compatible bean definition of same name and class 
  [kr.ac.tukorea.swframework.config.XssEscapeFilterConfig]
```

**해결:** `controller/XssEscapeFilterConfig.java`를 삭제하고 `config/XssEscapeFilterConfig.java`만 유지합니다.

---

## 최종 파일 구조

```
src/main/java/kr/ac/tukorea/swframework/
├── controller/
│   └── LoginController.java       ← lab01 신규, lab03~05 순차 확장
├── config/
│   └── WebConfig.java             ← lab02 신규 (인터셉터 등록)
├── interceptor/
│   └── LoginInterceptor.java      ← lab02 신규 (로그인 체크)
├── dto/
│   └── LoginForm.java             ← lab03 신규 (세션 저장 DTO)
├── util/
│   └── PasswordUtil.java          ← lab04 신규 (BCrypt 유틸)
└── repository/
    └── UserRepository.java        ← lab04 신규 (메모리 사용자 저장소)

src/main/resources/
├── templates/
│   └── login.html                 ← lab01 신규 (로그인 페이지)
└── application.yml                ← lab05 세션 타임아웃 설정 추가

week07/
├── w07_dictionary.md              ← 7주차 핵심 용어집 (15개 용어)
└── README.md                      ← 이 파일
```

### Lab별 복사 파일 목록

| Lab | 복사할 파일 (lab0x/ → 프로젝트) | 주요 변경 내용 |
|-----|-------------------------------|--------------|
| **사전** | `controller/XssEscapeFilterConfig.java` 삭제 | config/에만 유지 (빈 이름 충돌 방지) |
| **Lab 01** | `LoginController.java` → `controller/` | 하드코딩 인증, HttpSession 사용 |
| | `login.html` → `templates/` | Thymeleaf 로그인 폼 |
| **Lab 02** | `LoginInterceptor.java` → `interceptor/` | HandlerInterceptor 구현 (interceptor/ 폴더 생성) |
| | `WebConfig.java` → `config/` | 인터셉터 등록 + 경로 패턴 설정 |
| **Lab 03** | `LoginForm.java` → `dto/` | Serializable DTO (세션 저장용) |
| | `LoginController.java` → `controller/` | LoginForm 객체를 세션에 저장 |
| | `templates/student/list.html` 주석 해제 | 레이아웃 헤더 활성화 (로그인 정보 표시) |
| **Lab 04** | `build.gradle` → 루트 | spring-security-crypto 의존성 추가 |
| | `PasswordUtil.java` → `util/` | BCrypt encode/matches (util/ 폴더 생성) |
| | `UserRepository.java` → `repository/` | BCrypt 해시 비밀번호 저장/검증 |
| | `LoginController.java` → `controller/` | UserRepository 생성자 주입 + BCrypt 인증 |
| **Lab 05** | `LoginController.java` → `controller/` | 실패 횟수 카운팅 + 계정 잠금 |
| | `application.yml` → `resources/` | 세션 타임아웃 + 쿠키 보안 설정 추가 |

---

## 실습 순서

---

### Lab 01 (35분) — HttpSession 로그인/로그아웃 (기본)

**목표:** HttpSession의 setAttribute/getAttribute/invalidate 3단계 흐름 체험

**추가 파일:**
- `controller/LoginController.java` — 하드코딩 인증 + 세션 저장
- `templates/login.html` — Thymeleaf 로그인 폼

**핵심 코드:**

```java
// 로그인 성공 → 세션 저장
session.setAttribute("loginUser", loginId);
return "redirect:/students"; // PRG 패턴

// 로그아웃 → 세션 무효화
session.invalidate();
return "redirect:/login";
```

**6주차 연결:**
- Form 전송 = 로그인 폼
- `@PostMapping` = 로그인 처리
- PRG 패턴 = redirect 적용
- `th:if` = 에러 메시지 조건 출력

**확인 포인트:**
1. 로그인 성공 → `/students`로 리다이렉트
2. 로그인 실패 → 에러 메시지 표시
3. 로그아웃 → 세션 삭제 확인
4. 개발자 도구 → Application → Cookies → JSESSIONID 쿠키 확인

**테스트 계정:** `admin` / `1234`, `guest` / `1234`

**실습 URL:**
- [http://localhost:8080/login](http://localhost:8080/login) — 로그인 폼
- [http://localhost:8080/students](http://localhost:8080/students) — 로그인 성공 후 이동

---

### Lab 02 (20분) — 인터셉터 로그인 체크 (심화)

**목표:** HandlerInterceptor로 비로그인 사용자 접근 차단 공통화

**추가 파일:**
- `interceptor/LoginInterceptor.java` — `preHandle()`에서 세션 체크
- `config/WebConfig.java` — 인터셉터 등록 + 경로 패턴 설정

**핵심 코드:**

```java
// LoginInterceptor.java
HttpSession session = request.getSession(false); // 세션 없으면 null
if (session == null || session.getAttribute("loginUser") == null) {
    response.sendRedirect("/login");
    return false; // Controller 진입 차단
}
return true; // Controller 진입 허용
```

```java
// WebConfig.java
registry.addInterceptor(new LoginInterceptor())
    .addPathPatterns("/**")
    .excludePathPatterns("/", "/login", "/logout", "/css/**", "/js/**", "/error");
```

**Week 05 AOP와의 연결:**
- AOP: Service 메서드 전후 공통 처리
- Interceptor: Controller 진입 전 공통 처리
- 둘 다 '횡단 관심사 분리' 원칙!

**주의사항:**
- `excludePathPatterns` 누락 시 → 로그인 페이지 자체도 차단 → 무한 리다이렉트!
- 정적 리소스(css, js, images) 반드시 제외

**확인:** 비로그인 상태로 `/students` 접속 → 자동으로 `/login`으로 리다이렉트

**실습 URL:**
- [http://localhost:8080/students](http://localhost:8080/students) — 비로그인 시 `/login`으로 리다이렉트 확인
- [http://localhost:8080/students/new](http://localhost:8080/students/new) — 비로그인 시 차단 확인

---

### Lab 03 (20분) — 세션에 객체 저장 (심화)

**목표:** LoginForm DTO를 세션에 저장, Thymeleaf에서 객체 필드 활용

**추가 파일:**
- `dto/LoginForm.java` — Serializable DTO (loginId, name, role)
- `controller/LoginController.java` — LoginForm 객체를 세션에 저장

**Lab 01과의 차이:**

```java
// Lab 01: String 저장
session.setAttribute("loginUser", loginId);

// Lab 03: 객체 저장
LoginForm loginUser = new LoginForm(loginId, "관리자", "ADMIN");
session.setAttribute("loginUser", loginUser);
```

**Thymeleaf에서 활용:**

```html
<!-- Lab 01: String이므로 직접 출력만 가능 -->
<span th:text="${session.loginUser}">admin</span>

<!-- Lab 03: 객체이므로 필드 접근 가능 -->
<span th:text="${session.loginUser.name}">관리자</span>
<span th:if="${session.loginUser.admin}">관리자 메뉴</span>
```

**확인:** 로그인 후 화면에 사용자 이름과 권한 표시

**실습 URL:**
- [http://localhost:8080/login](http://localhost:8080/login) — admin 로그인 → 관리자 이름/권한 표시 확인
- [http://localhost:8080/login](http://localhost:8080/login) — guest 로그인 → 게스트 이름/권한 표시 확인

---

### Lab 04 (20분) — BCrypt 비밀번호 암호화 (심화)

**목표:** 평문 비밀번호 저장의 위험성 이해, BCrypt encode/matches 사용법 습득

**추가 파일:**
- `build.gradle` — `spring-security-crypto` 의존성 추가
- `util/PasswordUtil.java` — BCrypt encode/matches 유틸리티
- `repository/UserRepository.java` — BCrypt 해시 비밀번호 저장/검증
- `controller/LoginController.java` — UserRepository 생성자 주입 + BCrypt 인증

**확인 포인트:**
- 같은 '1234' → 매번 다른 해시값 (Salt 자동 포함)
- `matches('1234', 해시값)` → true
- `matches('5678', 해시값)` → false
- `encode()` 결과끼리 비교 → 항상 false! (반드시 `matches()` 사용)

**서버 시작 로그 확인:**

```
UserRepository 초기화 완료 — 테스트 계정 2개 등록 (BCrypt 암호화 적용)
```

**실습 URL:**
- [http://localhost:8080/login](http://localhost:8080/login) — admin/1234 로그인 (BCrypt 인증)
- [http://localhost:8080/login](http://localhost:8080/login) — admin/wrong 로그인 실패 확인

---

### Lab 05 (25분) — 로그인 실패 처리 & 타임아웃 [도전]

**목표:** 실패 횟수 카운팅 → 계정 잠금, 세션 타임아웃 설정

**변경 파일:**
- `controller/LoginController.java` — 실패 횟수 + 계정 잠금 로직 추가
- `application.yml` — 세션 타임아웃 + 쿠키 보안 속성 설정

**핵심 코드:**

```java
// 실패 횟수 증가
int failCount = failCountMap.merge(loginId, 1, Integer::sum);

// 5회 실패 → 계정 잠금 (5분)
if (failCount >= MAX_FAIL_COUNT) {
    lockTimeMap.put(loginId, LocalDateTime.now());
}
```

```yaml
# application.yml
server:
  servlet:
    session:
      timeout: 30m          # 세션 타임아웃
      cookie:
        http-only: true      # XSS 방어 (JavaScript 쿠키 접근 차단)
        same-site: lax       # CSRF 방어 (외부 도메인 쿠키 전송 제어)
```

**확인:**
1. 잘못된 비밀번호 5회 입력 → "계정이 잠겼습니다" 메시지 확인
2. 남은 시도 횟수 표시 확인
3. 5분 경과 후 잠금 해제 확인
4. 로그인 성공 → 실패 횟수 초기화 확인

**실습 URL:**
- [http://localhost:8080/login](http://localhost:8080/login) — admin/wrong 5회 입력 → 계정 잠금 확인
- [http://localhost:8080/login](http://localhost:8080/login) — 5분 경과 후 admin/1234 정상 로그인 확인

---

## 전체 URL 목록

| HTTP | URL | 설명 | Lab |
|------|-----|------|-----|
| GET | [/login](http://localhost:8080/login) | 로그인 폼 | 01 |
| POST | /login | 로그인 처리 | 01 |
| POST | /logout | 로그아웃 처리 | 01 |
| GET | [/students](http://localhost:8080/students) | 학생 목록 (로그인 필요) | 02 |

---

## 확인 체크리스트

- [ ] 로그인 성공 시 `/students`로 리다이렉트되는가?
- [ ] 비로그인 상태에서 `/students` 직접 접속 시 `/login`으로 이동하는가?
- [ ] 로그아웃 후 `/students` 접속 시 `/login`으로 이동하는가?
- [ ] 잘못된 ID/PW 입력 시 에러 메시지가 표시되는가?
- [ ] 개발자 도구 → Application → Cookies에서 JSESSIONID 확인
- [ ] 로그아웃 후 JSESSIONID 쿠키 삭제 확인
- [ ] 서버 콘솔에 BCrypt 초기화 로그 출력 확인 (Lab 04)
- [ ] 5회 실패 시 계정 잠금 메시지 표시 (Lab 05)

---

## 웹 보안 4대 위협 요약

| 위협 | 원리 | 방어 | 관련 주차 |
|------|------|------|----------|
| 평문 비밀번호 | DB 유출 시 즉시 노출 | BCrypt 해싱 | **W07 Lab 04** |
| XSS | 악성 스크립트 삽입 | `th:text` 이스케이프 | **W06 Lab 05** |
| CSRF | 인증 세션 악용 위조 요청 | CSRF 토큰 (th:action) | **W07 세션 보호** |
| SQL Injection | 입력값으로 DB 조작 | `#{}` 파라미터 바인딩 | **W09 MyBatis** |

---

## 다음 주차 연결 (Week 08)

- 8주차는 **중간고사** + **팀 프로젝트 주제 선정**입니다.
- W03~07의 MVC + Thymeleaf + 세션 = 중간고사 범위의 전부
- 이번 주 인증/인터셉터가 팀 프로젝트 보안 설계의 기초가 됩니다.
