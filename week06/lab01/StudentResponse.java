// [복사 위치] src/main/java/kr/ac/tukorea/swframework/dto/StudentResponse.java
// [작업] 기존 파일을 이 파일로 교체 (major → studentId, email 추가)
package kr.ac.tukorea.swframework.dto;

/**
 * REST API 응답용 DTO — Week 06 업데이트
 * major → studentId, email 필드 추가
 */
public class StudentResponse {
    private Long id;
    private String name;
    private String studentId;
    private String email;

    public StudentResponse(Long id, String name, String studentId, String email) {
        this.id = id;
        this.name = name;
        this.studentId = studentId;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStudentId() { return studentId; }
    public String getEmail() { return email; }
}
