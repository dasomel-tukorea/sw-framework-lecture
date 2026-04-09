// [복사 위치] src/main/java/kr/ac/tukorea/swframework/dto/StudentForm.java
// [작업] 파일이 없으면 생성
// Lab 02: 검증 어노테이션 없는 기본 폼 DTO
//         (Lab 03에서 @NotBlank, @Pattern, @Email 추가 예정)
package kr.ac.tukorea.swframework.dto;

/**
 * 학생 등록/수정 폼 DTO — Lab 02
 *
 * 왜 Student 도메인을 직접 쓰지 않는가?
 * - Student는 DB 구조 반영 (@Table, @Id 등 인프라 관심사)
 * - 폼 입력 검증은 화면 관심사 → 역할 분리
 *
 * Lab 02 단계: 검증 없이 기본 바인딩만 확인
 * - @ModelAttribute가 기본 생성자 + Setter로 객체에 값을 채워줌
 *
 * Lab 03에서 추가할 것:
 *   @NotBlank, @Size, @Pattern, @Email 어노테이션
 */
public class StudentForm {

    private String name;
    private String studentId;
    private String email;

    /**
     * 기본 생성자 필수!
     * Spring @ModelAttribute 바인딩: 기본 생성자로 객체 생성 → Setter로 값 주입
     */
    public StudentForm() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
