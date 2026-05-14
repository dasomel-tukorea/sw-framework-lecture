# Lab 02 — 전역 예외 처리 (`@ControllerAdvice`)

> "예외 처리도 설계의 일부 — 정상 흐름만 짜는 게 아니라 실패 경로도 함께 설계"
>
> **swframework 적용 대상 도메인: Student** (게시판이 아닌 학생 CRUD)

---

## 학습 포인트

- **`@ControllerAdvice`** + **`@ExceptionHandler`** = 모든 Controller의 예외를 한 곳에서
- **커스텀 비즈니스 예외**: `EntityNotFoundException` (RuntimeException 상속)
- **404 / 500 커스텀 에러 페이지**: 보안(스택트레이스 숨김) + UX(친절한 메시지)
- 기존 `IllegalArgumentException`도 함께 처리 (점진적 마이그레이션)
- W05 AOP의 한 형태로 봐도 됨 — 횡단 관심사(예외 처리) 분리

---

## 파일

| 파일 | 적용 위치 (swframework) |
|---|---|
| `EntityNotFoundException.java` | `src/main/java/.../exception/EntityNotFoundException.java` |
| `GlobalExceptionHandler.java` | `src/main/java/.../exception/GlobalExceptionHandler.java` |
| `404.html` | `src/main/resources/templates/error/404.html` |
| `500.html` | `src/main/resources/templates/error/500.html` |

> **사전 작업**: `swframework/src/main/java/kr/ac/tukorea/swframework/exception/` 패키지 생성

---

## 기존 코드와의 차이 (마이그레이션 포인트)

### Before — StudentController (현재 swframework)

```java
@GetMapping("/{id}")
public String detail(@PathVariable Long id, Model model) {
    Student student = studentService.findById(id);
    if (student == null) throw new IllegalArgumentException("존재하지 않는 학생 ID: " + id);
    //                              ↑↑↑ Spring 기본 500 페이지로 가버림
    model.addAttribute("student", student);
    return "student/detail";
}
```

### After — Lab 02 적용 후

```java
@GetMapping("/{id}")
public String detail(@PathVariable Long id, Model model) {
    Student student = studentService.findById(id);
    if (student == null) {
        throw new EntityNotFoundException(id + "번 학생을 찾을 수 없습니다.");
        //         ↑↑↑ GlobalExceptionHandler가 잡아 → error/404.html
    }
    model.addAttribute("student", student);
    return "student/detail";
}
```

> 본 lab의 `GlobalExceptionHandler`는 `IllegalArgumentException`도 함께 처리하므로 **기존 코드는 그대로 둬도 404로 라우팅**된다. 점진적으로 새 코드는 `EntityNotFoundException`을 사용하면 됨.

---

## 통증 → 해결

### Before — Controller마다 try-catch 4중첩

```java
@GetMapping("/{id}")
public String detail(@PathVariable Long id, Model model) {
    try {
        Student s = studentService.findById(id);
        if (s == null) return "error/404";   // 매번 직접 분기
        model.addAttribute("student", s);
        return "student/detail";
    } catch (DataAccessException e) {
        return "error/500";                   // 매번 catch
    } catch (Exception e) {
        return "error/500";                   // 또 catch
    }
}
// → 모든 Controller 메서드에 동일 코드 복붙 → 실수 누적
```

### After — 비즈니스 로직만 남고 예외는 위임

```java
@GetMapping("/{id}")
public String detail(@PathVariable Long id, Model model) {
    Student s = studentService.findById(id);
    if (s == null) throw new EntityNotFoundException(id + "번 학생 없음");
    model.addAttribute("student", s);
    return "student/detail";
}
// → 예외 발생 시 @ControllerAdvice가 자동 처리
```

---

## 예외 흐름 다이어그램

```
[StudentController.detail()]
   throw new EntityNotFoundException(id + "번 학생 없음")
       │
       ▼
[Controller] — 잡지 않음
       │
       ▼
[DispatcherServlet] — 예외 감지 → ExceptionHandler 검색
       │
       ▼
[@ControllerAdvice / GlobalExceptionHandler]
   @ExceptionHandler(EntityNotFoundException.class)
   handleNotFound() 호출
       │
       ▼
[error/404.html] 렌더링 → 사용자에게 친절한 메시지
       │  + 로그: log.warn("데이터 없음: {}")
       ▼
HTTP 200 (View 렌더링) — 사용자 입장에서는 정상 응답처럼 보임
```

---

## 실습 단계 (15분)

1. **패키지 생성** (1분)
   ```bash
   mkdir -p swframework/src/main/java/kr/ac/tukorea/swframework/exception
   mkdir -p swframework/src/main/resources/templates/error
   ```

2. **lab02 파일 4개 복사** (3분)
   - `EntityNotFoundException.java` → `exception/`
   - `GlobalExceptionHandler.java` → `exception/`
   - `404.html` → `templates/error/`
   - `500.html` → `templates/error/`

3. **(선택) StudentController에서 마이그레이션** (5분)
   `IllegalArgumentException` → `EntityNotFoundException`으로 점진 교체.
   기존 코드 그대로 두어도 동일하게 404로 라우팅됨 (호환성 핸들러).

4. **빌드 + 실행** (2분)
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=h2'
   ```

5. **확인** (4분)
   - 로그인 후 `/students/9999` 접속 → 커스텀 404 페이지
   - 서버 로그에 `WARN 데이터 없음: 9999번...` 출력

---

## 확인 포인트

- [ ] `/students/9999` → 커스텀 404 페이지 (Spring 기본 흰 화면 X)
- [ ] `/api/students/9999` → JSON으로 처리되지만 `@ResponseBody` 적용 시 422/500 응답 (REST는 별도 처리 권장)
- [ ] 사용자에게는 **상세 오류 노출 X** (스택트레이스·DB 정보)
- [ ] 서버 로그에는 **상세 정보 기록** (`log.error(... , ex)`)
- [ ] 기존 `throw new IllegalArgumentException`도 404로 라우팅됨

---

## 보안 한 줄

> "에러 페이지에서 스택트레이스가 보이면, 공격자가 사용자보다 먼저 본다."

```yaml
# application.yaml (운영)
server:
  error:
    include-stacktrace: never
    include-message: never
```

---

## 주차 연결

- **W05** AOP 프록시 → `@ControllerAdvice`도 동일한 횡단 관심사 분리
- **W07** XSS 방어 — `th:text`는 자동 이스케이프 → 에러 메시지의 사용자 입력도 안전
- **W09** `@Transactional` 롤백 → `EntityNotFoundException`은 RuntimeException이라 자동 롤백 대상

## 다음 단계 (Lab 03 연계)

- **검증 실패도 비슷한 사상**: `BindingResult`로 받아 폼 재렌더링
- (REST API에서는) `MethodArgumentNotValidException`도 `@ExceptionHandler`로 처리
