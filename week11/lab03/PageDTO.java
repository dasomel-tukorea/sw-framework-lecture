// [복사 위치] src/main/java/kr/ac/tukorea/swframework/dto/PageDTO.java
// [작업] Week 11 lab03 — lab01 의 PageDTO 에 블록 페이징 필드/메서드를 확장한 버전으로 교체.
//        - Lombok 미사용 (수강생이 계산 메서드 직접 작성)
//        - 블록 페이징 공식 4종(blockSize / currentBlock / startPage / endPage)을 모두 포함
package kr.ac.tukorea.swframework.dto;

/**
 * 페이징 + 검색 + 정렬 + 블록 페이징 조건을 담는 DTO — Week 11 lab03
 *
 * 단건 페이징 공식:
 *   offset       = (page - 1) * size
 *   totalPages   = ceil(totalCount / size)
 *
 * 블록 페이징 공식:
 *   blockSize    = 10                                   (한 블록에 표시할 페이지 수)
 *   currentBlock = (page - 1) / blockSize               (0-based 블록 인덱스)
 *   startPage    = currentBlock * blockSize + 1
 *   endPage      = min(startPage + blockSize - 1, totalPages)
 *
 * 예) totalPages=37, page=23, blockSize=10
 *     currentBlock = (23-1)/10 = 2
 *     startPage    = 2*10+1    = 21
 *     endPage      = min(30, 37) = 30
 *     → 표시: [« 이전] 21 22 23 24 25 26 27 28 29 30 [다음 »]
 */
public class PageDTO {

    // === 단건 페이징 ===
    private int page = 1;          // 현재 페이지 (기본값 1)
    private int size = 10;         // 페이지당 건수 (기본값 10)
    private int totalCount;        // 전체 건수
    private String searchType;     // 검색 유형 (name / email / student_id / major)
    private String keyword;        // 검색어
    private String sortBy;         // 정렬 기준 (id / name / student_id / major / created_at)

    // === 블록 페이징 (lab03 신규) ===
    private int blockSize = 10;    // 한 블록에 표시할 페이지 수 (기본값 10)

    public PageDTO() {}

    // === Getter / Setter ===

    public int getPage() { return page; }

    /** 페이지 값 설정 시 최소 1페이지를 보장 (음수/0 방어). */
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

    public int getBlockSize() { return blockSize; }
    public void setBlockSize(int blockSize) { this.blockSize = blockSize; }

    // === 단건 페이징 계산 메서드 (Thymeleaf + MyBatis 공용) ===

    /** MyBatis 에서 #{offset}, Thymeleaf 에서 ${page.offset} 으로 사용 */
    public int getOffset() {
        return (page - 1) * size;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalCount / size);
    }

    public boolean hasPrev() {
        return page > 1;
    }

    public boolean hasNext() {
        return page < getTotalPages();
    }

    // =================================================================
    // 블록 페이징 (lab03 신규)
    // =================================================================

    /**
     * 현재 페이지가 속한 블록 인덱스 (0-based).
     *   page=1~10  → 0
     *   page=11~20 → 1
     *   page=23    → 2
     */
    public int getCurrentBlock() {
        return (page - 1) / blockSize;
    }

    /** 현재 블록의 시작 페이지 번호 (1-based) */
    public int getStartPage() {
        return getCurrentBlock() * blockSize + 1;
    }

    /**
     * 현재 블록의 마지막 페이지 번호.
     * 전체 페이지 수를 넘지 않도록 클램프(clamp).
     */
    public int getEndPage() {
        return Math.min(getStartPage() + blockSize - 1, getTotalPages());
    }

    /** 이전 블록 존재 여부 (현재가 첫 블록이 아닐 때만 true) */
    public boolean hasPrevBlock() {
        return getCurrentBlock() > 0;
    }

    /** 다음 블록 존재 여부 (현재 블록의 endPage 가 마지막 페이지보다 작을 때만 true) */
    public boolean hasNextBlock() {
        return getEndPage() < getTotalPages();
    }

    /**
     * 이전 블록의 마지막 페이지 (= 현재 startPage - 1).
     * 첫 블록이면 1로 클램프하여 안전하게 동작.
     */
    public int getPrevBlockPage() {
        return Math.max(getStartPage() - 1, 1);
    }

    /** 다음 블록의 첫 페이지 (= 현재 endPage + 1) */
    public int getNextBlockPage() {
        return getEndPage() + 1;
    }
}
