# 3주차 핵심 용어집

강의 자료의 비유와 설명을 바탕으로 정리한 용어집입니다. 처음 접하는 개념은 비유를 통해 먼저 감을 잡고, 정확한 정의로 마무리하세요.

---

## 1. 프레임워크 개념

**소프트웨어 프레임워크 (Software Framework)**
> "반제품 — 개발자가 채워야 할 틀을 미리 제공하는 것"

애플리케이션 개발에 필요한 공통 기능(보안, 로깅, DB 연결, 트랜잭션 등)을 미리 구현해 제공하는 재사용 가능한 소프트웨어 구조입니다. 개발자는 비즈니스 로직에만 집중하면 됩니다.

---

**IoC (Inversion of Control, 제어의 역전)**
> "할리우드 원칙: Don't call us, we'll call you"

일반적인 Library 방식에서는 개발자가 필요할 때 라이브러리를 직접 호출하지만, Framework 방식에서는 **프레임워크가 개발자의 코드를 대신 호출**합니다. 제어권이 개발자에서 프레임워크로 역전되는 것입니다.

| 구분 | Library | Framework |
|---|---|---|
| 제어권 | 개발자 | 프레임워크 |
| 호출 방향 | 개발자 → Library | Framework → 개발자 코드 |
| 예시 | Apache Commons | Spring Boot |

---

**프레임워크가 필요한 6가지 이유**

| 이유 | 설명 |
|---|---|
| 반복 공통 기능 방지 | 보안, 로깅, 트랜잭션 등 공통 코드를 프레임워크가 제공 |
| 검증된 아키텍처 | MVC, 계층형 등 수년간 실무 검증된 설계 패턴 적용 |
| 코드 품질 표준화 | 팀원 모두 동일한 구조, 리뷰·유지보수 용이 |
| 생산성 향상 | 초기 설정 단축, 비즈니스 로직에만 집중, 2~3배 속도 |
| 유지보수 용이 | 역할 분리로 변경 영향 범위 제한 |
| 팀 협업 효율화 | 일관된 코딩 스타일과 아키텍처로 협업 극대화 |

---

## 2. 백엔드 프레임워크

**Spring Boot (Java)**
계층형 + MVC 아키텍처 기반의 Java 백엔드 프레임워크입니다. Convention over Configuration 철학으로 설정을 최소화하고, IoC/DI, AOP, 트랜잭션 등을 제공합니다.
- 한국 공공 SI 프로젝트의 **80%+** 가 Spring 기반
- 전자정부 표준프레임워크의 기반 기술
- 이 과목 실습 언어

---

**Django (Python)**
> "'Batteries Included' — 필요한 것이 모두 기본 포함된 프레임워크"

MVT(Model-View-Template) 패턴 기반 Python 프레임워크입니다. Admin 페이지 자동 생성, ORM 내장으로 빠른 초기 개발이 가능합니다.
- 적합 도메인: 데이터 집중형/AI 서비스 (초기 인스타그램, 데이터 대시보드)

---

**Express.js (Node.js)**
미들웨어 체인 구조의 JavaScript 백엔드 프레임워크입니다. 미니멀한 설계로 필요한 것만 추가하며, 비동기 처리에 최적화되어 있습니다.
- 적합 도메인: 실시간 스트리밍/채팅 (넷플릭스, 실시간 메신저)

---

**eGovFrame (전자정부 표준프레임워크)**
행정안전부 산하 NIA에서 개발·보급하는 Java 기반 공공 개발 표준 프레임워크 (현재 v5.0)입니다. Spring Framework를 기반으로 공공 사업에 필요한 공통 기능을 패키징하여 제공합니다.
- 공통컴포넌트 약 **250종** 제공 (협업, 보안/권한, 시스템관리, 요소기술 등)
- 공공기관 정보시스템 구축 시 사실상 필수 적용 기술
- Spring Boot 학습 = eGovFrame 핵심 아키텍처 이해 → 취업 경쟁력

| 4대 환경 | 설명 |
|---|---|
| 개발환경 | Eclipse 기반 IDE, 22개 오픈소스 SW |
| 실행환경 | 응용 SW 실행 기반 (화면·업무·데이터·연계·배치·공통기반) |
| 운영환경 | 모니터링, 배포, 배치 관리 |
| 관리환경 | 프레임워크 현황 및 표준 적합성 관리 |

---

## 3. 프론트엔드 프레임워크

**SSR (Server-Side Rendering, 서버 사이드 렌더링)**
서버에서 HTML을 완성하여 브라우저로 전송하는 렌더링 방식입니다.
- SEO에 매우 유리 (크롤러가 즉시 분석 가능)
- 첫 화면 로딩 속도 빠름
- 대표: Thymeleaf, JSP
- **이 과목에서 사용하는 방식**

---

**CSR (Client-Side Rendering, 클라이언트 사이드 렌더링)**
서버는 빈 HTML과 JS만 전송하고, 브라우저의 JavaScript가 DOM을 생성하는 렌더링 방식입니다.
- 이후 화면 전환이 빠르고 동적 상호작용 우수
- SEO에 불리 (빈 HTML 수신)
- 대표: React, Vue.js, Angular

---

**React**
Meta(구 Facebook)가 개발한 컴포넌트 기반 CSR 프론트엔드 라이브러리입니다. 최대 생태계와 커뮤니티를 보유합니다.

**Vue.js**
커뮤니티 주도의 CSR 프론트엔드 프레임워크입니다. 완만한 학습 곡선으로 입문자에게 적합합니다.

**Angular**
Google이 개발한 강한 타입 기반의 엔터프라이즈급 CSR 프론트엔드 프레임워크입니다.

---

## 4. MVC 패턴

**MVC (Model-View-Controller)**
> "관심사의 분리(Separation of Concerns) — 화면, 로직, 데이터를 각 계층에서 독립 관리"

애플리케이션을 세 가지 역할로 분리하는 소프트웨어 설계 패턴입니다.

| 구성요소 | 역할 | Spring 대응 |
|---|---|---|
| Model | 데이터와 비즈니스 로직 | `@Service`, `@Entity`/`@Table` |
| View | 사용자에게 보여주는 화면 | Thymeleaf 템플릿 |
| Controller | 요청을 받아 Model과 View를 연결 | `@Controller`, `@RestController` |

---

**JSP Model 1**
HTML, Java 코드, SQL이 하나의 JSP 파일에 혼재하는 구조입니다.
- 단위 테스트 불가, 변경 시 영향 범위 예측 불가
- URL에 `.jsp` 확장자가 노출되어 기술 스택이 드러남 (보안 취약)
- 현재는 레거시로 간주됨 (공공 시스템에서 `.do` 패턴도 아직 사용)

---

**Spring MVC Model 2**
Controller → Service → View 구조로 역할을 완전히 분리한 방식입니다.
- 계층별 단위 테스트 가능
- 독립적 수정 → 다른 계층 영향 최소화
- RESTful URL 구조 (확장자 없음, 자원 중심)
- 현재 실무 표준 방식

---

**DispatcherServlet**
Spring MVC의 핵심 컴포넌트로, 모든 HTTP 요청을 가장 먼저 받아 적절한 Controller로 분배하는 **프론트 컨트롤러**입니다.

```
Client → DispatcherServlet → @Controller/@RestController → Service → View/JSON
```

---

## 5. 계층형 아키텍처

**Presentation 계층**
HTTP 요청을 수신하고 응답을 반환하는 계층입니다. 비즈니스 로직을 직접 처리하지 않고 Service에 위임합니다 (thin layer).
- 어노테이션: `@Controller`, `@RestController`
- 패키지: `controller/`

---

**Business Logic 계층**
핵심 비즈니스 로직을 처리하는 계층입니다. 여러 Repository를 조합하고 트랜잭션을 관리합니다 (fat layer).
- 어노테이션: `@Service`, `@Transactional`
- 패키지: `service/`

---

**Data Access 계층**
DB CRUD 처리를 담당하는 계층입니다. Spring Data JDBC의 ListCrudRepository를 상속하면 구현체가 자동 생성됩니다.
- 어노테이션: `@Repository`
- 패키지: `repository/`

---

**Domain 클래스**
DB 테이블과 매핑되는 자바 클래스입니다. Spring Data JDBC에서는 `@Table`로 테이블을 지정하고 `@Id`로 기본키를 선언합니다.
- 패키지: `domain/`

---

**DTO (Data Transfer Object)**
> "배달 상자 — 계층 간 또는 외부로 데이터를 옮길 때 사용하는 전용 객체"

도메인 클래스를 직접 노출하지 않고 필요한 필드만 선택하여 전달하는 객체입니다. API 응답에 도메인 클래스를 직접 반환하면 DB 구조가 외부에 노출되므로 반드시 DTO로 변환해야 합니다.
- 패키지: `dto/`
- Getter 필수 (Jackson이 Getter를 통해 JSON 필드를 생성)

---

**DI (Dependency Injection, 의존성 주입)**
객체가 직접 의존 객체를 생성하지 않고, Spring이 외부에서 주입해 주는 방식입니다. 결합도를 낮추고 테스트·유지보수를 용이하게 합니다.
- 권장 방식: **생성자 주입** (Spring Boot 3.x에서 생성자가 1개이면 `@Autowired` 생략 가능)

```java
// 생성자 주입 예시
public GreetingController(GreetingService greetingService) {
    this.greetingService = greetingService;
}
```

---

## 6. REST API

**REST (REpresentational State Transfer)**
2000년 Roy Thomas Fielding 박사의 UC Irvine 박사학위 논문에서 제안한 웹 아키텍처 스타일입니다. HTTP를 기반으로 자원(Resource)을 URL로 표현하고, HTTP 메서드로 행위를 나타냅니다.

**REST의 6가지 제약 조건:**

| 제약 조건 | 설명 |
|---|---|
| Client-Server | 클라이언트와 서버 역할 분리 |
| Stateless | 요청마다 독립적, 서버에 상태 저장 안 함 |
| Cacheable | 응답을 캐시하여 성능 향상 |
| Uniform Interface | 일관된 인터페이스로 독립 진화 |
| Layered System | 계층화된 시스템 구조 |
| Code on Demand | 필요시 서버에서 코드 다운로드 (선택) |

---

**HTTP 메서드와 CRUD 매핑**

| HTTP Method | CRUD | Spring 어노테이션 | URL 예시 | 설명 |
|---|---|---|---|---|
| GET | 조회 (Read) | `@GetMapping` | `/api/students` | 학생 목록 조회 |
| POST | 생성 (Create) | `@PostMapping` | `/api/students` | 새 학생 등록 |
| PUT | 수정 (Update) | `@PutMapping` | `/api/students/1` | 학생 정보 수정 |
| DELETE | 삭제 (Delete) | `@DeleteMapping` | `/api/students/1` | 학생 삭제 |

---

**`@Controller` vs `@RestController`**

| 구분 | `@Controller` | `@RestController` |
|---|---|---|
| 반환 값 | View 이름 (문자열) | 데이터 (객체/Map 등) |
| 렌더링 | Thymeleaf가 HTML 생성 (SSR) | Jackson이 JSON 자동 변환 |
| 용도 | 웹 페이지 반환 | REST API (JSON 응답) |
| 예시 | `return "hello";` → hello.html | `return List<Student>;` → JSON 배열 |

---

**Jackson**
Spring Boot에 기본 내장된 Java 객체 ↔ JSON 변환 라이브러리입니다. `@RestController`가 객체를 반환하면 Jackson이 자동으로 JSON으로 직렬화합니다. **Getter가 없으면 JSON 필드가 생성되지 않으므로 반드시 Getter를 작성해야 합니다.**

---

## 7. Spring Data JDBC

**Spring Data JDBC**
SQL Mapper 방식의 Spring Data 모듈입니다. JPA의 복잡한 ORM 없이 간결하게 DB 연동할 수 있으며, 개발자가 SQL을 직접 제어할 수 있습니다.

| 어노테이션 | 위치 | 설명 |
|---|---|---|
| `@Table("테이블명")` | 클래스 | DB 테이블과 매핑 |
| `@Id` | 필드 | 기본키(Primary Key) 지정 |

---

**ListCrudRepository**
Spring Data JDBC에서 제공하는 기본 리포지토리 인터페이스입니다. 상속만 해도 `findAll()`, `findById()`, `save()`, `deleteById()` 등의 CRUD 메서드가 자동 생성됩니다.

```java
public interface StudentRepository extends ListCrudRepository<Student, Long> {
    // 선언 없이도 CRUD 메서드 자동 제공
}
```

---

**schema.sql**
Spring Data JDBC는 Hibernate DDL auto가 없으므로 `src/main/resources/schema.sql`에 테이블 생성 SQL을 직접 작성해야 합니다. `spring.sql.init.mode: always` 설정 시 앱 시작 때 자동으로 실행됩니다.

```sql
CREATE TABLE IF NOT EXISTS student (
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    major VARCHAR(100) NOT NULL
);
```

---

**DataInitializer**
`CommandLineRunner`를 구현한 `@Component` 클래스로, 앱 시작 시 자동으로 `run()` 메서드가 실행되어 초기 데이터를 삽입합니다.

---

## 8. 비교 정리

| 구분 | Spring Boot | Django | Express.js |
|---|---|---|---|
| 언어 | Java | Python | JavaScript (Node.js) |
| 아키텍처 | 계층형 + MVC | MVT | 미들웨어 체인 |
| 철학 | Convention over Configuration | Batteries Included | 미니멀 |
| 적합 도메인 | 금융/공공 시스템 | AI/데이터 서비스 | 실시간 서비스 |
| 한국 실무 | **80%+ 공공 SI** | AI 분야 증가 | 실시간 서비스 |

| 구분 | SSR | CSR |
|---|---|---|
| 렌더링 위치 | 서버 | 브라우저 (JS) |
| 첫 로딩 속도 | 빠름 | 느림 |
| 이후 전환 속도 | 느림 (서버 요청) | 빠름 |
| SEO | 유리 | 불리 |
| 대표 기술 | Thymeleaf, JSP | React, Vue, Angular |
| 이 과목 | **사용** | 참고만 |

| 구분 | `@Controller` | `@RestController` |
|---|---|---|
| 반환 | View 이름 → HTML | 객체 → JSON |
| 변환 주체 | Thymeleaf | Jackson |
| 용도 | 웹 페이지 (SSR) | REST API |

| 구분 | JSP Model 1 | Spring MVC Model 2 |
|---|---|---|
| 코드 구조 | HTML+Java+SQL 혼재 | Controller/Service/View 분리 |
| 단위 테스트 | 불가 | 계층별 가능 |
| 유지보수 | 어려움 | 용이 |
| URL 패턴 | `/login.jsp`, `/board.do` | `/students`, `/api/students` |
