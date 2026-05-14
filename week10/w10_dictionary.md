# 10주차 핵심 용어집

처음 접하는 개념은 **비유**로 감을 잡고, **정확한 정의**로 마무리.

---

## 1. DispatcherServlet

> "호텔 프론트 데스크 — 누가 들어오든 일단 여기서 등록·체크인·청구"

Spring MVC의 **Front Controller**. 모든 HTTP 요청의 **단일 진입점**. 요청을 받아 적절한 Controller로 분배하고, 공통 관심사(예외·로깅·인증)를 한 곳에서 처리.

```
브라우저 → DispatcherServlet → HandlerMapping → Controller
        ↑                                          ↓
        ←─── View 렌더링 ←─── ViewResolver ←─── Model
```

---

## 2. 3계층 아키텍처

> "음식점 — 홀(Controller) → 주방(Service) → 창고(Mapper)"

| 계층 | 역할 | 비유 |
|---|---|---|
| **Controller** | HTTP 요청·응답 처리 | 홀 직원 (주문 받기) |
| **Service** | 비즈니스 로직 + 트랜잭션 | 주방장 (조리·플레이팅) |
| **Mapper** (Repository) | DB 접근 | 창고 관리인 (재료 꺼내기) |

> 각 계층은 자신의 책임만 — 홀이 요리하지 않고, 창고 직원이 손님과 대화하지 않음.

---

## 3. DTO (Data Transfer Object)

> "택배 상자 — 받는 사람에게 필요한 것만 담아 보냄"

계층 간 데이터 전달 전용 객체. **Entity와 분리**하는 이유:

| 문제 | Entity 직접 노출 | DTO 사용 |
|---|---|---|
| 필드 노출 | 패스워드까지 응답에 포함 | 필요한 필드만 |
| 계층 결합 | DB 스키마 바뀌면 화면도 깨짐 | 독립적 변경 가능 |
| 순환 참조 | Entity 간 관계 따라가다 무한 루프 | 평면 구조 |
| 보안 | 사용자가 임의 필드 채울 위험 | Form DTO로 차단 |

---

## 4. 정적 팩토리 메서드

> "가짜 생성자 — 의도가 드러나는 이름으로 객체를 만든다"

```java
BoardDTO dto = new BoardDTO();      // 의도 모호
dto.setId(id);
dto.setTitle(title);

BoardDTO dto = BoardDTO.of(id, title, content, author);   // 명확
BoardDTO dto = BoardDTO.forUpdate(id, form);              // 수정 전용
```

---

## 5. PRG 패턴 (Post-Redirect-Get)

> "은행 ATM — 출금 누른 뒤 새로고침해도 또 출금되지 않게"

POST 처리 후 **302 Redirect**로 GET을 유도. 새로고침 시 POST 재전송을 방지.

```
POST /board/create  ──→ 302 Redirect: /board/list ──→ GET /board/list
                                                   ↑
                                          새로고침해도 GET만 재실행
```

---

## 6. `@ControllerAdvice`

> "AS 센터 — 모든 매장에서 발생한 클레임을 한 곳에서 처리"

모든 `@Controller`에서 발생한 예외를 한 클래스에서 처리. **횡단 관심사 분리** (W05 AOP의 한 형태).

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(Exception ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }
}
```

---

## 7. `@ExceptionHandler`

특정 예외 타입에 대한 처리 메서드 지정. `@ControllerAdvice` 안에 정의하면 전역 적용.

| 처리 순서 |
|---|
| 1. Controller 메서드 자체의 try-catch (있으면) |
| 2. 같은 Controller 안의 `@ExceptionHandler` |
| 3. `@ControllerAdvice`의 `@ExceptionHandler` |
| 4. Spring 기본 ErrorHandler |

---

## 8. 비즈니스 예외 (Custom Exception)

`RuntimeException`을 상속한 도메인 의미를 담은 예외.

```java
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String msg) { super(msg); }
}
// 사용
throw new EntityNotFoundException(id + "번 게시글 없음");
```

> `RuntimeException` 상속이 중요 — `@Transactional` 롤백 대상.

---

## 9. Bean Validation (`@Valid`)

> "공항 보안검색 — 비행기 타기 전에 한 번 더 검사"

사용자 입력을 서버 측에서 검증. **HTML5 `required`는 클라이언트 보조**일 뿐.

```java
@PostMapping("/create")
public String create(@Valid @ModelAttribute BoardForm form,
                     BindingResult bindingResult) {  // ← @Valid 바로 뒤!
    if (bindingResult.hasErrors()) return "board/form";
    ...
}
```

자주 쓰는 어노테이션: `@NotBlank` `@Size` `@NotNull` `@Email` `@Pattern` `@Min/@Max`

---

## 10. `BindingResult`

검증 결과를 담는 객체. **`@Valid` 바로 뒤 파라미터**에 위치해야 함.

```java
// ✓ 올바른 순서
public String create(@Valid @ModelAttribute Form form,
                     BindingResult result) { ... }

// ✗ 잘못된 순서 — 검증 실패 시 MethodArgumentNotValidException 던짐
public String create(@Valid @ModelAttribute Form form,
                     Model model,
                     BindingResult result) { ... }
```

---

## 11. SSR (Server-Side Rendering)

> "신문 인쇄 — 서버가 완성된 HTML을 미리 찍어서 보냄"

Thymeleaf가 서버에서 HTML을 완성하여 브라우저에 전달. **장점**: SEO, 초기 로딩 빠름. **단점**: 페이지 전환마다 서버 왕복.

대비: **SPA (Single Page Application)** — Vue/React가 클라이언트에서 렌더링.

---

## 12. HttpSession

> "회원 카드 — 매장이 손님 ID를 기억"

서버 메모리에 사용자 상태 저장. 쿠키(JSESSIONID)로 식별.

```java
session.setAttribute("loginUser", user);   // 저장
String user = (String) session.getAttribute("loginUser");  // 조회
session.invalidate();                        // 로그아웃
```

> W07 핵심 — W10에서는 게시글 작성자 검증에 활용.

---

## 13. HandlerInterceptor

> "공항 게이트 — 비행기 타기 전 신분증 검사"

Controller 진입 전후에 동작하는 횡단 관심사 (인증·로깅·CORS).

```
preHandle()   → Controller 진입 전 (인증 검사)
postHandle()  → Controller 종료 후, View 렌더링 전
afterCompletion() → View 렌더링 후 (정리 작업)
```

---

## 14. `@Valid` vs `@Validated`

| 항목 | `@Valid` | `@Validated` |
|---|---|---|
| 표준 | Jakarta Bean Validation 표준 | Spring 전용 |
| 그룹 검증 | 미지원 | 지원 (`@Validated(Create.class)`) |
| 메서드 파라미터 | ✓ | ✓ (`@Service` 클래스에 사용 가능) |
| 본 강의 권장 | ✓ Controller에서 사용 | 복잡한 시나리오에만 |

---

## 15. Front Controller 패턴

> "회사 대표 전화 — 모든 외부 연락이 한 번호로"

여러 진입점이 아닌 **하나의 진입점이 모든 요청을 받아 분배**하는 디자인 패턴. Spring의 DispatcherServlet이 이 패턴의 대표 사례.

장점: 공통 처리(인증·로깅·예외) 일원화 / 단점: 진입점이 단일 실패 지점이 됨 (Spring은 안정성으로 해결).
