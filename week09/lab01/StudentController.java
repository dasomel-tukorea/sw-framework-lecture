// Week 09 — MyBatis CRUD
// StudentController.java — 학생 관리 요청 처리 컨트롤러
// 프로젝트 경로: src/main/java/kr/ac/tukorea/swframework/controller/StudentController.java
package kr.ac.tukorea.swframework.controller;

import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 학생 관리 요청 처리 컨트롤러
 *
 * - @Controller: Spring MVC 컨트롤러로 등록
 * - @RequestMapping("/students"): 모든 URL에 /students 접두사 적용
 *
 * 주요 학습 포인트:
 * 1. @GetMapping / @PostMapping — HTTP 메서드별 매핑
 * 2. @ModelAttribute — Form 데이터를 Java 객체에 자동 바인딩
 * 3. @PathVariable — URL 경로 변수 추출
 * 4. PRG 패턴 — Post-Redirect-Get 중복 등록 방지
 */
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    // 생성자 주입 (DI) — @Autowired 생략 가능 (생성자가 1개일 때)
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * 학생 목록 조회
     * GET /students
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "student/list";  // templates/student/list.html 렌더링
    }

    /**
     * 학생 등록 폼 페이지
     * GET /students/new
     */
    @GetMapping("/new")
    public String createForm() {
        return "student/form";  // templates/student/form.html 렌더링
    }

    /**
     * 학생 등록 처리 — PRG 패턴 적용 (Post-Redirect-Get)
     * POST /students
     *
     * @param student Form 데이터가 자동 바인딩된 객체
     * @return redirect URL (PRG 패턴 — 새로고침해도 POST가 재전송되지 않음)
     */
    @PostMapping
    public String create(@ModelAttribute Student student) {
        studentService.save(student);
        return "redirect:/students";  // 등록 후 목록으로 이동
    }

    /**
     * 학생 수정 폼 페이지
     * GET /students/{id}/edit
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.findById(id));
        return "student/form";
    }

    /**
     * 학생 수정 처리
     * POST /students/{id}
     */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Student student) {
        student.setId(id);
        studentService.update(student);
        return "redirect:/students";
    }

    /**
     * 학생 삭제 처리
     * POST /students/{id}/delete
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        studentService.delete(id);
        return "redirect:/students";
    }
}
