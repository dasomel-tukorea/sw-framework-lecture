// [복사 위치] src/main/java/kr/ac/tukorea/swframework/controller/StudentController.java
// [작업] Week 11 lab01 — 페이징/검색 통합 컨트롤러
//        - URL 은 W07 이래 사용 중인 REST 스타일 /students (복수형) 유지
//        - 인터페이스 StudentService 에만 의존 (구현체 교체 자유)
//        - Lombok 미사용 — 생성자 주입과 메서드 시그니처를 수강생이 직접 확인
package kr.ac.tukorea.swframework.controller;

import jakarta.validation.Valid;
import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.dto.PageDTO;
import kr.ac.tukorea.swframework.dto.StudentForm;
import kr.ac.tukorea.swframework.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 학생 관리 컨트롤러 — 페이징 + 검색 + CRUD
 *
 * URL 매핑 (REST 스타일):
 *   GET  /students              — 목록 (검색·페이징)
 *   GET  /students/new          — 등록 폼
 *   POST /students              — 등록 처리 (PRG)
 *   GET  /students/{id}         — 상세
 *   GET  /students/{id}/edit    — 수정 폼
 *   POST /students/{id}/edit    — 수정 처리
 *   POST /students/{id}/delete  — 삭제 처리
 */
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ── 목록 ──────────────────────────────────────────────

    /**
     * 학생 목록 + 검색 + 페이징.
     *
     * @ModelAttribute("page") 의 효과:
     *   1) PageDTO 의 getter/setter 로 쿼리스트링을 자동 바인딩
     *   2) Model 에 "page" 이름으로 등록 → Thymeleaf 에서 ${page.xxx} 로 접근
     */
    @GetMapping
    public String list(@ModelAttribute("page") PageDTO pageDTO, Model model) {
        // 검색 조건이 반영된 전체 학생 수 — 페이지 수 계산용
        int totalCount = studentService.getTotalCount(pageDTO);
        pageDTO.setTotalCount(totalCount);

        // 현재 페이지 학생 목록 (LIMIT/OFFSET)
        List<Student> students = studentService.getListWithPaging(pageDTO);
        model.addAttribute("students", students);

        return "student/list";
    }

    // ── 등록 폼 + PRG ──────────────────────────────────

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

        Student student = new Student(form.getName(), form.getStudentId(), form.getEmail(), form.getMajor());
        studentService.create(student);

        // PRG — 등록된 학생의 상세 페이지로 리다이렉트
        redirectAttributes.addAttribute("id", student.getId());
        redirectAttributes.addFlashAttribute("status", true);
        return "redirect:/students/{id}";
    }

    // ── 상세 / 수정 / 삭제 ───────────────────────────────

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Student student = studentService.getDetail(id);
        model.addAttribute("student", student);
        return "student/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Student student = studentService.getDetail(id);

        // 도메인 → 폼 DTO 복사 (도메인을 폼에 직접 노출하지 않음)
        StudentForm form = new StudentForm();
        form.setName(student.getName());
        form.setStudentId(student.getStudentId());
        form.setEmail(student.getEmail());
        form.setMajor(student.getMajor());

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

        Student student = studentService.getDetail(id);
        student.setName(form.getName());
        student.setStudentId(form.getStudentId());
        student.setEmail(form.getEmail());
        student.setMajor(form.getMajor());
        studentService.modify(student);

        return "redirect:/students/{id}";
    }

    @PostMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        studentService.remove(id);
        return "redirect:/students";
    }
}
