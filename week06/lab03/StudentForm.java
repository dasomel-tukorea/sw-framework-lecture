// [복사 위치] src/main/java/kr/ac/tukorea/swframework/dto/StudentForm.java
// [작업] lab02 파일을 이 파일로 교체 (Bean Validation 어노테이션 추가)
package kr.ac.tukorea.swframework.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 학생 등록/수정 폼 DTO — Lab 03
 *
 * Lab 02 대비 변경:
 *   Bean Validation 어노테이션 추가
 *   → Controller의 @Valid 와 함께 동작
 *
 * Bean Validation 어노테이션 (jakarta.validation — Spring Boot 3.x):
 *   @NotBlank  — null, "", "   " 모두 거부 (공백만 있는 경우도 실패)
 *   @Size      — 문자열 길이 범위 검증
 *   @Pattern   — 정규표현식 형식 검증
 *   @Email     — 이메일 형식 검증 (빈 값은 통과 — @NotBlank 없으면 선택 입력)
 *
 * 동작 원리:
 *   1. Controller: @Valid @ModelAttribute StudentForm form
 *   2. Spring이 form 객체의 각 필드에 어노테이션 검증 실행
 *   3. 실패한 검증 결과 → BindingResult에 저장
 *   4. BindingResult.hasErrors() → true이면 폼 뷰 재렌더링
 *   5. 뷰에서 th:errors로 에러 메시지 출력
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
