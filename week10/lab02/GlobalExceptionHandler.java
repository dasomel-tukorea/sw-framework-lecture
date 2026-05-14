// [복사 위치] src/main/java/kr/ac/tukorea/swframework/exception/GlobalExceptionHandler.java
// Week 10 — Lab 02: 모든 Controller의 예외를 한 곳에서 처리
package kr.ac.tukorea.swframework.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 전역 예외 처리 클래스 — Week 10
 *
 * <p>@ControllerAdvice: 모든 @Controller에서 발생한 예외를 이 클래스에서 처리한다.
 * <p>@ExceptionHandler: 처리할 예외 타입을 지정한다.
 * <p>@Slf4j: Lombok 로깅 (log 변수 자동 생성)
 *
 * <p>현업 포인트
 * <ul>
 *   <li>보안: 서버 내부 구조(파일 경로·DB 정보·스택트레이스)를 사용자에게 노출하면 안 됨</li>
 *   <li>UX: 개발자용 에러 메시지를 일반 사용자가 이해하기 어려움</li>
 *   <li>→ 친절한 에러 페이지를 제공하고, 상세 로그는 서버 로그에만 기록</li>
 * </ul>
 *
 * <p>기존 코드의 변경 포인트:
 *   StudentController·StudentApiController의
 *     throw new IllegalArgumentException("존재하지 않는 학생 ID: " + id);
 *   →
 *     throw new EntityNotFoundException(id + "번 학생을 찾을 수 없습니다.");
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리 — 요청한 데이터가 없을 때 (404)
     *
     * <p>처리 흐름:
     * <pre>
     *   Service에서 EntityNotFoundException 발생
     *   → DispatcherServlet이 예외를 전파
     *   → 이 메서드가 처리
     *   → error/404.html 렌더링
     * </pre>
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex, Model model) {
        log.warn("데이터 없음: {}", ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    /**
     * 기존 IllegalArgumentException 처리 (호환성 유지)
     *
     * <p>기존 코드가 throw new IllegalArgumentException(...)로 던지는 경우에도
     * 동일하게 404로 응답한다 — 점진적 마이그레이션 지원.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArg(IllegalArgumentException ex, Model model) {
        log.warn("잘못된 요청: {}", ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    /**
     * 일반 예외 처리 — 예상치 못한 서버 오류 (500)
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, Model model) {
        log.error("서버 오류 발생: {}", ex.getMessage(), ex);
        // 사용자에게는 일반 메시지만 전달 (상세 오류 정보 노출 금지)
        model.addAttribute("message", "서버 내부 오류가 발생했습니다.");
        return "error/500";
    }
}
