// [복사 위치] src/main/java/kr/ac/tukorea/swframework/controller/StudentController.java
// [작업] 기존 파일이 없으면 생성, 있으면 교체
// Lab 01: 목록 조회만 구현 (lab02~06에서 기능 추가)
package kr.ac.tukorea.swframework.controller;

import kr.ac.tukorea.swframework.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 학생 MVC 컨트롤러 — Lab 01
 *
 * @RestController(JSON) vs @Controller(HTML):
 *   - @Controller: 메서드가 반환하는 문자열을 뷰 이름으로 해석
 *   - return "student/list" → templates/student/list.html 렌더링
 *
 * Model: Controller → View 데이터 전달 컨테이너
 *   - model.addAttribute("students", list) → 템플릿에서 ${students}로 접근
 */
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // ── Lab 01: 학생 목록 ──────────────────────────────────────────

    /**
     * GET /students — 학생 목록 페이지
     *
     * 1. DB에서 전체 학생 조회
     * 2. Model에 담아 View로 전달
     * 3. templates/student/list.html 렌더링
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        return "student/list";
    }
}
