// Week 09 — MyBatis CRUD
// Student.java — 학생 도메인 클래스
// 프로젝트 경로: src/main/java/kr/ac/tukorea/swframework/domain/Student.java
package kr.ac.tukorea.swframework.domain;

import java.time.LocalDateTime;

/**
 * 학생 도메인 클래스
 *
 * DB의 student 테이블과 매핑된다.
 * MyBatis가 ResultSet을 Student 객체로 자동 변환한다.
 *
 * - 기본 생성자: MyBatis가 리플렉션으로 객체 생성 시 필수
 * - Getter/Setter: MyBatis 결과 매핑 및 Thymeleaf 바인딩에 필요
 */
public class Student {

    private Long id;                   // 학생 번호 (PK, AUTO_INCREMENT)
    private String name;               // 학생 이름
    private String email;              // 이메일
    private String major;              // 전공
    private LocalDateTime createdAt;   // 등록일시 (DB에서 자동 생성)

    // 기본 생성자 (MyBatis가 리플렉션으로 객체 생성 시 필요)
    public Student() {}

    // --- Getter / Setter ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
