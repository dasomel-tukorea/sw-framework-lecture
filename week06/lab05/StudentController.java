// [복사 위치] src/main/java/kr/ac/tukorea/swframework/controller/StudentController.java
// [작업] lab04 파일을 이 파일로 교체 (XSS 테스트 메서드 추가)
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
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.web.util.HtmlUtils;

/**
 * 학생 MVC 컨트롤러 — Lab 05
 *
 * Lab 04 대비 추가:
 *   GET  /students/xss-test  → XSS 비교 폼 (th:utext / th:text / 수동 이스케이프)
 *   POST /students/xss-test  → XSS 비교 처리
 *   GET  /students/filter-test → XSS Servlet Filter 필터 데모 폼
 *   POST /students/filter-test → 필터가 파라미터를 sanitize한 뒤 Controller 진입
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

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학생 ID: " + id));
        model.addAttribute("student", student);
        return "student/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학생 ID: " + id));

        StudentForm form = new StudentForm();
        form.setName(student.getName());
        form.setStudentId(student.getStudentId());
        form.setEmail(student.getEmail());

        model.addAttribute("studentForm", form);
        model.addAttribute("studentId", id);
        return "student/editForm";
    }

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
        studentRepository.save(student);

        return "redirect:/students/{id}";
    }

    @PostMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return "redirect:/students";
    }

    // ── Lab 05-A: XSS 방어 비교 (th:utext / th:text / 수동 이스케이프) ──

    /**
     * GET /students/xss-test — XSS 비교 폼
     */
    @GetMapping("/xss-test")
    public String xssTestForm() {
        return "student/xssTest";
    }

    /**
     * POST /students/xss-test — XSS 4가지 방어 방법 비교
     *
     * ① th:utext          — 이스케이프 없음 (위험)
     * ② th:text           — Thymeleaf 뷰 레이어에서 자동 이스케이프 (안전)
     * ③ 수동 이스케이프    — Controller에서 HtmlUtils.htmlEscape() 적용 후 th:utext 출력
     *                       → 모든 HTML 태그를 문자열로 변환 (안전하지만 <b>도 이스케이프)
     * ④ OWASP Sanitizer   — allowlist 방식: 허용된 태그(<b>,<i> 등)만 통과, <script> 제거
     *                       → 안전한 HTML은 보존, 위험한 태그만 제거 (안전 + 서식 유지)
     *
     * HtmlUtils vs OWASP 핵심 차이:
     *   입력: <b>굵은글씨</b><script>alert('XSS')</script>
     *   HtmlUtils → &lt;b&gt;굵은글씨&lt;/b&gt;...  (모든 HTML 이스케이프)
     *   OWASP     → <b>굵은글씨</b>               (script만 제거, b 태그 보존)
     */
    @PostMapping("/xss-test")
    public String xssTest(@RequestParam String userInput, Model model) {
        // allowlist 정책: 서식(b, i, u 등) + 링크(a) 허용, 나머지 위험 태그 제거
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

        model.addAttribute("userInput", userInput);                           // ① 원본 (위험)
        model.addAttribute("manualEscaped", HtmlUtils.htmlEscape(userInput)); // ③ 수동 이스케이프
        model.addAttribute("owaspSanitized", policy.sanitize(userInput));     // ④ OWASP sanitize
        return "student/xssTest";
    }

    // ── Lab 05-B: XSS Servlet Filter 필터 레벨 XSS 방어 ─────────────────────

    /**
     * GET /students/filter-test — XSS Servlet Filter 필터 데모 폼
     */
    @GetMapping("/filter-test")
    public String filterTestForm() {
        return "student/xssFilterTest";
    }

    /**
     * POST /students/filter-test — 필터가 sanitize 한 뒤 Controller 진입
     *
     * XssEscapeFilterConfig 의 XssEscapeFilter 가 /students/filter-test 에 등록됨
     * → 이 메서드가 실행될 때 userInput 은 이미 HtmlUtils.htmlEscape() 가 적용된 상태
     * → th:utext 로 출력해도 태그가 실행되지 않음 (필터에서 이미 방어됨)
     *
     * 핵심 차이:
     *   - /xss-test : Controller 에서 수동으로 escape() 호출
     *   - /filter-test: Servlet Filter 가 Controller 진입 전에 자동 sanitize
     *                 → Controller 코드에 이스케이프 로직 없음 (관심사 분리)
     */
    @PostMapping("/filter-test")
    public String filterTest(@RequestParam String userInput, Model model) {
        // 이 시점의 userInput 은 필터가 이미 이스케이프한 상태
        // <script>alert('XSS')</script> → &lt;script&gt;alert('XSS')&lt;/script&gt;
        model.addAttribute("userInput", userInput);
        return "student/xssFilterTest";
    }
}
