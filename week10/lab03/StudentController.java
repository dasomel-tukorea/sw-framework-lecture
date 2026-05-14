// [복사 위치] src/main/java/kr/ac/tukorea/swframework/controller/StudentController.java
// [작업] swframework 실제 StudentController — addStudent() / editStudent() 부분 발췌
// Week 10 — Lab 03: @Valid + BindingResult 패턴
package kr.ac.tukorea.swframework.controller;

import jakarta.validation.Valid;
import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.dto.StudentForm;
import kr.ac.tukorea.swframework.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @Valid + BindingResult 패턴 — swframework 실제 적용 코드 발췌
 *
 * <p>흐름
 * <ol>
 *   <li>사용자가 폼 제출 → POST /students</li>
 *   <li>Spring이 @ModelAttribute로 StudentForm 객체 자동 바인딩</li>
 *   <li>@Valid 어노테이션 발견 → Bean Validation 동작 (NotBlank·Pattern·Email 등 검사)</li>
 *   <li>검증 결과를 BindingResult에 담음 (반드시 @Valid 바로 뒤!)</li>
 *   <li>bindingResult.hasErrors() 분기 처리</li>
 * </ol>
 *
 * <p>중요한 규칙
 * <ul>
 *   <li>BindingResult가 @Valid 바로 뒤가 아니면 — Spring이 MethodArgumentNotValidException 던짐 → 400 응답</li>
 *   <li>검증 실패 시 redirect X — Model이 살아있어야 에러 메시지 표시 가능</li>
 *   <li>검증 성공 시 PRG 패턴: redirect:/students/{id}</li>
 * </ul>
 *
 * <p>Lab 02 (전역 예외 처리)와의 결합 — 검증/조회/오류 3가지 케이스
 * <ul>
 *   <li>검증 실패 → BindingResult → 폼 재렌더링 (HTTP 200)</li>
 *   <li>학생 조회 실패 → EntityNotFoundException → 404 페이지</li>
 *   <li>그 외 오류 → Exception → 500 페이지</li>
 * </ul>
 */
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ───────────────────────────────────────────────
    // 등록 폼 — 빈 StudentForm을 Model에 담아 전달
    // ───────────────────────────────────────────────
    @GetMapping("/new")
    public String addForm(Model model) {
        model.addAttribute("studentForm", new StudentForm());
        return "student/addForm";
    }

    // ───────────────────────────────────────────────
    // 등록 처리 — @Valid + BindingResult + PRG (redirect:/students/{id})
    // ───────────────────────────────────────────────
    @PostMapping
    public String addStudent(
            @Valid @ModelAttribute("studentForm") StudentForm form,    // ← @Valid
            BindingResult result,                                       // ← 바로 뒤 (필수)
            RedirectAttributes redirectAttributes) {

        // 검증 실패 시 폼 페이지 재렌더링 (사용자 입력값 유지)
        if (result.hasErrors()) {
            return "student/addForm";       // Redirect X — Model 유지
        }

        // 검증 통과 → Student 객체로 변환 → 저장 → PRG (상세 페이지로)
        Student student = new Student(form.getName(), form.getStudentId(), form.getEmail());
        studentService.save(student);

        redirectAttributes.addAttribute("id", student.getId());
        redirectAttributes.addFlashAttribute("status", true);
        return "redirect:/students/{id}";   // 등록 후 상세 페이지로 (목록 아님)
    }

    // ───────────────────────────────────────────────
    // 수정 폼 — 기존 데이터 prefill
    // ───────────────────────────────────────────────
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id);
        if (student == null) {
            // Lab 02 적용 후 — EntityNotFoundException 사용 권장
            throw new IllegalArgumentException("존재하지 않는 학생 ID: " + id);
        }

        StudentForm form = new StudentForm();
        form.setName(student.getName());
        form.setStudentId(student.getStudentId());
        form.setEmail(student.getEmail());

        model.addAttribute("studentForm", form);
        model.addAttribute("studentId", id);
        return "student/editForm";
    }

    // ───────────────────────────────────────────────
    // 수정 처리 — 검증 + ID 결합 + PRG
    // ───────────────────────────────────────────────
    @PostMapping("/{id}/edit")
    public String editStudent(
            @PathVariable Long id,
            @Valid @ModelAttribute("studentForm") StudentForm form,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("studentId", id);    // 폼 재렌더링 시 action URL 유지
            return "student/editForm";
        }

        Student student = studentService.findById(id);
        if (student == null) {
            throw new IllegalArgumentException("존재하지 않는 학생 ID: " + id);
        }
        student.setName(form.getName());
        student.setStudentId(form.getStudentId());
        student.setEmail(form.getEmail());
        studentService.save(student);

        return "redirect:/students/{id}";   // 수정 후 상세 페이지로
    }
}
