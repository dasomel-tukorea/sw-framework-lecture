# 11주차 핵심 용어집

처음 접하는 개념은 **비유**로 감을 잡고, **정확한 정의**로 마무리.

> 본 주차는 W10까지 완성된 student CRUD 위에 **페이징·검색·정렬·파일 업로드**를 얹는다.
> 따라서 W07(세션·비밀번호), W09(MyBatis), W10(MVC·`@Valid`) 용어를 자주 인용한다.

---

## 1. 페이징 (Pagination)

> "도서관 책장 — 5만 권을 한 줄에 못 꽂으니, 10권씩 칸을 나눈다"

전체 데이터를 **page·size 단위로 잘라** 한 화면에 일부만 노출. 30+ 학생을 한 페이지에 쏟지 않고 10명씩 보여줌으로써 **서버 부하·네트워크 트래픽·사용자 인지 비용**을 모두 절감.

```
GET /students?page=2&size=10  →  11~20번 학생만 응답
```

> W09에서 배운 MyBatis 위에 **SQL 한 줄(LIMIT/OFFSET)** 만 더해서 구현.

---

## 2. LIMIT / OFFSET

> "줄 서기 — 앞에서 N명 건너뛰고(OFFSET) 다음 M명만 받는다(LIMIT)"

MySQL 페이징의 표준 문법.

```sql
SELECT * FROM student
ORDER BY id DESC
LIMIT 10 OFFSET 10;     -- 11~20번
```

| 공식 |
|---|
| `offset = (page - 1) * size` |
| `totalPages = Math.ceil(totalCount / size)` |

> 주의: OFFSET이 커질수록(예: 100,000) **앞 행들을 모두 훑고 버리므로** 느려진다. 운영 단계에선 **커서(cursor) 기반 페이징** 또는 **인덱스 조건(`WHERE id < ?`)** 으로 대체.

---

## 3. PageDTO

> "주문서 — 페이지·크기·검색어·정렬 기준을 한 장에 모아 전달"

Controller·Service·Mapper·View 사이를 오가는 **페이징 통합 DTO** (W10 [[3. DTO]] 개념의 페이징 특화 버전).

| 필드 | 역할 |
|---|---|
| `page` / `size` | 현재 페이지 / 페이지당 건수 |
| `totalCount` | COUNT(*) 결과 (Service에서 주입) |
| `searchType` / `keyword` | 검색 조건 |
| `sortBy` | 정렬 기준 (화이트리스트 키) |
| `getOffset()` | `(page-1)*size` 계산 메서드 |
| `getTotalPages()` | 올림 계산 메서드 |

> getter 메서드(`getOffset`)는 **MyBatis에서 `#{offset}`** 으로, **Thymeleaf에서 `${page.offset}`** 으로 양쪽에서 사용 가능.

---

## 4. `countAll` 쿼리

> "택배 송장 두 장 — 내용물 목록 한 장, 총 개수 한 장"

페이지 수(`totalPages`)를 계산하려면 **현재 페이지의 행 개수가 아니라 검색 조건을 만족하는 전체 개수**가 필요. 따라서 list 쿼리와 별도로 `COUNT(*)` 쿼리를 한 번 더 실행.

```xml
<select id="findAllWithPaging" resultType="Student">
  SELECT * FROM student
  <include refid="searchCondition"/>
  ORDER BY id DESC
  LIMIT #{size} OFFSET #{offset}
</select>

<select id="countAll" resultType="int">
  SELECT COUNT(*) FROM student
  <include refid="searchCondition"/>
</select>
```

---

## 5. MyBatis `<sql>` / `<include>`

> "공용 부품 — 동일한 WHERE 절을 두 쿼리에서 똑같이 끼워 쓰기"

검색 조건을 한 번 정의(`<sql id="...">`)하고 list 쿼리·count 쿼리 양쪽에서 `<include>`로 재사용. **검색 조건이 바뀔 때 한 곳만 고치면 됨** — W09의 단일 쿼리 한계를 보완.

```xml
<sql id="searchCondition">
  <where>
    <if test="keyword != null and keyword != ''">
      <choose>
        <when test="searchType == 'name'">     name LIKE CONCAT('%', #{keyword}, '%')</when>
        <when test="searchType == 'email'">    email LIKE CONCAT('%', #{keyword}, '%')</when>
        <otherwise>                            student_id LIKE CONCAT('%', #{keyword}, '%')</otherwise>
      </choose>
    </if>
  </where>
</sql>
```

---

## 6. MyBatis `<choose>` / `<when>` / `<otherwise>`

> "if-elif-else의 SQL판"

여러 분기 중 **하나만 선택**해서 SQL을 조립. 정렬 기준(`sortBy`) 같은 **다지선택**에 적합.

```xml
<choose>
  <when test="sortBy == 'name'">       ORDER BY name ASC</when>
  <when test="sortBy == 'student_id'"> ORDER BY student_id ASC</when>
  <when test="sortBy == 'major'">      ORDER BY major ASC</when>
  <otherwise>                          ORDER BY id DESC</otherwise>
</choose>
```

> 다음 항목 [[7. 정렬 화이트리스트]]와 한 쌍 — `<choose>`가 **화이트리스트 강제 장치** 역할을 한다.

---

## 7. 정렬 화이트리스트 (Sort Whitelist)

> "VIP 명단 — 명단에 적힌 이름만 입장 가능"

사용자 입력(`sortBy=name`)을 **그대로 SQL에 박지 않고** 미리 정해둔 키 집합(`name`/`student_id`/`major`/`created_at`)에 매칭. W09 [[8. `#{}` vs `${}`]]에서 다룬 SQL Injection 방어의 정렬 버전.

```java
// ❌ 위험: sortBy=";DROP TABLE student;--" 가 SQL에 직접 들어감
"ORDER BY " + sortBy

// ✓ 안전: <choose>로 4가지 키 중 하나만 매칭, 나머지는 기본값
<choose><when test="sortBy=='name'">...</when>...</choose>
```

> 컬럼명·정렬키워드는 PreparedStatement의 `?`로 바인딩 불가 → **반드시 화이트리스트**.

---

## 8. 블록 페이징 (Block Pagination)

> "달력 — 1~10일, 11~20일처럼 묶음 단위로 넘기기"

페이지가 100개를 넘어도 화면엔 **10개씩 한 블록**만 표시. 블록 끝에서 `‹ 이전` / `다음 ›` 으로 점프.

```
blockSize     = 10
currentBlock  = (page - 1) / blockSize           // 0-based
startPage     = currentBlock * blockSize + 1
endPage       = Math.min(startPage + blockSize - 1, totalPages)

hasPrevBlock  = currentBlock > 0
hasNextBlock  = endPage < totalPages
```

예) totalPages=37, page=23 → currentBlock=2, startPage=21, endPage=30

---

## 9. Querystring 보존

> "장바구니 유지 — 페이지를 넘겨도 검색어·정렬 기준은 그대로"

페이지 링크가 `?page=3` 만 들고 가면 검색·정렬 조건이 **리셋**된다. Thymeleaf의 `th:href`에 모든 조건을 함께 실어 보내야 함.

```html
<a th:href="@{/students(
     page=${p},
     size=${page.size},
     searchType=${page.searchType},
     keyword=${page.keyword},
     sortBy=${page.sortBy})}">[[${p}]]</a>
```

---

## 10. `MultipartFile`

> "택배 박스 — 폼 데이터(작은 봉투)와 파일(본체)을 한 번에 받음"

Spring이 `multipart/form-data` 요청을 자동 파싱해 만들어 주는 객체. 파일 이름·크기·바이트 스트림에 접근 가능.

```html
<form method="post" enctype="multipart/form-data">   <!-- 필수 -->
  <input type="file" name="attachment">
</form>
```

```java
@PostMapping
public String add(@Valid StudentForm form,
                  BindingResult br,
                  @RequestParam("attachment") MultipartFile file) { ... }
```

| 메서드 | 용도 |
|---|---|
| `getOriginalFilename()` | 원본 파일명 |
| `getSize()` | 바이트 크기 |
| `isEmpty()` | 빈 업로드 여부 |
| `transferTo(File)` | 디스크에 저장 |

---

## 11. UUID 파일명

> "옷장 번호표 — 같은 이름의 옷이 들어와도 번호로 구분"

`UUID.randomUUID()`로 만든 36자리 고유 ID를 저장 파일명에 붙여 **충돌**과 **Path Traversal**을 동시에 방어.

```java
String saved = UUID.randomUUID() + "_" + file.getOriginalFilename();
// e.g. a3f2c8e1-..._학생증.pdf
```

| 위협 | 일반 파일명 | UUID 파일명 |
|---|---|---|
| 동일명 덮어쓰기 | `학생증.pdf` 두 명 → 손실 | 항상 unique |
| Path Traversal | `../../etc/passwd` 가능 | `..` 문자 무력화 |
| 원본명 보존 | 그대로 노출 | DB의 `attachment_name` 컬럼에 별도 저장 |

---

## 12. 원본명 vs 저장명 분리

> "택배 송장과 박스 번호 — 받는 사람에겐 송장, 창고에선 박스 번호"

DB에 두 컬럼을 따로 둔다.

| 컬럼 | 값 | 용도 |
|---|---|---|
| `attachment_name` | `학생증.pdf` | 다운로드 시 사용자에게 보여줄 이름 |
| `saved_name` | `a3f2..._학생증.pdf` | 디스크 실제 저장명 (UUID) |

> 다운로드 응답의 `Content-Disposition` 헤더에는 **원본명**을, 파일 시스템 접근에는 **저장명**을 사용.

---

## 13. `Content-Disposition: attachment`

> "전단지 vs 봉투 — `inline`은 즉시 펼침, `attachment`는 받아서 보관"

HTTP 응답 헤더로 브라우저에게 **다운로드 대화상자**를 띄우라고 지시.

```java
response.setHeader("Content-Disposition",
    "attachment; filename=\"" + URLEncoder.encode(originalName, "UTF-8") + "\"");
```

| 값 | 브라우저 동작 |
|---|---|
| `inline` (기본) | 가능하면 브라우저에서 바로 표시 |
| `attachment; filename=...` | 다운로드 받아서 디스크에 저장 |

> 한글 파일명은 `URLEncoder.encode(...)`로 감싸지 않으면 깨진다.

---

## 14. 업로드 보안 3종 세트

| # | 위협 | 대응 |
|---|---|---|
| ① | 거대한 파일 → 디스크/메모리 고갈 | `spring.servlet.multipart.max-file-size: 10MB` |
| ② | 실행 파일 업로드 (`.jsp`, `.sh`) | **확장자 화이트리스트** (pdf, png, jpg 등) |
| ③ | `../`로 상위 디렉터리 접근 | **UUID 저장명** + `transferTo`에 절대 경로만 사용 |

> 업로드 디렉터리는 **웹 루트(static/) 바깥**에 두기 — 정적 URL로 직접 접근 차단.

---

## 15. `member` 테이블 + `PasswordUtil` 재사용

> "기존 도구 다시 쓰기 — W07에서 만든 비밀번호 유틸을 그대로 호출"

W07에서는 회원이 `ConcurrentHashMap`(메모리)에 있었음 → 서버 재시작 시 휘발. W11에서 **MySQL `member` 테이블**로 영구화하고, **`kr.ac.tukorea.swframework.util.PasswordUtil`** 의 정적 메서드만 호출.

```java
// W07에서 작성한 유틸 그대로
String hash = PasswordUtil.encode(rawPassword);          // 회원가입
boolean ok  = PasswordUtil.matches(input, member.getPassword()); // 로그인
```

> 새 `BCryptPasswordEncoder` Bean을 만들지 말 것 — **재사용**이 핵심.

---

## 16. BCrypt (복습 — W07)

> "암호 슬롯 — 단방향 함수, 같은 비밀번호도 매번 다른 해시"

비밀번호 단방향 해시 알고리즘. 솔트(salt)를 자동 포함하므로 같은 비밀번호도 매번 다른 결과 → **레인보우 테이블 공격 무력화**.

```
encode("1234")  →  $2a$10$abc...   (회원가입 시)
encode("1234")  →  $2a$10$xyz...   (다시 호출하면 다른 해시)
matches("1234", "$2a$10$abc...") → true
```

> DB의 `password` 컬럼은 60자 이상 여유. 평문 저장은 **법적 책임**(개인정보보호법 §29).

---

## 17. WBS (Work Breakdown Structure)

> "이사 체크리스트 — 큰 일을 작은 작업으로 쪼개 일정·담당·산출물 배정"

프로젝트 전체를 **작업(Task)** 단위로 분해해 일정을 시각화한 표·간트차트. 팀 프로젝트 마감(W14~W15) 전에 **누가·언제·무엇을 산출하는지** 합의.

| 시트 | 내용 |
|---|---|
| 일정 개요 | 시작·종료·마일스톤 |
| 간트차트 | Phase × 주차 가시화 |
| 팀원별 집계 | 1인당 작업량 균형 확인 |
| 리스크 | 발생 가능 이슈 + 대응 |
| 협업 규칙 | Git 브랜치·코드리뷰·회의 주기 |

> W11 과제 산출물: `docs/W11_WBS.md` + `assignment/W11_WBS_템플릿.xlsx`

---

## 18. 1:10:100 법칙 (복습 — W09)

| 단계 | 발견 비용 |
|---|---|
| 분석 (W08) | 1 |
| 구현 (W11) | 10 |
| 배포 후 (W12~) | 100 |

> W11에서 페이징 쿼리 한 줄을 잘못 짜면 W14 발표장에서 100배로 돌아온다. **콘솔 로그로 SQL을 직접 확인**하는 습관.

---

## 주차 연결 지도

```
W07 세션·PasswordUtil ────────────────────┐
                                          │ 재사용 (Lab 05)
W09 MyBatis · #{}/${} · @Transactional ───┤
                                          │ <sql>/<include>/<choose>로 확장
W10 MVC · DTO · @Valid · PRG · 세션 ──────┤
                                          │ 페이징·검색·정렬·파일 첨부 추가
W11 ◀── 이번 주
                                          │
W12 배포 · W13 품질 · W14·W15 발표 ───────┘
```
