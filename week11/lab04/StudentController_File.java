// [복사 위치] src/main/java/kr/ac/tukorea/swframework/controller/StudentController.java
// [작업] Week 11 lab04 — 파일 업로드/다운로드 확장
//        lab01 StudentController 에 본 파일의 메서드를 병합하는 형태로 사용한다.
//
// 사전 준비:
//   1) Student 도메인에 attachmentName / savedName 두 필드를 추가 (DB 컬럼: attachment_name / saved_name)
//      → map-underscore-to-camel-case 설정으로 자동 매핑
//   2) StudentMapper.xml 의 INSERT / UPDATE / SELECT 에 두 컬럼을 함께 매핑
//   3) application.yml 에 file.upload-dir 와 spring.servlet.multipart.* 설정
//
// Lombok 미사용 — 생성자 주입과 상수 정의를 수강생이 직접 확인한다.
package kr.ac.tukorea.swframework.controller;

import jakarta.validation.Valid;
import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.dto.PageDTO;
import kr.ac.tukorea.swframework.dto.StudentForm;
import kr.ac.tukorea.swframework.service.StudentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 학생 컨트롤러 — 파일 업로드/다운로드 확장 (Week 11 lab04)
 *
 * 핵심:
 *   1. @Value("${file.upload-dir}") — application.yml 의 업로드 경로 주입
 *   2. addStudent() — MultipartFile + StudentForm 바인딩, UUID 저장명 생성
 *   3. downloadAttachment() — URLEncoder.encode(filename, UTF-8) 로 한글 파일명 깨짐 방지
 *
 * 보안 5가지 가드 (PDF p.21):
 *   ① 파일 크기 제한        — application.yml `spring.servlet.multipart.max-file-size` (10MB)
 *   ② 확장자 화이트리스트   — ALLOWED_EXTENSIONS 검사
 *   ③ MIME 타입 화이트리스트 — Content-Type 검사
 *   ④ Path Traversal 방어   — UUID 파일명으로 사용자 입력 무시 + 다운로드 시 정규화
 *   ⑤ 원본 파일명 sanitize  — Path.getFileName() 만 추출하여 디렉토리 구분자 제거
 */
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    // application.yml 의 file.upload-dir 값을 자동 주입
    @Value("${file.upload-dir}")
    private String uploadDir;

    // [보안 ②] 업로드 허용 확장자 화이트리스트
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("pdf", "png", "jpg", "jpeg", "gif", "hwp", "docx", "xlsx", "txt");

    // [보안 ③] 업로드 허용 MIME 타입 화이트리스트
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "image/png", "image/jpeg", "image/gif",
            "application/x-hwp",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain"
    );

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ── 목록 (lab01 과 동일) ───────────────────────────────

    @GetMapping
    public String list(@ModelAttribute("page") PageDTO pageDTO, Model model) {
        int totalCount = studentService.getTotalCount(pageDTO);
        pageDTO.setTotalCount(totalCount);
        List<Student> students = studentService.getListWithPaging(pageDTO);
        model.addAttribute("students", students);
        return "student/list";
    }

    // ── 상세 (첨부 다운로드 링크 노출은 detail.html 에서 처리) ──

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getDetail(id));
        return "student/detail";
    }

    // ── 등록 폼 ─────────────────────────────────────────

    @GetMapping("/new")
    public String addForm(Model model) {
        model.addAttribute("studentForm", new StudentForm());
        return "student/addForm";
    }

    /**
     * 학생 등록 — MultipartFile + Bean Validation + PRG.
     *
     * 폼은 multipart/form-data 로 전송되어야 한다 (addForm.html 의 enctype 속성).
     *
     * @param form               StudentForm — @Valid 적용
     * @param result             BindingResult — @Valid 바로 뒤에 선언 필수
     * @param attachment         업로드 파일 (선택 — 미첨부 시 null 또는 isEmpty)
     * @param redirectAttributes PRG redirect 시 PathVariable id 전달
     */
    @PostMapping
    public String addStudent(@Valid @ModelAttribute("studentForm") StudentForm form,
                             BindingResult result,
                             @RequestParam(value = "attachment", required = false) MultipartFile attachment,
                             RedirectAttributes redirectAttributes) throws IOException {

        if (result.hasErrors()) {
            // 검증 실패 — 폼으로 돌아가 에러 메시지 노출
            return "student/addForm";
        }

        Student student = new Student(form.getName(), form.getStudentId(), form.getEmail(), form.getMajor());

        // 파일이 첨부된 경우에만 처리 (필수 항목 아님)
        if (attachment != null && !attachment.isEmpty()) {
            // [보안 ⑤] 원본 파일명 sanitize — 디렉토리 구분자 제거
            String rawOriginal = attachment.getOriginalFilename();
            String originalName = (rawOriginal == null)
                    ? ""
                    : Path.of(rawOriginal).getFileName().toString();

            // [보안 ①②③] 크기 + 확장자 + MIME 화이트리스트 검사
            validateAttachment(originalName, attachment.getContentType(), attachment.getSize());

            // [보안 ④] UUID 저장명으로 사용자 입력 무시 — Path Traversal 방어
            String savedName = UUID.randomUUID() + "_" + originalName;

            Path uploadPath = Path.of(uploadDir).resolve(savedName);
            Files.createDirectories(uploadPath.getParent());
            attachment.transferTo(uploadPath);

            student.setAttachmentName(originalName); // 원본 파일명 (사용자 표시용)
            student.setSavedName(savedName);         // UUID 저장 파일명 (서버 내부용)
        }

        studentService.create(student);

        // PRG — 등록된 학생의 상세 페이지로 리다이렉트
        redirectAttributes.addAttribute("id", student.getId());
        redirectAttributes.addFlashAttribute("status", true);
        return "redirect:/students/{id}";
    }

    /**
     * 파일 다운로드 — Content-Disposition 으로 브라우저 다운로드 대화상자 표시.
     *
     * 한글 파일명 깨짐 방지(PDF p.22):
     *   filename*=UTF-8''<URL encoded> 형식 + filename="" fallback.
     *   모던 브라우저(Chrome / Edge / Firefox / Safari)는 filename* 우선 사용.
     *   URLEncoder.encode 후 '+' 는 RFC 5987 에 맞춰 '%20' 으로 치환.
     */
    @GetMapping("/attachment/{savedName}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable String savedName)
            throws MalformedURLException {

        // [보안 ④] savedName 이 UUID 기반이지만 한 번 더 정규화하여 경로 이탈 시도 차단
        Path filePath = Path.of(uploadDir).resolve(savedName).normalize();
        if (!filePath.startsWith(Path.of(uploadDir).normalize())) {
            return ResponseEntity.badRequest().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        // UUID_원본명 형식이므로 첫 '_' 이후가 원본 파일명
        String filename = resource.getFilename();
        String originalForHeader = (filename != null && filename.contains("_"))
                ? filename.substring(filename.indexOf('_') + 1)
                : filename;

        // 한글 파일명을 위해 RFC 5987 filename*=UTF-8'' 인코딩 사용
        String encoded = URLEncoder.encode(originalForHeader, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + originalForHeader + "\"; "
                                + "filename*=UTF-8''" + encoded)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // =================================================================
    // 첨부파일 보안 검증 — 확장자 + MIME + 크기 + 빈 파일명 가드
    // =================================================================
    private void validateAttachment(String originalName, String contentType, long size) {

        // [보안 ⑤] 빈 파일명 차단
        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("원본 파일명이 비어 있습니다.");
        }

        // [보안 ①] 크기 — application.yml 의 max-file-size 를 통과해도 다시 가드 (10MB)
        long maxBytes = 10L * 1024 * 1024;
        if (size > maxBytes) {
            throw new IllegalArgumentException("파일 크기는 10MB 이하만 허용됩니다.");
        }

        // [보안 ②] 확장자 화이트리스트
        int dotIdx = originalName.lastIndexOf('.');
        if (dotIdx < 0 || dotIdx == originalName.length() - 1) {
            throw new IllegalArgumentException("확장자가 없는 파일은 업로드할 수 없습니다.");
        }
        String ext = originalName.substring(dotIdx + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("허용되지 않은 확장자입니다: " + ext);
        }

        // [보안 ③] MIME 타입 — 브라우저가 보낸 Content-Type 을 한 번 더 검증
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("허용되지 않은 MIME 타입입니다: " + contentType);
        }
    }
}
