// src/main/java/kr/ac/tukorea/swframework/dto/StudentResponse.java
package kr.ac.tukorea.swframework.dto;

// View에 보내는 데이터를 담는 객체 (DTO)
public class StudentResponse {
    private Long id;
    private String name;
    private String major;

    // 생성자
    public StudentResponse(Long id, String name, String major) {
        this.id = id;
        this.name = name;
        this.major = major;
    }

    // Getter (JSON 변환 시 필수 — Jackson이 Getter를 사용하여 JSON 필드 생성)
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getMajor() { return major; }
}
