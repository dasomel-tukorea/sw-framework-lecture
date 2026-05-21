// [복사 위치] src/main/java/kr/ac/tukorea/swframework/dto/PageDTO.java
// [작업] Week 11 lab01 신규 생성 — 페이징 + 검색 + 정렬 조건 통합 DTO
//        - Lombok 미사용 (수강생이 getter/setter 와 계산 메서드의 관계를 직접 확인)
//        - lab03 에서 본 클래스에 블록 페이징 필드/메서드를 확장한다 (덮어쓰기)
package kr.ac.tukorea.swframework.dto;

/**
 * 페이징 + 검색 + 정렬 조건을 담는 DTO
 *
 * Controller 에서 파라미터를 바인딩받고, MyBatis 에서 SQL 파라미터로 사용한다.
 *
 * 핵심 공식: offset = (page - 1) * size
 *   page=1, size=10 → offset=0   (1~10번)
 *   page=2, size=10 → offset=10  (11~20번)
 *
 * Thymeleaf 에서는 ${page.offset} / ${page.totalPages} 형태로 getter 호출.
 */
public class PageDTO {

    private int page = 1;          // 현재 페이지 (기본값 1)
    private int size = 10;         // 페이지당 건수 (기본값 10)
    private int totalCount;        // 전체 건수 (Controller 에서 setter 로 세팅)
    private String searchType;     // 검색 유형 (name / email / student_id / major)
    private String keyword;        // 검색어
    private String sortBy;         // 정렬 기준 (id / name / student_id / major / created_at)

    public PageDTO() {}

    // === Getter / Setter ===

    public int getPage() { return page; }

    /**
     * 페이지 값 설정 시 최소 1페이지를 보장한다 (음수/0 방어).
     */
    public void setPage(int page) { this.page = Math.max(page, 1); }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public String getSearchType() { return searchType; }
    public void setSearchType(String searchType) { this.searchType = searchType; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    // === 계산 메서드 (Thymeleaf 와 MyBatis 양쪽에서 사용) ===

    /**
     * 오프셋 계산 — MyBatis 에서 #{offset} 으로 접근 가능.
     * getter 형식이므로 Thymeleaf 에서도 ${page.offset} 으로 사용 가능.
     */
    public int getOffset() {
        return (page - 1) * size;
    }

    /**
     * 전체 페이지 수 계산 — 올림 처리.
     */
    public int getTotalPages() {
        return (int) Math.ceil((double) totalCount / size);
    }

    /**
     * 이전 페이지 존재 여부 (단건 이동용)
     */
    public boolean hasPrev() {
        return page > 1;
    }

    /**
     * 다음 페이지 존재 여부 (단건 이동용)
     */
    public boolean hasNext() {
        return page < getTotalPages();
    }
}
