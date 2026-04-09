// [복사 위치] src/main/java/kr/ac/tukorea/swframework/controller/StudentController.java
// [작업] lab01 파일을 이 파일로 교체 (addForm, addStudent 추가)
package kr.ac.tukorea.swframework.controller;

import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.dto.StudentForm;
import kr.ac.tukorea.swframework.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 학생 MVC 컨트롤러 — Lab 02
 *
 * Lab 01 대비 추가:
 *   GET  /students/new  → 등록 폼
 *   POST /students      → 등록 처리 + PRG 패턴
 *
 * PRG 패턴(Post-Redirect-Get):
 *   POST 처리 완료 → redirect:/students/{id} → 브라우저 GET 재요청
 *   → F5(새로고침)해도 GET만 재실행 → 중복 등록 없음
 */
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // ── Lab 01: 목록 ──────────────────────────────────────────────

    @GetMapping
    public String list(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        return "student/list";
    }

    // ── Lab 02: 등록 폼 + PRG ─────────────────────────────────────

    /**
     * GET /students/new — 등록 폼
     *
     * 빈 StudentForm을 Model에 담는 이유:
     * - th:object="${studentForm}" → null이면 Thymeleaf 렌더링 오류
     * - 검증 실패 후 폼으로 돌아올 때도 동일한 구조 사용 (Lab 03)
     */
    @GetMapping("/new")
    public String addForm(Model model) {
        model.addAttribute("studentForm", new StudentForm());
        return "student/addForm";
    }

    /**
     * POST /students — 학생 등록 처리
     *
     * @ModelAttribute: 요청 파라미터를 StudentForm 객체에 자동 바인딩
     *   (기본 생성자 + Setter 필요)
     *
     * RedirectAttributes.addAttribute("id", saved.getId())
     *   → redirect URL의 {id} 플레이스홀더에 자동 치환
     *
     * addFlashAttribute("status", true)
     *   → redirect 후 딱 1회만 사용되고 소멸 (성공 메시지용)
     *
     * Lab 03에서 @Valid + BindingResult 추가 예정
     */
    @PostMapping
    public String addStudent(
            @ModelAttribute("studentForm") StudentForm form,
            RedirectAttributes redirectAttributes) {

        Student student = new Student(form.getName(), form.getStudentId(), form.getEmail());
        Student saved = studentRepository.save(student);

        redirectAttributes.addAttribute("id", saved.getId());
        redirectAttributes.addFlashAttribute("status", true);
        return "redirect:/students/{id}"; // PRG
    }

    // ── Lab 04에서 추가: detail, editForm, editStudent, deleteStudent ──
    // ── Lab 05에서 추가: xssTestForm, xssTest ─────────────────────────
}
