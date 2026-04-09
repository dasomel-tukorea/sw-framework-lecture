// [복사 위치] src/main/java/kr/ac/tukorea/swframework/controller/StudentController.java
// [작업] lab03 파일을 이 파일로 교체 (상세/수정/삭제 추가)
package kr.ac.tukorea.swframework.controller;

import jakarta.validation.Valid;
import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.dto.StudentForm;
import kr.ac.tukorea.swframework.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 학생 MVC 컨트롤러 — Lab 04
 *
 * Lab 03 대비 추가:
 *   GET  /students/{id}        → 상세 페이지
 *   GET  /students/{id}/edit   → 수정 폼
 *   POST /students/{id}/edit   → 수정 처리 (PRG)
 *   POST /students/{id}/delete → 삭제 처리 (PRG)
 *
 * URL 우선순위 주의:
 *   /students/new 는 리터럴 경로 → /{id} 경로변수보다 우선 매핑됨
 *   Spring MVC: 구체적인 경로 > 경로변수 순으로 우선순위 처리
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

    // ── Lab 02~03: 등록 폼 + PRG ──────────────────────────────────

    @GetMapping("/new")
    public String addForm(Model model) {
        model.addAttribute("studentForm", new StudentForm());
        return "student/addForm";
    }

    @PostMapping
    public String addStudent(
            @Valid @ModelAttribute("studentForm") StudentForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "student/addForm";
        }

        Student student = new Student(form.getName(), form.getStudentId(), form.getEmail());
        Student saved = studentRepository.save(student);

        redirectAttributes.addAttribute("id", saved.getId());
        redirectAttributes.addFlashAttribute("status", true);
        return "redirect:/students/{id}";
    }

    // ── Lab 04: 상세 / 수정 / 삭제 ───────────────────────────────

    /**
     * GET /students/{id} — 학생 상세 페이지
     *
     * @PathVariable: URL 경로의 {id} 값을 메서드 파라미터로 바인딩
     * orElseThrow(): Optional이 비어있으면 예외 발생
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학생 ID: " + id));
        model.addAttribute("student", student);
        return "student/detail";
    }

    /**
     * GET /students/{id}/edit — 수정 폼
     *
     * 기존 Student 데이터를 StudentForm에 복사해서 폼에 pre-fill
     * 이유: 도메인 Student를 th:object로 직접 쓰면 검증 어노테이션이 섞임
     *      → DTO와 도메인을 분리해 각자의 역할에 집중
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학생 ID: " + id));

        StudentForm form = new StudentForm();
        form.setName(student.getName());
        form.setStudentId(student.getStudentId());
        form.setEmail(student.getEmail());

        model.addAttribute("studentForm", form);
        model.addAttribute("studentId", id); // th:action URL에서 사용
        return "student/editForm";
    }

    /**
     * POST /students/{id}/edit — 수정 처리
     *
     * Spring Data JDBC save() 동작:
     *   - @Id 필드가 null  → INSERT (새 레코드 생성)
     *   - @Id 필드가 non-null → UPDATE (기존 레코드 수정)
     *
     * 수정 처리:
     *   1. findById()로 기존 Student 로드 (id 보존)
     *   2. setXxx()로 변경할 값 적용
     *   3. save() 호출 → id 있으므로 UPDATE SQL 실행
     */
    @PostMapping("/{id}/edit")
    public String editStudent(
            @PathVariable Long id,
            @Valid @ModelAttribute("studentForm") StudentForm form,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("studentId", id);
            return "student/editForm";
        }

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학생 ID: " + id));
        student.setName(form.getName());
        student.setStudentId(form.getStudentId());
        student.setEmail(form.getEmail());
        studentRepository.save(student); // id 있음 → UPDATE

        return "redirect:/students/{id}"; // PRG
    }

    /**
     * POST /students/{id}/delete — 삭제 처리
     *
     * 왜 GET이 아닌 POST인가?
     *   GET /students/1/delete 링크를 브라우저 프리패치·크롤러가 접근하면
     *   의도치 않게 삭제됨 (보안 취약점)
     *   → 데이터 변경 작업은 반드시 POST(또는 DELETE) 사용
     */
    @PostMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return "redirect:/students"; // PRG: 삭제 후 목록으로
    }

    // ── Lab 05에서 추가: xssTestForm, xssTest ─────────────────────
}
