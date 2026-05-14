# Lab 03 — Bean Validation (`@Valid` + `BindingResult`)

> "사용자 입력은 모두 적이다 (W07) — 서버 측에서 한 번 더 검증"
>
> **swframework 현황**: 9주차까지 완성된 `StudentForm`에 이미 적용 — 본 lab은 동작 원리 학습 + Lab 02 예외 처리와의 결합

---

## swframework 적용 현황

swframework `dto/StudentForm.java`에 이미 적용된 검증 패턴:

```java
public class StudentForm {

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(min = 2, max = 20, message = "이름은 2~20자 사이로 입력해주세요.")
    private String name;

    @NotBlank(message = "학번은 필수 입력 항목입니다.")
    @Pattern(regexp = "\\d{9}", message = "학번은 9자리 숫자로 입력해주세요. (예: 202300001)")
    private String studentId;

    @Email(message = "올바른 이메일 형식으로 입력해주세요.")
    private String email; // 선택 입력 (@NotBlank 없음)
}
```

`controller/StudentController.java`의 `addStudent()`:

```java
@PostMapping
public String addStudent(
        @Valid @ModelAttribute("studentForm") StudentForm form,
        BindingResult result,
        RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        return "student/addForm";       // Redirect X — Model 유지
    }

    Student student = new Student(form.getName(), form.getStudentId(), form.getEmail());
    studentService.save(student);

    redirectAttributes.addAttribute("id", student.getId());
    redirectAttributes.addFlashAttribute("status", true);
    return "redirect:/students/{id}";   // PRG — 상세 페이지로
}
```

> Lab 03은 이 코드를 **읽고 이해하는 것** + Lab 02 예외 처리와 결합해 사용하는 학습.

---

## 파일 (참고용)

| 파일 | 설명 |
|---|---|
| `StudentForm.java` | swframework 실제 적용된 검증 패턴 |
| `StudentController.java` | Controller 발췌 (addStudent / editStudent) |

---

## 학습 포인트

- **Form 전용 DTO**: 검증 어노테이션은 Form에만 (Domain Student에는 X)
- **`@Valid`** + **`BindingResult`**: 검증 결과를 받아 분기 처리
- **`BindingResult`는 `@Valid` 바로 뒤 파라미터**여야 함 — 순서 어긋나면 400
- 검증 실패 시 **Redirect X** — Model을 살려야 에러 메시지 표시 가능
- HTML5의 `required`는 클라이언트 측 보조일 뿐, 서버 검증이 진짜 보호선

---

## 사전 준비 (이미 적용됨)

`swframework/build.gradle`:

```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'   // ✓ 이미 있음
```

---

## 검증 흐름 다이어그램 (swframework 실제 흐름)

```
사용자 폼 제출
    │  POST /students  (name="", studentId="12", email="bad")
    ▼
[Spring DispatcherServlet]
    │
    ▼  @ModelAttribute로 StudentForm 객체 생성·바인딩
[StudentForm]
    │  name="", studentId="12", email="bad"
    ▼  @Valid 어노테이션 발견 → Bean Validation 실행
[Hibernate Validator]
    │
    ├─ @NotBlank("이름은 필수...")           ❌ 실패
    ├─ @Pattern("학번은 9자리 숫자...")        ❌ 실패
    └─ @Email("올바른 이메일 형식...")         ❌ 실패
    │
    ▼  결과를 BindingResult에 담음
[StudentController.addStudent()]
    │
    ▼  bindingResult.hasErrors() == true
return "student/addForm";   // ← Redirect X! Model 유지
    │
    ▼  Thymeleaf 렌더링
[student/addForm.html]
    │   th:errors="*{name}"      → "이름은 필수 입력 항목입니다."
    │   th:errors="*{studentId}" → "학번은 9자리 숫자..."
    │   th:errors="*{email}"     → "올바른 이메일 형식..."
    │   th:value="${studentForm.name}" → 사용자 입력값 보존
    ▼
사용자에게 폼 + 에러 메시지 표시
```

---

## Lab 02 (전역 예외 처리) 와의 결합

세 가지 케이스가 어떻게 다르게 처리되는지 비교:

| 케이스 | 발생 시점 | 처리 | 응답 |
|---|---|---|---|
| **검증 실패** | `@Valid` Bean Validation | BindingResult → 폼 재렌더링 | HTTP 200 + 폼 HTML |
| **데이터 없음** | `studentService.findById()` → null | `throw EntityNotFoundException` → @ControllerAdvice | error/404.html |
| **예측 불가 오류** | DB connection 실패 등 | `throw Exception` → @ControllerAdvice | error/500.html |

---

## 자주 쓰는 검증 어노테이션

| 어노테이션 | 적용 타입 | 의미 |
|---|---|---|
| `@NotNull` | 모든 타입 | null만 거부 |
| `@NotEmpty` | String/Collection | null + 빈 거부 |
| `@NotBlank` | String | null + 빈 + 공백만 거부 (**가장 엄격**) |
| `@Size(min=, max=)` | String/Collection | 길이/크기 제약 |
| `@Min` / `@Max` | 숫자 | 값 범위 |
| `@Email` | String | 이메일 형식 (빈 값은 통과) |
| `@Pattern(regexp=)` | String | 정규식 |
| `@Past` / `@Future` | LocalDate | 과거/미래 날짜 |

---

## 흔한 실수 4가지

| 실수 | 결과 |
|---|---|
| `BindingResult`가 `@Valid` 바로 뒤가 아닌 곳에 있음 | 검증 실패 시 400 응답 |
| 검증 실패에 `return "redirect:..."` 사용 | Model 날아가서 에러 메시지 표시 X |
| Domain Student에 검증 어노테이션 직접 부착 | 조회 시에도 검증 동작 — Form 분리 필수 |
| `@NotBlank`를 Integer 필드에 사용 | 컴파일은 되지만 동작 안 함 (`@NotNull` 사용) |

---

## 실습 — week10.http로 검증 (Lab03 섹션)

```http
### [Lab03-1] 빈 이름 → @NotBlank 실패 → 폼 재렌더링 (200)
POST /students
name=&studentId=202300999&email=test@test.com

### [Lab03-2] 학번 8자리 → @Pattern 실패
name=홍길동테스트&studentId=20230099&email=test@test.com

### [Lab03-3] 잘못된 이메일 → @Email 실패
name=홍길동테스트&studentId=202300888&email=not-an-email
```

---

## 확인 포인트

- [ ] 빈 이름으로 등록 → 폼 페이지에 "이름은 필수 입력 항목입니다." 표시
- [ ] 학번 8자리 입력 → "학번은 9자리 숫자로 입력해주세요." 표시
- [ ] 잘못된 이메일 형식 → "올바른 이메일 형식으로 입력해주세요." 표시
- [ ] 사용자 입력값이 폼에 그대로 유지됨 (다시 타이핑 안 해도 됨)
- [ ] 정상 입력 시 PRG 패턴 정상 동작 (`redirect:/students/{id}`)
- [ ] (Lab 02 적용 후) `/students/9999` 접근 → 커스텀 404 페이지

---

## 주차 연결

- **W07** XSS 방어와 함께 **서버 측 검증**은 보안 기본 → `th:text` 자동 이스케이프 + Bean Validation
- **W09** `#{}` PreparedStatement — DB 단에서 막기 / **W10** Bean Validation — 입력 단에서 막기
- **Lab 02 + Lab 03** — 검증/조회/오류 3가지 케이스 처리 패턴 완성
