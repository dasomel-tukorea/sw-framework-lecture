# Lab 01 — 학생 페이징 (PageDTO + LIMIT/OFFSET)

> "30+ 학생을 한 화면에 나열 X → 10명씩 페이지로 나누기"
>
> **swframework 적용 도메인**: student (게시판이 아닌 학생)

## 학습 포인트

- **OFFSET 공식**: `offset = (page - 1) * size`
- **PageDTO** — 페이지·크기·전체건수·검색조건·정렬 통합 DTO
- **MyBatis `<sql>` + `<include>`** — 검색 조건을 list/count 양쪽에서 재사용
- **`countAll` 별도 쿼리** 필요 (페이지 수 계산용)

## 파일 (swframework에 추가할 코드)

| 파일 | 적용 위치 (swframework) | 설명 |
|---|---|---|
| `PageDTO.java` | `dto/` | **신규** — W11에서 처음 추가 |
| `StudentMapper.java` | `mapper/` | `findAllWithPaging` / `countAll` 메서드 추가 |
| `StudentMapper.xml` | `resources/mapper/` | `<sql id="searchCondition">` + 2개 SELECT 추가 |
| `StudentService.java` | `service/` | `getListWithPaging(PageDTO)` / `getTotalCount(PageDTO)` 추가 |
| `StudentController.java` | `controller/` | `list()` 메서드에 `@ModelAttribute PageDTO` 적용 |
| `student/list.html` | `templates/student/` | 페이징 네비게이션 + 검색·정렬 querystring 보존 |

## 핵심 흐름

```
GET /students?page=2&size=10
   │
   ▼  LoginInterceptor 통과
[StudentController.list()]
   PageDTO page = ...   (page=2, size=10)
   ▼
[StudentService.getListWithPaging(page)]
   ├─ getTotalCount(page)        → COUNT(*) WHERE ...
   └─ getListWithPaging(page)    → SELECT ... LIMIT 10 OFFSET 10
   ▼
[Thymeleaf student/list.html]
   페이징 네비게이션 + 학생 10명
```

## PageDTO 핵심 코드 (신규 파일)

```java
// [복사 위치] src/main/java/kr/ac/tukorea/swframework/dto/PageDTO.java
package kr.ac.tukorea.swframework.dto;

public class PageDTO {
    private int page = 1;          // 현재 페이지
    private int size = 10;         // 페이지당 건수
    private int totalCount;        // 전체 학생 수
    private String searchType;     // name / email / student_id / major
    private String keyword;        // 검색어
    private String sortBy;         // id / name / student_id / major / created_at

    // === Getter / Setter ===
    public int getPage() { return page; }
    public void setPage(int page) { this.page = Math.max(page, 1); }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int t) { this.totalCount = t; }
    public String getSearchType() { return searchType; }
    public void setSearchType(String s) { this.searchType = s; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String k) { this.keyword = k; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String s) { this.sortBy = s; }

    // === 계산 메서드 (Getter는 #{offset}으로 MyBatis 접근 가능) ===
    public int getOffset()      { return (page - 1) * size; }
    public int getTotalPages()  { return (int) Math.ceil((double) totalCount / size); }
    public boolean hasPrev()    { return page > 1; }
    public boolean hasNext()    { return page < getTotalPages(); }
}
```

## StudentMapper.xml — 추가할 2개 SELECT

```xml
<!-- ① 검색 조건 (Lab 04 W10에서 이미 student_id·major 포함된 형태) -->
<sql id="searchCondition">
    <where>
        <if test="searchType == 'name' and keyword != null and keyword != ''">
            AND name LIKE CONCAT('%', #{keyword}, '%')
        </if>
        <if test="searchType == 'email' and keyword != null and keyword != ''">
            AND email LIKE CONCAT('%', #{keyword}, '%')
        </if>
        <if test="searchType == 'student_id' and keyword != null and keyword != ''">
            AND student_id LIKE CONCAT('%', #{keyword}, '%')
        </if>
        <if test="searchType == 'major' and keyword != null and keyword != ''">
            AND major LIKE CONCAT('%', #{keyword}, '%')
        </if>
    </where>
</sql>

<!-- ② 페이징 SELECT -->
<select id="findAllWithPaging" parameterType="PageDTO" resultType="Student">
    SELECT <include refid="studentColumns"/>
    FROM student
    <include refid="searchCondition"/>
    ORDER BY id DESC
    LIMIT #{size} OFFSET #{offset}
</select>

<!-- ③ 전체 건수 (페이지 수 계산용) -->
<select id="countAll" parameterType="PageDTO" resultType="int">
    SELECT COUNT(*) FROM student
    <include refid="searchCondition"/>
</select>
```

## StudentController — list() 수정

```java
// 기존 list() 메서드 변경
@GetMapping
public String list(@ModelAttribute("page") PageDTO pageDTO, Model model) {
    int total = studentService.getTotalCount(pageDTO);
    pageDTO.setTotalCount(total);

    List<Student> students = studentService.getListWithPaging(pageDTO);
    model.addAttribute("students", students);
    // pageDTO는 @ModelAttribute("page")로 이미 Model에 등록됨
    return "student/list";
}
```

## Thymeleaf 페이징 네비게이션 (student/list.html)

```html
<div class="pagination">

    <a th:if="${page.hasPrev()}"
       th:href="@{/students(page=${page.page - 1}, size=${page.size},
                            searchType=${page.searchType}, keyword=${page.keyword},
                            sortBy=${page.sortBy})}">◀ 이전</a>

    <span th:each="i : ${#numbers.sequence(1, page.totalPages)}">
        <a th:href="@{/students(page=${i}, size=${page.size},
                                searchType=${page.searchType}, keyword=${page.keyword},
                                sortBy=${page.sortBy})}"
           th:text="${i}"
           th:classappend="${i == page.page} ? 'current'"></a>
    </span>

    <a th:if="${page.hasNext()}"
       th:href="@{/students(page=${page.page + 1}, ...)}">다음 ▶</a>

</div>
```

> **검색·정렬 querystring 보존이 핵심** — 페이지 이동 시 조건 사라지면 사용자가 처음부터 다시.

## 실습 단계 (30분)

1. **(5분)** student 테이블에 데이터 30건+ 추가 (DataInitializer 보강 또는 `data-w11.sql`)
2. **(3분)** `dto/PageDTO.java` 새 파일 작성
3. **(5분)** `StudentMapper.java`에 `findAllWithPaging` / `countAll` 메서드 추가
4. **(5분)** `StudentMapper.xml`에 `<sql id="searchCondition">` + 2개 SELECT 추가
5. **(5분)** `StudentService` + `StudentController.list()` 수정
6. **(5분)** `student/list.html`에 페이징 네비게이션 추가
7. **(2분)** 실행 + `/students?page=2` 동작 확인

## 확인 포인트

- [ ] `/students` → 첫 페이지 10건만 표시
- [ ] `/students?page=2` → 11~20번 학생
- [ ] `/students?page=3` → 21~30번 학생
- [ ] SQL 로그에 `LIMIT 10 OFFSET 0` / `LIMIT 10 OFFSET 10` 확인
- [ ] 검색 후 페이지 이동 시 검색어 유지

## 흔한 실수

| 실수 | 결과 | 해결 |
|---|---|---|
| `OFFSET (page * size)` (0-based 혼동) | 1페이지가 빈 화면 | `(page - 1) * size` |
| countAll에 검색 조건 누락 | 페이지 수 부정확 | `<include refid="searchCondition"/>` 양쪽 모두 |
| `${page.offset}`이 안 되는 경우 | Getter 누락 | `getOffset()` 메서드 시그니처 확인 |
| @ModelAttribute 이름과 Thymeleaf 변수명 불일치 | `${page.xxx}` 안 나옴 | `@ModelAttribute("page")` 명시 |

## 주차 연결

- **W09 lab03** Dynamic SQL `<if>` `<choose>` `<foreach>` → 본 lab에서 페이징과 결합
- **W10 lab04** 검색 기능 → 본 lab의 `searchCondition` 패턴이 발전형
- **Lab 02** 정렬 추가 / **Lab 03** 블록 페이징 UI / **Lab 04** 파일 첨부
