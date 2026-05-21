# Lab 04 — 학생 자료 첨부 (파일 업로드/다운로드)

> "학생 등록·수정 시 학생증·자기소개서 같은 자료 첨부 — MultipartFile + UUID + Content-Disposition"

## 학습 포인트

- **`MultipartFile`** — Spring이 multipart/form-data 자동 파싱
- **UUID 파일명** — 충돌 방지 + Path Traversal 방어
- **원본 파일명 분리** — DB에 `attachment_name`(표시용) + `saved_name`(UUID 저장용) 둘 다
- **Content-Disposition: attachment** — 브라우저가 다운로드 대화상자 표시

## 사전 준비

### application.yaml

```yaml
spring:
  servlet:
    multipart:
      max-file-size:    10MB    # 개별 파일
      max-request-size: 20MB    # 전체 요청

file:
  upload-dir: ./uploads          # 운영: 절대 경로 또는 S3
```

### student 테이블 컬럼 추가

```sql
ALTER TABLE student
  ADD COLUMN attachment_name VARCHAR(255) NULL,   -- 원본 파일명 (예: '학생증.pdf')
  ADD COLUMN saved_name      VARCHAR(255) NULL;   -- UUID 저장 파일명
```

### Student 도메인에 필드 추가

```java
public class Student {
    // ... 기존 필드: id, name, studentId, email, major, createdAt
    private String attachmentName;   // map-underscore-to-camel-case로 자동 매핑
    private String savedName;
    // Getter/Setter 추가
}
```

## 업로드 흐름

```
[사용자]
  multipart/form-data
  ├─ name:       "홍길동"
  ├─ studentId:  "202300001"
  ├─ email:      "hong@tukorea.ac.kr"
  └─ attachment: <File 객체>   ← enctype="multipart/form-data"
  │
  ▼  POST /students
[StudentController.addStudent()]
  │  MultipartFile attachment 파라미터 자동 바인딩
  ▼
[파일 처리]
  ├─ 원본명:    attachment.getOriginalFilename()  →  "학생증.pdf"
  ├─ UUID:      UUID.randomUUID() + "_" + 원본명   →  "a3f2..._학생증.pdf"
  └─ transferTo(uploadDir + savedName)
  │
  ▼
[DB INSERT]
  student (name, student_id, email, major, attachment_name, saved_name)
        ('홍길동', '202300001', '...', 'IT경영', '학생증.pdf', 'a3f2..._학생증.pdf')
  │
  ▼  redirect:/students/{id} (PRG)
```

## Controller 핵심 코드 (swframework 적용)

```java
// StudentController.java — @RequestMapping("/students")
@Value("${file.upload-dir}")
private String uploadDir;

// [보안 ②] 허용 확장자 화이트리스트
private static final Set<String> ALLOWED_EXTENSIONS =
        Set.of("pdf", "png", "jpg", "jpeg", "gif", "hwp", "docx", "xlsx", "txt");

// [보안 ③] 허용 MIME 타입 화이트리스트
private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
        "application/pdf",
        "image/png", "image/jpeg", "image/gif",
        "application/x-hwp",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain"
);

@PostMapping
public String addStudent(@Valid @ModelAttribute("studentForm") StudentForm form,
                         BindingResult result,
                         @RequestParam(value = "attachment", required = false) MultipartFile attachment,
                         RedirectAttributes redirectAttributes) throws IOException {

    if (result.hasErrors()) {
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

        student.setAttachmentName(originalName); // 원본 파일명 (표시용)
        student.setSavedName(savedName);         // UUID 저장명 (서버 내부용)
    }

    studentService.create(student);

    redirectAttributes.addAttribute("id", student.getId());
    redirectAttributes.addFlashAttribute("status", true);
    return "redirect:/students/{id}";
}
```

## 다운로드 핵심 코드 (한글 파일명 깨짐 방지)

```java
@GetMapping("/attachment/{savedName}")
public ResponseEntity<Resource> downloadAttachment(@PathVariable String savedName)
        throws MalformedURLException {

    // [보안 ④] savedName 정규화 — uploadDir 밖으로 경로 이탈 차단
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

    // RFC 5987 — 한글 파일명을 위해 filename*=UTF-8'' 인코딩 (모던 브라우저 우선)
    String encoded = URLEncoder.encode(originalForHeader, StandardCharsets.UTF_8)
            .replace("+", "%20");

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + originalForHeader + "\"; "
                            + "filename*=UTF-8''" + encoded)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
}
```

## Thymeleaf 업로드 폼 (addForm.html)

```html
<!-- enctype="multipart/form-data" 필수 -->
<form method="post" th:action="@{/students}" th:object="${studentForm}"
      enctype="multipart/form-data">

    <label>이름</label>
    <input type="text" th:field="*{name}"/>
    <span th:if="${#fields.hasErrors('name')}" th:errors="*{name}"></span>

    <label>학번</label>
    <input type="text" th:field="*{studentId}"/>

    <label>이메일</label>
    <input type="email" th:field="*{email}"/>

    <!-- 자료 첨부 (W11 신규) -->
    <label>자료 첨부 (선택)</label>
    <input type="file" name="attachment"/>

    <button type="submit">등록</button>
</form>
```

## Thymeleaf 다운로드 링크 (detail.html)

```html
<!-- savedName 있을 때만 표시 -->
<div th:if="${student.savedName != null}">
    첨부자료:
    <a th:href="@{/students/attachment/{name}(name=${student.savedName})}"
       th:text="${student.attachmentName}">학생증.pdf</a>
</div>
```

## 보안 5가지 가드 (source: `validateAttachment()`)

| # | 위협 | 방어 |
|---|---|---|
| ① | **큰 파일 업로드 DOS** | `application.yml` `max-file-size: 10MB` + 서비스단 size 재검증 |
| ② | **실행 파일 업로드** (`.jsp`, `.exe`) | `ALLOWED_EXTENSIONS` 확장자 화이트리스트 |
| ③ | **위장 Content-Type** | `ALLOWED_MIME_TYPES` MIME 타입 화이트리스트 |
| ④ | **Path Traversal** (`../../etc/passwd`) | UUID 저장명 + 다운로드 시 `Path.normalize()` + `startsWith(uploadDir)` 검사 |
| ⑤ | **빈 파일명 / 디렉토리 구분자** | `Path.getFileName()` 으로 sanitize + 빈 문자열 차단 |

```java
// validateAttachment(originalName, contentType, size) — Controller 내부 private 메서드
// ① 크기 — 10MB 초과 시 IllegalArgumentException
// ② 확장자 — 점(.)이 없거나 화이트리스트 외 → 거부
// ③ MIME — Content-Type 이 화이트리스트 외 → 거부
// ⑤ 빈 파일명 — originalName.isBlank() → 거부
```

## 확인 포인트

- [ ] 파일 업로드 → DB의 `attachment_name`/`saved_name` 컬럼에 값 저장
- [ ] 서버의 `uploads/` 디렉토리에 UUID 파일명으로 저장됨
- [ ] 다운로드 링크 클릭 → 원본 파일명으로 다운로드 대화상자
- [ ] 파일 없는 학생의 상세 페이지 → 첨부 영역 숨김
- [ ] 10MB 초과 파일 → `MaxUploadSizeExceededException`

## 흔한 실수

| 실수 | 결과 | 해결 |
|---|---|---|
| `enctype="multipart/form-data"` 누락 | 파일이 전송되지 않음 | form 태그 속성 확인 |
| `uploads/` 디렉토리 미생성 | `NoSuchFileException` | `Files.createDirectories()` |
| 원본 파일명 그대로 저장 | 한글 깨짐·충돌 | UUID 접두사 추가 |
| 다운로드 filename에 원본명 노출 X | 사용자에게 UUID 그대로 보임 | Content-Disposition filename에 원본명 |

## 주차 연결

- **W07** XSS·SQL Injection → 본 lab에서 **Path Traversal 방어** 추가
- **W10 lab02** `@ControllerAdvice` → `MaxUploadSizeExceededException` 처리 가능
- **W14 발표**: 첨부파일은 실무 게시판·신청서 같은 도메인의 핵심 기능
