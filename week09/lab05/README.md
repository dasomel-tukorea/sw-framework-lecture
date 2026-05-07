# Lab 05 — 게시판(Board) DTO 패턴 (실전 · 선택)

> 학생 CRUD가 학습용 모델이라면, 게시판 CRUD는 **실전에서 재사용 가능한 패턴**이다.

## 학습 포인트

| 주제 | Lab 01 (Student) | Lab 05 (Board) |
|---|---|---|
| 데이터 객체 | `Student.java` (Domain) | `BoardDTO.java` (DTO) |
| 정적 팩토리 | 없음 | `BoardDTO.of()` / `forUpdate()` |
| 검색 SQL 재사용 | 없음 | `<sql id="searchCondition"/>` + `<include/>` |
| 정렬 분기 | 없음 | `<choose>` |
| 페이징 | 없음 | `LIMIT/OFFSET` (W11 본격) |

## 파일

| 파일 | 적용 위치 |
|---|---|
| `BoardDTO.java` | `src/main/java/kr/ac/tukorea/swframework/dto/` |
| `BoardMapper.xml` | `src/main/resources/mapper/` |

## sw-framework-demo와 비교

`sw-framework-demo/src/main/java/kr/ac/tukorea/swframework/dto/BoardDTO.java` 와 동일한 구조 — 실전 코드를 학생 CRUD와 1:1로 비교하며 학습.

```
sw-framework-demo/                  ← 완성된 실전 코드 (참고용)
  ├── dto/BoardDTO.java
  ├── mapper/BoardMapper.java
  ├── resources/mapper/BoardMapper.xml
  ├── service/BoardServiceImpl.java
  └── controller/BoardController.java

week09/lab01/                       ← 이번 주 학습 코드 (작성 대상)
  ├── Student.java
  ├── StudentMapper.java
  ├── StudentMapper.xml
  ├── StudentService.java
  └── StudentController.java
```

## 핵심 패턴 — 정적 팩토리 메서드

```java
// ❌ Bad — Controller에서 id 설정 빠뜨림 위험
@PostMapping("/edit/{id}")
public String edit(@PathVariable Long id, @ModelAttribute BoardDTO form) {
    form.setId(id);                          // 실수로 빠뜨리면 잘못된 row 수정
    boardService.modify(form);
    return "redirect:/board/detail/" + id;
}

// ✓ Good — 정적 팩토리로 id 설정을 강제
@PostMapping("/edit/{id}")
public String edit(@PathVariable Long id, @ModelAttribute BoardDTO form) {
    boardService.modify(BoardDTO.forUpdate(id, form));
    return "redirect:/board/detail/" + id;
}
```

## 핵심 패턴 — `<sql>` + `<include>`

같은 검색 조건이 list와 count 양쪽에서 사용됨 — 한 곳만 수정해도 일관성 보장:

```xml
<sql id="searchCondition">
    <where>
        <if test="...">AND title LIKE ...</if>
    </where>
</sql>

<select id="findAllWithPaging" ...>
    SELECT * FROM board
    <include refid="searchCondition"/>     <!-- 재사용 -->
    LIMIT #{size} OFFSET #{offset}
</select>

<select id="countAll" ...>
    SELECT COUNT(*) FROM board
    <include refid="searchCondition"/>     <!-- 재사용 -->
</select>
```

## 다음 주 예고 (W10)

- `@ControllerAdvice`로 전역 예외 처리
- `EntityNotFoundException` → 404 페이지 라우팅
- DTO 변환 로직을 Service로 위임

## 실행 (옵션)

본 lab은 **참고용**. 실제로 빌드하려면 `BoardMapper.java` 인터페이스도 추가해야 한다 — sw-framework-demo의 코드를 참고.
