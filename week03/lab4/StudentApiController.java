// src/main/java/kr/ac/tukorea/swframework/controller/StudentApiController.java
package kr.ac.tukorea.swframework.controller;

import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.dto.StudentResponse;
import kr.ac.tukorea.swframework.repository.StudentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController         // ← JSON 데이터를 반환하는 컨트롤러
@RequestMapping("/api") // ← 공통 경로 접두어
public class StudentApiController {

    private final StudentRepository studentRepository; // ← Repository 주입

    public StudentApiController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // GET /api/hello → JSON 응답
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of(
            "message", "안녕하세요!",
            "course", "SW프레임워크",
            "semester", "2026-1"
        );
    }

    // GET /api/students → DB에서 전체 학생 목록을 DTO로 반환
    @GetMapping("/students")
    public List<StudentResponse> getStudents() {
        return studentRepository.findAll().stream()
                .map(s -> new StudentResponse(s.getId(), s.getName(), s.getMajor()))
                .collect(Collectors.toList());
    }

    // GET /api/students/1 → DB에서 특정 학생 조회 후 DTO로 반환
    @GetMapping("/students/{id}")
    public StudentResponse getStudent(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다: " + id));
        return new StudentResponse(student.getId(), student.getName(), student.getMajor());
    }
}
