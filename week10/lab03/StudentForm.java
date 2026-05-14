// [복사 위치] src/main/java/kr/ac/tukorea/swframework/dto/StudentForm.java
// [작업] swframework 실제 코드 — 이미 적용된 패턴 (참고용)
// Week 10 — Lab 03 Bean Validation
package kr.ac.tukorea.swframework.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 학생 등록/수정 폼 DTO — Week 10 Lab 03
 *
 * <p>swframework에 이미 적용되어 있는 검증 패턴.
 * 본 파일은 학습용 참고 코드.
 *
 * <p>Lab 02 (예외 처리) 와의 연결
 * <ul>
 *   <li>입력 검증 실패 → BindingResult로 처리 → 폼 재렌더링 (200)</li>
 *   <li>존재하지 않는 학생 조회 → EntityNotFoundException → @ControllerAdvice → 404</li>
 *   <li>예측 불가 오류 → Exception → @ControllerAdvice → 500</li>
 * </ul>
 *
 * <p>Jakarta Bean Validation 어노테이션 (Spring Boot 3.x)
 * <ul>
 *   <li>{@code @NotBlank} — null, 빈문자열, 공백만 모두 거부 (문자열 필수값)</li>
 *   <li>{@code @Size(min=, max=)} — 문자열 길이 범위 검증</li>
 *   <li>{@code @Pattern(regexp=)} — 정규표현식 형식 검증</li>
 *   <li>{@code @Email} — 이메일 형식 검증 (빈 값은 통과 — @NotBlank 없으면 선택 입력)</li>
 * </ul>
 *
 * <p>동작 원리
 * <ol>
 *   <li>Controller: {@code @Valid @ModelAttribute StudentForm form}</li>
 *   <li>Spring이 form 객체의 각 필드에 어노테이션 검증 실행</li>
 *   <li>실패한 검증 결과 → BindingResult에 저장</li>
 *   <li>BindingResult.hasErrors() == true이면 폼 뷰 재렌더링</li>
 *   <li>뷰에서 th:errors로 에러 메시지 출력</li>
 * </ol>
 *
 * 의존성: build.gradle에 spring-boot-starter-validation 필요
 *   implementation 'org.springframework.boot:spring-boot-starter-validation'
 */
public class StudentForm {

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(min = 2, max = 20, message = "이름은 2~20자 사이로 입력해주세요.")
    private String name;

    @NotBlank(message = "학번은 필수 입력 항목입니다.")
    @Pattern(regexp = "\\d{9}", message = "학번은 9자리 숫자로 입력해주세요. (예: 202300001)")
    private String studentId;

    @Email(message = "올바른 이메일 형식으로 입력해주세요.")
    private String email; // 선택 입력 (@NotBlank 없음)

    public StudentForm() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "StudentForm{name='" + name + "', studentId='" + studentId + "', email='" + email + "'}";
    }
}
