// [복사 위치] src/main/java/kr/ac/tukorea/swframework/dto/StudentForm.java
// [작업] W11 페이징 통합 화면에서도 동일하게 사용 (W10 lab03 = 기존 자산 재사용)
//        - Lombok 미사용 (수강생이 보일러플레이트와 검증 어노테이션의 관계를 직접 확인)
//        - 도메인 Student 와 분리 — Controller 의 @Valid 대상은 항상 Form DTO
package kr.ac.tukorea.swframework.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 학생 등록/수정 폼 DTO — Week 11
 *
 * Bean Validation 어노테이션 (jakarta.validation):
 *   @NotBlank — null / "" / "   " 모두 거부
 *   @Size     — 문자열 길이 범위
 *   @Pattern  — 정규표현식 형식
 *   @Email    — 이메일 형식 (빈 값은 통과 — @NotBlank 없으면 선택 입력)
 *
 * 동작 흐름:
 *   1. Controller: @Valid @ModelAttribute StudentForm form
 *   2. Spring 이 어노테이션 검증 실행
 *   3. 실패 결과 → BindingResult 에 저장
 *   4. BindingResult.hasErrors() == true → 폼 뷰 재렌더링
 *   5. 뷰에서 th:errors 로 메시지 출력
 *
 * 도메인(Student)과 분리하는 이유:
 *   - 도메인은 DB 매핑 / 비즈니스 불변식을 담당
 *   - 폼 DTO 는 입력 검증과 화면 바인딩만 담당 → 결합도 낮추고 보안 위험 차단
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

    private String major; // 선택 입력 (W09 추가 컬럼)

    public StudentForm() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    @Override
    public String toString() {
        return "StudentForm{name='" + name + "', studentId='" + studentId
                + "', email='" + email + "', major='" + major + "'}";
    }
}
