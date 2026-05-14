// [복사 위치] src/main/java/kr/ac/tukorea/swframework/exception/EntityNotFoundException.java
// Week 10 — Lab 02: 전역 예외 처리
package kr.ac.tukorea.swframework.exception;

/**
 * 요청한 데이터가 DB에 존재하지 않을 때 발생하는 비즈니스 예외.
 *
 * <p>RuntimeException을 상속해 {@code @Transactional} 롤백 대상이 된다.
 *
 * <p>사용 예 (StudentController / StudentApiController):
 * <pre>
 *   Student student = studentService.findById(id);
 *   if (student == null) {
 *       throw new EntityNotFoundException(id + "번 학생을 찾을 수 없습니다.");
 *   }
 * </pre>
 *
 * <p>GlobalExceptionHandler가 이 예외를 잡아 error/404.html을 렌더링한다.
 *
 * <p>기존 코드 마이그레이션:
 *   IllegalArgumentException("존재하지 않는 학생 ID: " + id)
 *   → EntityNotFoundException(id + "번 학생을 찾을 수 없습니다.")
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
