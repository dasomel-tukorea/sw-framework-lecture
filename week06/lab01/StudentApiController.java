// [복사 위치] src/main/java/kr/ac/tukorea/swframework/controller/StudentApiController.java
// [작업] 기존 파일을 이 파일로 교체 (getMajor → getStudentId/getEmail 반영)
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

@RestController
@RequestMapping("/api")
public class StudentApiController {

    private final StudentRepository studentRepository;

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

    // GET /api/students → 전체 학생 목록 (DTO 변환)
    // StudentResponse: major → studentId, email 필드로 변경됨 (lab01 참고)
    @GetMapping("/students")
    public List<StudentResponse> getStudents() {
        return studentRepository.findAll().stream()
                .map(s -> new StudentResponse(s.getId(), s.getName(), s.getStudentId(), s.getEmail()))
                .collect(Collectors.toList());
    }

    // GET /api/students/1 → 특정 학생 조회
    @GetMapping("/students/{id}")
    public StudentResponse getStudent(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다: " + id));
        return new StudentResponse(student.getId(), student.getName(), student.getStudentId(), student.getEmail());
    }
}
