# Lab 04 — 검색 기능 (Dynamic SQL 응용)

> "W09에서 배운 `<if>` `<choose>` `<where>`를 swframework 학생 검색에 응용"
>
> **swframework 현황**: SearchController + StudentMapper.xml에 이미 모두 적용 — 본 lab은 동작 원리 학습

---

## swframework 적용 현황

### SearchController (이미 적용됨)

`controller/SearchController.java`:

```java
@Controller
@RequestMapping("/students")
public class SearchController {

    private final StudentService studentService;

    @GetMapping("/search")
    public String search(@RequestParam(required = false, defaultValue = "") String type,
                         @RequestParam(required = false, defaultValue = "") String keyword,
                         Model model) {
        List<Student> students = studentService.search(type, keyword);
        model.addAttribute("students", students);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        return "student/list";
    }

    @GetMapping("/by-ids")
    public String findByIds(@RequestParam List<Long> ids, Model model) {
        List<Student> students = studentService.findByIds(ids);
        model.addAttribute("students", students);
        return "student/list";
    }
}
```

### StudentMapper.xml — 검색 유형 4가지 지원

```xml
<select id="findBySearchType" resultType="Student">
    SELECT <include refid="studentColumns"/>
    FROM student
    <where>
        <choose>
            <when test="searchType == 'name' and keyword != null and keyword != ''">
                name LIKE CONCAT('%', #{keyword}, '%')
            </when>
            <when test="searchType == 'email' and keyword != null and keyword != ''">
                email LIKE CONCAT('%', #{keyword}, '%')
            </when>
            <when test="searchType == 'student_id' and keyword != null and keyword != ''">
                student_id LIKE CONCAT('%', #{keyword}, '%')
            </when>
            <when test="searchType == 'major' and keyword != null and keyword != ''">
                major LIKE CONCAT('%', #{keyword}, '%')
            </when>
            <otherwise>
                <!-- keyword 없거나 알 수 없는 type → 전체 조회 -->
            </otherwise>
        </choose>
    </where>
    ORDER BY id DESC
</select>
```

> 본 lab은 이 코드를 **읽고 이해 + 새 검색 조건 추가하는 응용 실습**.

---

## 학습 포인트

- **검색 DTO 패턴** 대신 `@RequestParam` 두 개로 받기 (Controller가 단순)
- **`<sql>` + `<include>`** — 공통 컬럼 목록 재사용 (`studentColumns`)
- **`<choose>`로 검색 타입 분기** — name / email / student_id / major (4가지)
- **`<otherwise>` 조건**으로 검색어 없으면 전체 조회
- **`<foreach>` IN 절** — 다건 조회 `/students/by-ids?ids=1,2,3`

---

## 검색 흐름 (swframework 실 엔드포인트)

```
[브라우저]
   │  GET /students/search?type=name&keyword=홍
   ▼
[LoginInterceptor]   ← 로그인 세션 확인 (W07)
   │
   ▼
[SearchController.search()]
   │   @RequestParam type="name", keyword="홍"
   ▼
[StudentService.search("name", "홍")]
   │
   ▼
[StudentMapper.findBySearchType()]
   │   XML <choose>의 <when test="searchType == 'name' ...">
   │   → name LIKE CONCAT('%', '홍', '%')
   ▼
[MySQL/H2 student 테이블]
   │   결과: [홍길동]
   ▼
[student/list.html] 렌더링
```

---

## URL 매핑표 (swframework 현재 동작)

| URL | 결과 |
|---|---|
| `GET /students` | 전체 학생 3건 |
| `GET /students/search?type=name&keyword=홍` | '홍길동'만 |
| `GET /students/search?type=email&keyword=tukorea` | 이메일에 tukorea 포함된 3건 모두 |
| `GET /students/search?type=student_id&keyword=2023003` | 학번 2023003 시작 학생 |
| `GET /students/search?type=major&keyword=IT` | 전공에 'IT' 포함된 학생 |
| `GET /students/search` (빈값) | `<otherwise>` → 전체 조회 |
| `GET /students/by-ids?ids=1,2,3` | id 1,2,3 학생 3건 |

---

## Thymeleaf 검색 폼 (참고)

`student/list.html`에 추가할 수 있는 패턴:

```html
<form method="get" th:action="@{/students/search}">
    <select name="type">
        <option value="name"       th:selected="${type == 'name'}">이름</option>
        <option value="student_id" th:selected="${type == 'student_id'}">학번</option>
        <option value="email"      th:selected="${type == 'email'}">이메일</option>
        <option value="major"      th:selected="${type == 'major'}">전공</option>
    </select>
    <input type="text" name="keyword" th:value="${keyword}" placeholder="검색어"/>
    <button type="submit">검색</button>
</form>
```

---

## 응용 실습 — 새 검색 조건 추가 (15분)

`StudentMapper.xml`의 `<choose>` 블록에 다음을 추가:

```xml
<when test="searchType == 'all' and keyword != null and keyword != ''">
    name LIKE CONCAT('%', #{keyword}, '%')
    OR email LIKE CONCAT('%', #{keyword}, '%')
    OR student_id LIKE CONCAT('%', #{keyword}, '%')
    OR major LIKE CONCAT('%', #{keyword}, '%')
</when>
```

→ `GET /students/search?type=all&keyword=홍` 으로 4개 필드를 동시에 검색.

---

## 확인 포인트 (week10.http Lab04 섹션)

- [ ] `GET /students/search?type=name&keyword=홍` → '홍길동'만
- [ ] `GET /students/search?type=email&keyword=tukorea` → 3건 모두
- [ ] `GET /students/search` (빈값) → 전체 목록
- [ ] `GET /students/by-ids?ids=1,2,3` → 3건 표시
- [ ] SQL 로그(`log-impl: StdOutImpl`)에서 `<choose>`로 조립된 WHERE 절 확인

---

## 주차 연결

- **W09 lab03** Dynamic SQL `<if>` `<choose>` `<set>` `<foreach>` → 본 lab에서 학생 검색에 응용
- **W11** 페이징 — 같은 `<choose>` 패턴을 `findAllWithPaging` + `countAll`에 재사용
- **W12+** 통계 리포트 — 복잡한 SQL은 결국 MyBatis가 강점
