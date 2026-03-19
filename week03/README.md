# Week 03 — SW 프레임워크 이론 (백엔드/프론트엔드)

> 한국공학대학교 IT경영전공 | 2026학년도 1학기
> Spring Boot 3.5.x · Java 21 · Gradle · H2

---

## 학습 목표

1. Spring Boot, Django, Express.js 특징을 비교하고 적절한 선택 기준을 제시할 수 있다
2. React, Vue, Angular의 차이점과 SSR/CSR 렌더링 방식을 설명할 수 있다
3. MVC 패턴의 구조를 비교하고 계층형 아키텍처의 각 계층 역할을 식별할 수 있다
4. `@RestController`로 JSON REST API를 작성하고 HTTP 요청을 처리할 수 있다
5. H2 DB + Spring Data JDBC로 CRUD를 수행하고 프로젝트 구조를 이해할 수 있다

---

## 이론 요약

### Part 01 — 백엔드 프레임워크 비교

| | Spring Boot (Java) | Django (Python) | Express.js (Node.js) |
|---|---|---|---|
| 구조 | 계층형 + MVC | MVT 패턴 | 미들웨어 체인 |
| 철학 | Convention over Configuration | Batteries Included | 미니멀 (필요한 것만) |
| 강점 | 엔터프라이즈 안정성, IoC/DI | Admin 자동생성, 빠른 초기 개발 | 비동기 처리, JS 단일 언어 |
| 적합 도메인 | 대규모 금융/공공 시스템 | 데이터 집중형/AI 서비스 | 실시간 스트리밍/채팅 |

> 한국 공공 SI 프로젝트의 80%+ 가 Spring 기반. 전자정부 표준프레임워크 = Spring 기반.

### Part 02 — 전자정부 표준프레임워크 (eGovFrame)

행정안전부 산하 NIA에서 개발·보급하는 Java 기반 공공 개발 표준 프레임워크 (현재 v5.0).

| 계층 | 기술 | Spring 대응 |
|---|---|---|
| Presentation | Spring MVC, Tiles, Validator | `@Controller`, `@RestController` |
| Business Logic | Spring IoC/DI, AOP, @Transactional | `@Service`, `@Transactional` |
| Data Access | MyBatis, Spring Data JPA, DBCP | `@Repository`, JpaRepository |
| Integration | SOAP/REST API, EAI, MQ | RestTemplate, WebClient |
| Foundation | IoC Container, AOP, 로깅, 보안 | `@SpringBootApplication` |

### Part 03 — 프론트엔드 프레임워크

| 렌더링 | 방식 | 대표 기술 | 특징 |
|---|---|---|---|
| SSR | 서버에서 완성된 HTML 전송 | Thymeleaf, JSP | SEO 유리, 첫 로딩 빠름 **(이 과목 방식)** |
| CSR | 브라우저에서 JS로 DOM 생성 | React, Vue, Angular | 이후 전환 빠름, SEO 불리 |

**3대 CSR 프레임워크 비교:**
- **React** (Meta) — 컴포넌트 기반, 최대 생태계
- **Vue.js** (커뮤니티) — 완만한 학습 곡선
- **Angular** (Google) — 강한 타입, 엔터프라이즈 규모

### Part 04 — MVC 패턴과 계층형 아키텍처

**JSP Model 1 vs Spring MVC Model 2:**

| 항목 | JSP Model 1 | Spring MVC Model 2 |
|---|---|---|
| 코드 구조 | HTML + Java + SQL 혼재 | Controller / Service / View 분리 |
| 테스트 | 단위 테스트 불가 | 계층별 단위 테스트 가능 |
| 유지보수 | 변경 시 영향 예측 불가 | 한 계층 변경 시 다른 계층 영향 최소 |

**Spring Boot 계층형 아키텍처:**

```
Client → Presentation(@Controller) → Business Logic(@Service) → Data Access(@Repository) → DB
```

| 계층 | 어노테이션 | 역할 | 패키지 |
|---|---|---|---|
| Presentation | `@Controller` / `@RestController` | HTTP 요청·응답 | `controller/` |
| Business Logic | `@Service` / `@Transactional` | 비즈니스 로직, 트랜잭션 | `service/` |
| Data Access | `@Repository` / ListCrudRepository | DB CRUD | `repository/` |
| Domain/DTO | `@Table` / 필드 | 데이터 구조 정의 | `domain/`, `dto/` |

### Part 05 — REST API 개념

REST = **RE**presentational **S**tate **T**ransfer (Roy Fielding, 2000)

| HTTP Method | CRUD | Spring 어노테이션 | URL 예시 |
|---|---|---|---|
| GET | 조회 (Read) | `@GetMapping` | `/api/students` |
| POST | 생성 (Create) | `@PostMapping` | `/api/students` |
| PUT | 수정 (Update) | `@PutMapping` | `/api/students/1` |
| DELETE | 삭제 (Delete) | `@DeleteMapping` | `/api/students/1` |

**`@Controller` vs `@RestController`:**
- `@Controller` — View 이름(문자열)을 반환 → Thymeleaf로 HTML 렌더링 (SSR)
- `@RestController` — 데이터(JSON)를 HTTP 응답 본문으로 반환 → Jackson이 자동 변환

---

## 파일 목록

| 파일명 | 프로젝트 내 경로 | 실습 | 설명 |
|---|---|---|---|
| `HelloController.java` | `controller/` | 실습 2 | 기본 MVC — `@Controller` |
| `hello.html` | `resources/templates/` | 실습 2 | Thymeleaf Hello 템플릿 |
| `GreetingService.java` | `service/` | 실습 3 | 비즈니스 로직 계층 |
| `GreetingController.java` | `controller/` | 실습 3 | 계층 분리 — 생성자 DI |
| `greeting.html` | `resources/templates/` | 실습 3 | Thymeleaf Greeting 템플릿 |
| `Student.java` | `domain/` | 실습 4 | 도메인 클래스 (`@Table`) |
| `StudentResponse.java` | `dto/` | 실습 4 | API 응답 DTO |
| `StudentRepository.java` | `repository/` | 실습 4 | ListCrudRepository 상속 |
| `StudentApiController.java` | `controller/` | 실습 4 | `@RestController` JSON API |
| `DataInitializer.java` | `(root)` | 실습 4 | 앱 시작 시 초기 데이터 삽입 |
| `application.yml` | `resources/` | 실습 4 | H2 DB 설정 |

---

## 프로젝트 생성

1. [https://start.spring.io](https://start.spring.io) 접속
2. 아래 설정으로 프로젝트 생성:

| 항목 | 값 |
|---|---|
| Project | Gradle - Groovy |
| Language | Java |
| Spring Boot | 3.5.x |
| Group | `kr.ac.tukorea` |
| Artifact | `swframework` |
| Packaging | Jar |
| Java | 21 |

3. **의존성 4개 추가:**
   - `Spring Web` — REST API 및 MVC 핵심
   - `Thymeleaf` — 서버 사이드 템플릿 엔진 (SSR)
   - `Spring Data JDBC` — DB 연동 및 JDBC
   - `H2 Database` — 개발/테스트용 인메모리 DB

4. ZIP 다운로드 → 압축 해제 → IntelliJ에서 Open → Gradle Sync 완료 확인

---

## 실습 단계

### 실습 1 — 프로젝트 생성

위 [프로젝트 생성](#프로젝트-생성) 절차대로 프로젝트를 만들고 `./gradlew bootRun`이 BUILD SUCCESS로 뜨면 완료.

### 실습 2 — Spring MVC Hello 만들기

`HelloController.java`를 `controller/` 패키지에 생성하고 `hello.html` 템플릿을 추가한다.

- `@Controller` — View 이름을 반환, Thymeleaf가 HTML을 렌더링
- `model.addAttribute("name", "SW프레임워크")` — 템플릿에 데이터 전달
- `th:text="${name}"` — Thymeleaf 문법으로 값 출력

**예상 출력:** `안녕하세요, SW프레임워크!`

### 실습 3 — 계층 분리 (Controller + Service)

`GreetingService.java`를 `service/` 패키지에, `GreetingController.java`를 `controller/` 패키지에 생성.

- Controller는 요청/응답 처리만 담당 **(thin layer)**
- Service는 비즈니스 로직 집중 **(fat layer)**
- 생성자 주입(DI)으로 `GreetingService`를 주입받아 결합도를 낮춤

**예상 출력:** `홍길동님, SW프레임워크에 오신 것을 환영합니다!`

### 실습 4 — `@RestController` + JSON 응답 (H2 DB 연동)

아래 파일들을 각 패키지에 추가하고 `application.yml`을 설정한다.

**패키지 구조:**
```
src/main/java/kr/ac/tukorea/swframework/
├── controller/
│   └── StudentApiController.java   ← @RestController
├── domain/
│   └── Student.java                ← @Table
├── dto/
│   └── StudentResponse.java        ← DTO (API 응답 전용)
├── repository/
│   └── StudentRepository.java      ← ListCrudRepository 상속
└── DataInitializer.java            ← 초기 데이터 삽입
```

**흐름:** `Client → @RestController → Repository → H2 DB → StudentResponse(DTO) → JSON`

---

## 실행 및 확인

```bash
# macOS / Linux
./gradlew bootRun

# Windows
.\gradlew bootRun
```

| URL | 설명 | 예상 응답 |
|---|---|---|
| `http://localhost:8080/hello` | 실습 2 — HTML 페이지 | Thymeleaf 렌더링 |
| `http://localhost:8080/greeting?name=홍길동` | 실습 3 — 계층 분리 | Thymeleaf 렌더링 |
| `http://localhost:8080/api/hello` | 실습 4 — JSON | `{"message":"안녕하세요!", ...}` |
| `http://localhost:8080/api/students` | 실습 4 — 전체 목록 | `[{"id":1,"name":"홍길동","major":"IT경영"}, ...]` |
| `http://localhost:8080/api/students/1` | 실습 4 — 단건 조회 | `{"id":1,"name":"홍길동","major":"IT경영"}` |
| `http://localhost:8080/h2-console` | H2 웹 콘솔 | JDBC URL: `jdbc:h2:mem:testdb` |

---

## 주의사항

| 구분 | 내용 |
|---|---|
| Controller에 비즈니스 로직 금지 | Controller는 요청/응답 처리(thin layer)만. 비즈니스 로직은 반드시 Service에 위임 |
| 도메인 클래스 직접 노출 금지 | API 응답에 도메인 클래스(`@Table`)를 직접 반환하면 DB 구조가 노출됨. 반드시 DTO로 변환 |
| 역방향 호출 금지 | `Controller → Service → Repository` 순서만 허용. 역방향 의존은 아키텍처 붕괴 |
| 프레임워크 선택 기준 | Spring Boot가 무조건 최선이 아님. 프로젝트 특성에 맞는 기술을 선택할 것 |
| MyBatis `${}` 사용 금지 | `#{}` 파라미터 바인딩은 PreparedStatement로 SQL Injection을 방지. `${}`는 직접 삽입이므로 절대 사용 금지 |
