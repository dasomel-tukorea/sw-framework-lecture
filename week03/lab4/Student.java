// src/main/java/kr/ac/tukorea/swframework/domain/Student.java
package kr.ac.tukorea.swframework.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("student") // ← DB 테이블 이름 지정 (Spring Data JDBC)
public class Student {

    @Id // ← 기본키(Primary Key)
    private Long id;

    private String name;

    private String major;

    // Spring Data JDBC 필수: 기본 생성자 (DB 조회 시 객체 생성에 사용)
    protected Student() {}

    // 새 학생 생성 시 사용 (id는 DB가 자동 부여)
    public Student(String name, String major) {
        this.name = name;
        this.major = major;
    }

    // Getter
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getMajor() { return major; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', major='" + major + "'}";
    }
}
