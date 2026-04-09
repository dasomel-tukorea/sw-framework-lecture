// [복사 위치] src/main/java/kr/ac/tukorea/swframework/controller/StudentController.java
// [작업] lab02 파일을 이 파일로 교체 (@Valid + BindingResult 추가)
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
 * 학생 MVC 컨트롤러 — Lab 03
 *
 * Lab 02 대비 변경:
 *   addStudent()에 @Valid + BindingResult 추가
 *   → Bean Validation 검증 실패 시 폼 재렌더링
 *
 * 주의: BindingResult는 반드시 @Valid 파라미터 바로 다음에 선언해야 함
 *   틀린 예) @Valid StudentForm form, Model model, BindingResult result
 *   올바른 예) @Valid StudentForm form, BindingResult result, ...
 *   순서가 틀리면 검증 실패 시 BindingResult 대신 MethodArgumentNotValidException 발생
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

    /**
     * POST /students — 학생 등록 처리 (Bean Validation 적용)
     *
     * @Valid: StudentForm 필드의 검증 어노테이션 실행
     * BindingResult: 검증 결과 저장 컨테이너
     *   - result.hasErrors() == true → 검증 실패 → 폼 재렌더링
     *   - result.hasErrors() == false → 검증 성공 → 저장 후 redirect
     *
     * 검증 실패 시 return "student/addForm":
     *   - @ModelAttribute("studentForm") 덕분에 입력값이 Model에 유지됨
     *   - th:errors="*{name}" 등이 에러 메시지를 화면에 표시
     */
    @PostMapping
    public String addStudent(
            @Valid @ModelAttribute("studentForm") StudentForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // 검증 실패: 폼 뷰 재렌더링 (입력값 + 에러 메시지 유지)
            return "student/addForm";
        }

        Student student = new Student(form.getName(), form.getStudentId(), form.getEmail());
        Student saved = studentRepository.save(student);

        redirectAttributes.addAttribute("id", saved.getId());
        redirectAttributes.addFlashAttribute("status", true);
        return "redirect:/students/{id}"; // PRG
    }

    // ── Lab 04에서 추가: detail, editForm, editStudent, deleteStudent ──
    // ── Lab 05에서 추가: xssTestForm, xssTest ─────────────────────────
}
