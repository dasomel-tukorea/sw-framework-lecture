# Week 10 — Spring MVC 패턴 이론 & 실습

> "W09 MyBatis 마이그레이션이 끝난 swframework에 W10의 패턴(3계층·DTO·PRG·@ControllerAdvice·@Valid)을 입힌다"
>
> **베이스**: `/Users/m/Documents/IdeaProjects/40.tukorea/swframework` — 9주차까지 실습 완료된 학생 CRUD 코드

---

## 이번 주 학습 목표

| # | 목표 | 관련 Lab |
|---|------|---------|
| 1 | DispatcherServlet 5단계 흐름 + Front Controller 패턴 | 이론 |
| 2 | 3계층 분리 + DTO 패턴 (Entity ≠ DTO) | 이론 |
| 3 | PRG 패턴 (POST → 302 → GET) 새로고침 중복 방지 | Lab 01 |
| 4 | `@ControllerAdvice` 전역 예외 처리 + 커스텀 404/500 | Lab 02 |
| 5 | `@Valid` + `BindingResult` Bean Validation (`StudentForm`) | Lab 03 |
| 6 | Dynamic SQL 검색 응용 (W09 lab03 → SearchController) | Lab 04 |
| 7 | HttpSession + LoginInterceptor (W07 통합) | Lab 05 |
| 8 | 팀 화면설계서 + API 명세서 작성 (W10 과제) | Lab 06 + 과제 |

---

## 4~10주차 연결 지도

```
Week 04  IoC/DI & Bean
         생성자 주입 / @Service / 자동 구성
                      ↓
Week 05  AOP & 트랜잭션 프록시
         @Transactional 동작 원리 / 횡단 관심사 분리
                      ↓
Week 06  View & Form 처리
         Thymeleaf, PRG, @ModelAttribute
                      ↓
Week 07  세션 & 웹 보안
         HttpSession + LoginInterceptor + XSS/CSRF/SQL Injection
                      ↓
Week 08  팀 프로젝트 분석
         FR/NFR · MoSCoW · 요구사항 정의서
                      ↓
Week 09  Java DB & MyBatis  (Student CRUD 완성)
         JDBC 통증 → SQL Mapper / Profile / Dynamic SQL
                      ↓
Week 10  Spring MVC 패턴 + 전역 예외 처리  ← 이번 주
         3계층 / DTO / PRG / @ControllerAdvice / @Valid
                      ↓
Week 11  페이징 + 파일 업로드
         PageDTO / LIMIT/OFFSET / MultipartFile
```

---

## 산출물 사슬 (팀 프로젝트)

```
W08 요구사항 정의서   →  W09 ERD/테이블 정의서   →  W10 화면설계서·API 명세서   →  W11 WBS
W08_요구사항정의서.docx     W09_ERD_테이블정의서.docx    W10_화면설계서_API명세.docx        W11_WBS.xlsx
```

> 본 주차 산출물(W10)이 W11 페이징 구현의 청사진. 명세 부실하면 W11에서 다시 짠다.

---

## 사전 준비 (swframework — 이미 적용된 항목 ✓)

### 1. build.gradle — 의존성

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'           // ✓ W03~
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'     // ✓ W06~
    implementation 'org.springframework.boot:spring-boot-starter-aop'           // ✓ W05~
    implementation 'org.springframework.boot:spring-boot-starter-validation'    // ✓ W10
    implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3'  // ✓ W09
    implementation 'org.springframework.security:spring-security-crypto'        // ✓ W07
    runtimeOnly    'com.h2database:h2'
    runtimeOnly    'com.mysql:mysql-connector-j'
}
```

### 2. swframework 9주차까지 완성된 패키지 구조

```
src/main/java/kr/ac/tukorea/swframework/
├── controller/
│   ├── StudentController.java       ✓ 학생 CRUD MVC
│   ├── StudentApiController.java    ✓ /api/students JSON
│   ├── SearchController.java        ✓ /students/search
│   ├── LoginController.java         ✓ W07 로그인 + 계정 잠금
│   └── SessionController.java       ✓ W07
├── service/
│   └── StudentService.java          ✓ @Transactional
├── mapper/
│   └── StudentMapper.java           ✓ MyBatis
├── domain/
│   └── Student.java                 ✓ (id·name·studentId·email·major·createdAt)
├── dto/
│   ├── StudentForm.java             ✓ @Valid 검증 적용
│   ├── StudentResponse.java         ✓ REST 응답
│   └── LoginForm.java               ✓ W07
├── repository/
│   └── UserRepository.java          ✓ W07
├── interceptor/
│   └── LoginInterceptor.java        ✓ W07 세션 검사
├── config/
│   ├── WebConfig.java               ✓ 인터셉터 등록
│   ├── SessionListener.java         ✓ W07
│   └── XssEscapeFilterConfig.java   ✓ W07
└── DataInitializer.java             ✓ 홍길동·김영희·이철수 자동 INSERT

src/main/resources/
├── mapper/StudentMapper.xml         ✓ Dynamic SQL 5종 적용
├── templates/student/               ✓ list / detail / addForm / editForm
├── templates/login.html             ✓ W07
├── templates/error/                 ← W10 신규 (Lab 02 추가)
│   ├── 404.html
│   └── 500.html
├── application.yaml                 ✓ profiles.active=h2 (기본)
├── application-h2.yml               ✓
├── application-mysql.yml            ✓
└── schema-mysql.sql                 ✓

src/main/java/kr/ac/tukorea/swframework/
└── exception/                       ← W10 신규 (Lab 02 추가)
    ├── GlobalExceptionHandler.java
    └── EntityNotFoundException.java
```

> **W10에서 추가되는 것은 단 2개 패키지** — `exception/`(Lab 02) + `templates/error/`(Lab 02). 나머지는 모두 9주차까지 완성됨.

### 3. 기본 프로필 + 실행

```bash
cd swframework
./gradlew bootRun                                            # H2 모드 (기본)
./gradlew bootRun --args='--spring.profiles.active=mysql'    # MySQL 모드
```

`http://localhost:8080/login` → admin/1234 → `/students` 접속.

---

## Lab 흐름 (swframework 도메인 = Student)

| Lab | 주제 | swframework 적용 위치 | 예상 시간 |
|---|---|---|---|
| 01 | 학생 CRUD 5계층 (그린필드 학습) | 이미 모두 완성 — **읽고 이해** | 50분 |
| 02 | **전역 예외 처리** (`@ControllerAdvice`) | `exception/` + `templates/error/` 새로 추가 | 25분 |
| 03 | Bean Validation (`@Valid`) | `StudentForm` 이미 적용 — 동작 분석 | 25분 |
| 04 | Dynamic SQL 검색 응용 | `SearchController` + `StudentMapper.xml` 이미 적용 | 15분 |
| 05 | HttpSession + LoginInterceptor | W07 코드 이미 적용 — Lab 02·Lab 03과 결합 | 15분 (선택) |
| 06 | **화면설계서 + API 명세서** (필수 과제) | docs/W10_화면설계서.md + docs/W10_API_명세서.md | 50분 |

> **핵심 변화**: Lab 02만 새 코드 추가. 나머지는 swframework 9주차까지 완성된 코드를 W10 시각으로 다시 보는 학습.

---

## swframework 주요 URL (9주차 완성 + W10 변화)

| Method | URL | 설명 | W10 적용 후 |
|---|---|---|---|
| POST | `/login` | 로그인 (admin/1234) — 5회 실패 시 5분 잠금 | 동일 |
| GET | `/students` | 학생 목록 (3건) | 동일 |
| GET | `/students/{id}` | 학생 상세 | **존재하지 않으면 404 (Lab 02)** |
| GET | `/students/new` | 등록 폼 | 동일 |
| POST | `/students` | 등록 → `redirect:/students/{id}` (PRG) | 검증 실패 → 폼 재렌더링 (Lab 03) |
| GET | `/students/{id}/edit` | 수정 폼 | **없으면 404 (Lab 02)** |
| POST | `/students/{id}/edit` | 수정 → 상세 페이지 | 동일 |
| POST | `/students/{id}/delete` | 삭제 | 동일 |
| GET | `/students/search?type=&keyword=` | 검색 (name·email·student_id·major) | 동일 |
| GET | `/students/by-ids?ids=1,2,3` | 다건 조회 | 동일 |
| GET | `/api/students` | JSON 목록 | 동일 |
| GET | `/api/students/{id}` | JSON 단건 | **없으면 RuntimeException → 500 (Lab 02)** |

---

## DataInitializer로 자동 삽입되는 데이터

```java
new Student("홍길동", "202300001", "hong@tukorea.ac.kr", "IT경영");
new Student("김영희", "202300002", "kim@tukorea.ac.kr",  "컴퓨터공학");
new Student("이철수", "202300003", "lee@tukorea.ac.kr",  "전자공학");
```

---

## 과제

### 기본 과제 — Lab 02 적용 + 동작 확인
- [ ] swframework에 `exception/GlobalExceptionHandler.java` + `EntityNotFoundException.java` 추가
- [ ] `templates/error/404.html`, `500.html` 추가
- [ ] `/students/9999` 접속 → 커스텀 404 페이지 확인
- [ ] 검증 실패(빈 이름·학번 8자리) → 친절한 폼 에러 메시지
- [ ] GitHub Push (README + 설정 안내)
- [ ] 실행 화면 캡처 3장 (목록 + 검증 실패 + 404)

### 심화 과제 — 팀 화면설계서 + API 명세서 (필수)
- [ ] 템플릿 활용: `sw-framework-demo/docs/assignment/W10_화면설계서_API명세_템플릿.docx`
- [ ] **Part 1** `docs/W10_화면설계서.md` — 화면 5~8개 + 흐름도 + 와이어프레임 2~3개
- [ ] **Part 2** `docs/W10_API_명세서.md` — API 10개+ + 상세 명세 3~5개
- [ ] W08 FR ID와 매핑 (추적성)
- [ ] 팀 저장소 Push

### 제출
- **마감**: 다음 주차(W11) 수업 시작 전
- **방법**: e-class + GitHub Push
- **금지**: 타인 코드 복붙 / AI 코드 전체 사용 / 비밀번호 노출

---

## 참고 자료

- [Spring Framework Web MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Jakarta Bean Validation](https://jakarta.ee/specifications/bean-validation/3.0/jakarta-bean-validation-spec-3.0.html)
- [Spring Boot Exception Handling](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.servlet.spring-mvc.error-handling)
- [Thymeleaf 3.1 Tutorial](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
- **swframework** (9주차 완성 실습 프로젝트) — `/Users/m/Documents/IdeaProjects/40.tukorea/swframework`
- **sw-framework-demo** (완성 데모 + W10 docs 템플릿)
- 전자정부 표준프레임워크 실행환경 교육교재 (화면처리)
- 코딩 자율학습 스프링 부트 (홍팍 저, 길벗)
