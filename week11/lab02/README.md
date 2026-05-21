# Lab 02 — 학생 검색 + 정렬 통합

> "페이징 위에 정렬·검색까지 — `<choose>`로 ORDER BY 안전하게"
>
> **swframework 도메인**: student (이름·학번·이메일·전공)

## 학습 포인트

- **`<choose>` ORDER BY** — `#{}` 바인딩은 ORDER BY에 못 쓰임 → 화이트리스트 분기 필수
- **swframework 검색 4가지 그대로 활용** — name·email·student_id·major (W10 lab04와 일관)
- **검색·정렬 querystring 보존** — 페이지 이동·정렬 변경 시 모든 조건 유지

## 핵심 SQL (StudentMapper.xml — Lab 01 확장)

```xml
<select id="findAllWithPaging" parameterType="PageDTO" resultType="Student">
    SELECT <include refid="studentColumns"/>
    FROM student
    <include refid="searchCondition"/>
    ORDER BY
    <choose>
        <when test="sortBy == 'name'">name ASC</when>
        <when test="sortBy == 'student_id'">student_id ASC</when>
        <when test="sortBy == 'major'">major ASC</when>
        <when test="sortBy == 'created_at'">created_at DESC</when>
        <otherwise>id DESC</otherwise>  <!-- 기본: 최신순 -->
    </choose>
    LIMIT #{size} OFFSET #{offset}
</select>
```

> **`ORDER BY #{sortBy}` 절대 금지** — `#{}`는 PreparedStatement `?`로 바인딩되어 **문자열 리터럴**이 됨 → 정렬 X.
> `${sortBy}`로 쓰면 동작하지만 **SQL Injection** 노출. → `<choose>` 화이트리스트가 정답.

## Controller — list()에서 정렬 자동 처리

```java
@GetMapping
public String list(@ModelAttribute("page") PageDTO pageDTO, Model model) {
    // PageDTO에 sortBy 필드 있음 → @ModelAttribute로 자동 바인딩
    int total = studentService.getTotalCount(pageDTO);
    pageDTO.setTotalCount(total);
    model.addAttribute("students", studentService.getListWithPaging(pageDTO));
    return "student/list";
}
```

> URL 예: `GET /students?page=1&size=10&searchType=name&keyword=홍&sortBy=name`

## Thymeleaf — 정렬 링크 (헤더 클릭으로 정렬 토글)

```html
<thead>
<tr>
    <th><a th:href="@{/students(page=1, size=${page.size}, searchType=${page.searchType}, keyword=${page.keyword}, sortBy='id')}">번호</a></th>
    <th><a th:href="@{/students(page=1, size=${page.size}, searchType=${page.searchType}, keyword=${page.keyword}, sortBy='name')}">이름</a></th>
    <th><a th:href="@{/students(page=1, size=${page.size}, searchType=${page.searchType}, keyword=${page.keyword}, sortBy='student_id')}">학번</a></th>
    <th><a th:href="@{/students(page=1, size=${page.size}, searchType=${page.searchType}, keyword=${page.keyword}, sortBy='major')}">전공</a></th>
    <th><a th:href="@{/students(page=1, size=${page.size}, searchType=${page.searchType}, keyword=${page.keyword}, sortBy='created_at')}">등록일</a></th>
</tr>
</thead>
```

> 정렬 변경 시 `page=1`로 리셋 — 이전 페이지의 OFFSET이 새 정렬에 의미 없음.

## 검색 폼 — hidden으로 정렬 유지

```html
<form method="get" th:action="@{/students}">
    <input type="hidden" name="sortBy" th:value="${page.sortBy}"/>  <!-- 정렬 유지 -->
    <input type="hidden" name="page" value="1"/>                    <!-- 검색은 1페이지부터 -->

    <select name="searchType">
        <option value="name"       th:selected="${page.searchType == 'name'}">이름</option>
        <option value="student_id" th:selected="${page.searchType == 'student_id'}">학번</option>
        <option value="email"      th:selected="${page.searchType == 'email'}">이메일</option>
        <option value="major"      th:selected="${page.searchType == 'major'}">전공</option>
    </select>
    <input type="text" name="keyword" th:value="${page.keyword}"/>
    <button type="submit">검색</button>
</form>
```

## 확인 포인트

- [ ] 이름 헤더 클릭 → ORDER BY name ASC로 정렬
- [ ] 페이지 이동 시 정렬 조건 유지
- [ ] 검색 후 정렬 변경 → 검색어도 유지
- [ ] SQL 로그에 `ORDER BY name ASC` 확인 (`#{sortBy}` 같은 placeholder가 아님)

## 흔한 실수

| 실수 | 결과 |
|---|---|
| `ORDER BY #{sortBy}` | 정렬 안 됨 (문자열 리터럴) |
| `ORDER BY ${sortBy}` | SQL Injection 위험 |
| 정렬 변경 시 page 유지 | OFFSET이 어긋나 의도와 다른 페이지 |
| `<otherwise>` 누락 | 알 수 없는 sortBy → SQL 오류 |

## 주차 연결

- **W09** `<choose>` Dynamic SQL → 본 lab에서 ORDER BY에 응용
- **W07** SQL Injection 방어 → `#{}`는 못 쓰는 자리에 화이트리스트로 대체
- **Lab 01** 페이징과 결합 / **Lab 03** 블록 페이징 UI에서도 동일 패턴
- **W10 lab04** 검색 (`SearchController` `/students/search`) → **본 lab에서 페이징과 통합**되어 `/students` 단일 엔드포인트로
