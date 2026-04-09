// [복사 위치] src/main/java/kr/ac/tukorea/swframework/domain/Student.java
// [작업] 기존 파일을 이 파일로 교체
package kr.ac.tukorea.swframework.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 학생 도메인 클래스 — Week 06 업데이트
 *
 * Week 03 → Week 06 변경:
 * - major 필드 제거
 * - studentId(학번), email 필드 추가
 * - Setter 추가 (Lab04 수정 폼에서 필요)
 *
 * Spring Data JDBC 컬럼 매핑:
 * - studentId (Java) → student_id (DB) 자동 변환 (camelCase → snake_case)
 */
@Table("student")
public class Student {

    @Id
    private Long id;

    private String name;       // DB: name
    private String studentId;  // DB: student_id (자동 변환)
    private String email;      // DB: email

    protected Student() {}     // Spring Data JDBC 필수

    public Student(String name, String studentId, String email) {
        this.name = name;
        this.studentId = studentId;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStudentId() { return studentId; }
    public String getEmail() { return email; }

    // Setter — Lab04: 수정 폼에서 기존 객체 필드 변경 후 save() → UPDATE
    public void setName(String name) { this.name = name; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', studentId='" + studentId + "', email='" + email + "'}";
    }
}
