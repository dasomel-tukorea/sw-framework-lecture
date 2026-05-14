# Lab 01 — Spring MVC 학생 CRUD (swframework 적용)

> "W09에서 만든 학생 CRUD에 W10의 검증·예외 처리·검색·로그인 흐름을 입힌다"

---

## 적용 도메인

**Student** (게시판이 아닌 학생) — swframework 프로젝트의 메인 도메인 그대로 사용.

## 학습 포인트

- **DispatcherServlet** 5단계 흐름 직접 체감
- **3계층 분리**: `StudentController` / `StudentService` / `StudentMapper`
- **Form DTO 분리**: `StudentForm` (검증) ≠ `Student` (Domain) ≠ `StudentResponse` (REST)
- **PRG 패턴**: POST `/students` → 302 Redirect → GET `/students/{id}` (목록 아닌 상세)
- **API + MVC 공존**: `/students/**` (HTML) + `/api/students/**` (JSON)

## 파일 (swframework에 이미 모두 적용됨)

| 파일 | swframework 경로 | 상태 |
|---|---|---|
| `Student.java` | `domain/` | ✓ W09 완료 |
| `StudentForm.java` | `dto/` | ✓ 검증 적용 (Lab 03 기반) |
| `StudentResponse.java` | `dto/` | ✓ REST 응답 DTO |
| `StudentMapper.java` `StudentMapper.xml` | `mapper/`, `resources/mapper/` | ✓ MyBatis (W09) |
| `StudentService.java` | `service/` | ✓ @Service @Transactional |
| `StudentController.java` | `controller/` | ✓ MVC (XSS test 포함) |
| `StudentApiController.java` | `controller/` | ✓ `/api/**` REST |
| `SearchController.java` | `controller/` | ✓ 검색 (Lab 04 기반) |
| `list.html` `addForm.html` `editForm.html` `detail.html` | `templates/student/` | ✓ |

> 본 lab은 **swframework에 이미 모두 있는 코드를 읽고 이해하는 것이 목표**. Lab 02 (예외 처리)와 Lab 03 (검증 응용)에서 새 코드 추가.

---

## 5계층 흐름 — swframework 실제 URL

```
Browser
   │  GET /students?type=name&keyword=홍
   ▼
[LoginInterceptor]  ← W07 — 세션 없으면 /login으로 차단
   │
   ▼
[DispatcherServlet] — 단일 진입점 (Front Controller)
   │  HandlerMapping → StudentController.list() / SearchController.search()
   ▼
[StudentController.list()]   또는   [SearchController.search()]
   │   model.addAttribute("students", studentService.findAll());
   ▼
[StudentService.findAll()]   @Transactional(readOnly = true)
   ▼
[StudentMapper.findAll() / XML <select>]   #{} PreparedStatement
   ▼
[MySQL/H2 student 테이블]
   │
   ▲  ResultSet → List<Student> (map-underscore-to-camel-case)
   ▼  ViewResolver → templates/student/list.html
[Thymeleaf 렌더링] → Browser
```

---

## URL 매핑 표 (swframework 현재 상태)

| Method | URL | Controller 메서드 | 설명 |
|---|---|---|---|
| GET | `/students` | `StudentController.list()` | 학생 목록 |
| GET | `/students/new` | `StudentController.addForm()` | 등록 폼 |
| POST | `/students` | `StudentController.addStudent()` | 등록 → 302 `/students/{id}` |
| GET | `/students/{id}` | `StudentController.detail()` | 상세 |
| GET | `/students/{id}/edit` | `StudentController.editForm()` | 수정 폼 |
| POST | `/students/{id}/edit` | `StudentController.editStudent()` | 수정 → 302 `/students/{id}` |
| POST | `/students/{id}/delete` | `StudentController.deleteStudent()` | 삭제 |
| GET | `/students/search` | `SearchController.search()` | 검색 |
| GET | `/students/by-ids` | `SearchController.findByIds()` | 다건 조회 |
| GET | `/api/students` | `StudentApiController.getStudents()` | JSON 목록 |
| GET | `/api/students/{id}` | `StudentApiController.getStudent()` | JSON 단건 |

---

## DataInitializer로 자동 삽입되는 데이터 3건

```java
new Student("홍길동", "202300001", "hong@tukorea.ac.kr", "IT경영");
new Student("김영희", "202300002", "kim@tukorea.ac.kr", "컴퓨터공학");
new Student("이철수", "202300003", "lee@tukorea.ac.kr", "전자공학");
```

> 앱 시작 시 `DataInitializer`가 자동으로 INSERT (이미 있으면 건너뜀).

---

## 실습 단계 (10분)

1. **swframework 실행** (3분)
   ```bash
   cd swframework
   ./gradlew bootRun --args='--spring.profiles.active=h2'
   ```

2. **로그인** (1분)
   - `http://localhost:8080/login` 접속
   - 로그인 ID: `admin` / 비밀번호: `1234`

3. **5계층 동작 추적** (6분) — IntelliJ 디버거로 한 줄씩 따라가기
   - `/students` 접속 → `StudentController.list()` 진입 → `StudentService.findAll()` → `StudentMapper.findAll()` → SQL 로그 확인 → Thymeleaf 렌더링

---

## 확인 포인트

- [ ] 로그인 없이 `/students` → `/login` 리다이렉트 (LoginInterceptor)
- [ ] 로그인 후 목록 3건 표시 (홍길동·김영희·이철수)
- [ ] `/students/new` → 등록 폼 → POST → 302 `/students/{id}` (상세)
- [ ] 학번 8자리로 등록 시도 → "학번은 9자리 숫자로..." 에러 (Lab 03 검증)
- [ ] `/students/9999` → 현재는 Spring 기본 500 페이지 → Lab 02 적용 후 커스텀 404
- [ ] SQL 로그 출력 (콘솔에 SELECT·INSERT·UPDATE·DELETE)

## 주차 연결

- **W04** `@Service` `@Controller` DI → 본 프로젝트의 생성자 주입
- **W05** `@Transactional` AOP → StudentService
- **W06** Thymeleaf PRG · `@ModelAttribute` → StudentController
- **W07** HttpSession 로그인 + LoginInterceptor → 모든 `/students/**` 보호
- **W09** MyBatis `@Mapper` + XML → StudentMapper

## 흔한 오류 (W10 추가)

| 증상 | 원인 | 해결 |
|---|---|---|
| 빈 흰 화면 (500) | `IllegalArgumentException` Spring 기본 처리 | Lab 02 GlobalExceptionHandler 추가 |
| `/students/9999` 흰 화면 | 동일 | 동일 |
| POST 새로고침 시 중복 등록 | PRG 미적용 | 이미 `redirect:/students/{id}` 적용됨 ✓ |
| 검색어 깨짐 | UTF-8 인코딩 | application.yaml의 `characterEncoding=UTF-8` |
