# Lab 03 — 블록 페이징 UI (1-10 / 11-20 ...)

> "페이지 100개를 한 줄에 표시하면 UI 망함 — 10개씩 블록으로 묶기"

## 학습 포인트

- **블록 페이징 4공식** — blockSize · currentBlock · startPage · endPage
- **이전 블록·다음 블록** 화살표 처리 (네이버 카페·구글 검색 방식)
- **PageDTO에 메서드 추가** — Thymeleaf에서 직접 호출

## 블록 페이징 계산 (공식 4개)

```
blockSize     = 10                                       (한 블록에 표시할 페이지 수)
currentBlock  = (page - 1) / blockSize                   (현재 페이지가 속한 블록 — 0-based)
startPage     = currentBlock * blockSize + 1             (블록의 첫 페이지)
endPage       = Math.min(startPage + blockSize - 1, totalPages)  (블록의 마지막 페이지)

예) totalPages=37, page=23, blockSize=10
   → currentBlock = (23-1)/10 = 2          (0-based 블록 인덱스)
   → startPage    = 2 * 10 + 1 = 21
   → endPage      = Math.min(30, 37) = 30
   → 표시: [◀ 이전 블록] 21 22 23 24 ... 30 [다음 블록 ▶]
```

## PageDTO에 추가할 메서드 (Lab 01 PageDTO 확장)

```java
private int blockSize = 10;                              // 한 블록에 표시할 페이지 수

public int getBlockSize() { return blockSize; }
public void setBlockSize(int blockSize) { this.blockSize = blockSize; }

/** 현재 페이지가 속한 블록 인덱스 (0-based) */
public int getCurrentBlock() {
    return (page - 1) / blockSize;
}

/** 현재 블록의 시작 페이지 번호 (1-based) */
public int getStartPage() {
    return getCurrentBlock() * blockSize + 1;
}

/** 현재 블록의 마지막 페이지 번호 — 전체 페이지 수를 넘지 않도록 clamp */
public int getEndPage() {
    return Math.min(getStartPage() + blockSize - 1, getTotalPages());
}

/** 이전 블록 존재 여부 — 현재 블록 인덱스가 0이 아닐 때만 true */
public boolean hasPrevBlock() {
    return getCurrentBlock() > 0;
}

/** 다음 블록 존재 여부 — endPage 가 totalPages 보다 작을 때만 true */
public boolean hasNextBlock() {
    return getEndPage() < getTotalPages();
}

/** 이전 블록의 마지막 페이지 (= startPage - 1, 최소 1) */
public int getPrevBlockPage() {
    return Math.max(getStartPage() - 1, 1);
}

/** 다음 블록의 첫 페이지 (= endPage + 1) */
public int getNextBlockPage() {
    return getEndPage() + 1;
}
```

## Thymeleaf 블록 페이징 UI

```html
<div class="pagination">

  <!-- 이전 블록 (10페이지 점프) -->
  <a th:if="${page.hasPrevBlock()}"
     th:href="@{/students(page=${page.prevBlockPage}, size=${page.size}, searchType=${page.searchType}, keyword=${page.keyword}, sortBy=${page.sortBy})}">
     ◀ 이전
  </a>

  <!-- 블록 내 페이지 번호 (startPage ~ endPage) -->
  <span th:each="i : ${#numbers.sequence(page.startPage, page.endPage)}">
    <a th:href="@{/students(page=${i}, size=${page.size}, searchType=${page.searchType}, keyword=${page.keyword}, sortBy=${page.sortBy})}"
       th:text="${i}"
       th:classappend="${i == page.page} ? 'current'"></a>
  </span>

  <!-- 다음 블록 (10페이지 점프) -->
  <a th:if="${page.hasNextBlock()}"
     th:href="@{/students(page=${page.nextBlockPage}, size=${page.size}, searchType=${page.searchType}, keyword=${page.keyword}, sortBy=${page.sortBy})}">
     다음 ▶
  </a>

</div>
```

## 시각 예시

```
총 페이지 37개, 현재 page=23, blockSize=10

  ◀ 이전   [21] [22] [23] [24] [25] [26] [27] [28] [29] [30]   다음 ▶
              ↑
            현재 페이지 (CSS .current)

[다음 ▶] 클릭 → page=31로 이동 → 블록 4 표시:
  ◀ 이전   [31] [32] [33] [34] [35] [36] [37]                    (다음 ▶ 없음)
```

## 확인 포인트

- [ ] `/students?page=23` → 21~30 페이지 번호 표시
- [ ] [다음 ▶] 클릭 → page=31 → 31~37 표시
- [ ] [◀ 이전] 클릭 → page=20 → 11~20 표시
- [ ] 현재 페이지에 `.current` 클래스 (CSS로 색상 강조)
- [ ] 첫 블록에선 [◀ 이전] 숨김 / 마지막 블록에선 [다음 ▶] 숨김

## 실무 팁

- **CSS Grid · Flex로 가로 배치** — 페이지 번호는 inline-block보다 flex가 정렬 깔끔
- **모바일**: 블록 크기를 5로 줄이거나 [≪ 1] [< 이전] [다음 >] [≫ 마지막] 형태
- **SEO**: `<link rel="prev/next">` 태그도 추가 (검색엔진 인덱싱 힌트)

## 주차 연결

- **Lab 01** 페이징 + **Lab 02** 검색·정렬과 결합 — querystring 동일 보존
- **W14 발표**: 블록 페이징은 실무 게시판의 기본 UX
