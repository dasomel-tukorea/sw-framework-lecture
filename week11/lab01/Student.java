// [복사 위치] src/main/java/kr/ac/tukorea/swframework/domain/Student.java
// [작업] W09 lab02에서 도입된 Student 도메인을 W11에서도 그대로 사용한다.
//        - Lombok 미사용 (수강생이 학습 단계에서 보일러플레이트를 직접 확인하도록)
//        - MyBatis 리플렉션 매핑을 위해 기본 생성자 public 필수
//        - student_id / created_at 컬럼은 map-underscore-to-camel-case 로 자동 매핑
package kr.ac.tukorea.swframework.domain;

import java.time.LocalDateTime;

/**
 * 학생 도메인 클래스 — Week 11 (페이징 / 검색 / 정렬 / 첨부)
 *
 * MyBatis 매핑 규칙:
 *   - 기본 생성자(no-args) 필수: 리플렉션으로 객체 생성 후 setter 호출
 *   - mybatis.configuration.map-underscore-to-camel-case=true 설정으로
 *     DB 컬럼 student_id → Java 필드 studentId 자동 매핑
 *
 * 컬럼 매핑:
 *   id              BIGINT (PK)
 *   name            VARCHAR
 *   student_id      VARCHAR  ↔ studentId
 *   email           VARCHAR
 *   major           VARCHAR
 *   created_at      TIMESTAMP ↔ createdAt (DB DEFAULT CURRENT_TIMESTAMP)
 *
 * Lab 04(파일 첨부)에서 attachment_name / saved_name 컬럼이 추가되지만,
 * lab01~03 범위에서는 본 클래스의 기본 6개 필드만 사용한다.
 */
public class Student {

    private Long id;
    private String name;
    private String studentId;       // DB: student_id
    private String email;
    private String major;
    private LocalDateTime createdAt; // DB: created_at

    // MyBatis 리플렉션용 기본 생성자 (public 필수)
    public Student() {}

    public Student(String name, String studentId, String email) {
        this.name = name;
        this.studentId = studentId;
        this.email = email;
    }

    public Student(String name, String studentId, String email, String major) {
        this(name, studentId, email);
        this.major = major;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', studentId='" + studentId
                + "', email='" + email + "', major='" + major + "'}";
    }
}
