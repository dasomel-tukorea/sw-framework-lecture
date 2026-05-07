// Week 09 — Lab 04 게시판 실전 Mapper Interface
// 프로젝트 경로: src/main/java/kr/ac/tukorea/swframework/mapper/BoardMapper.java
//
// sw-framework-demo의 BoardMapper와 동일 — 실전 패턴 학습용
package kr.ac.tukorea.swframework.mapper;

import kr.ac.tukorea.swframework.dto.BoardDTO;
import kr.ac.tukorea.swframework.dto.PageDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 게시판 CRUD + 검색 + 페이징용 Mapper 인터페이스
 *
 * <p>Lab 01의 StudentMapper와 비교 학습 — DTO를 사용하는 점이 가장 큰 차이.
 * <ul>
 *   <li>StudentMapper: Domain(Student) ← DB와 1:1</li>
 *   <li>BoardMapper:   DTO(BoardDTO) + PageDTO(검색·정렬·페이징 파라미터)</li>
 * </ul>
 *
 * <p>namespace는 BoardMapper.xml의 namespace와 일치:
 * {@code kr.ac.tukorea.swframework.mapper.BoardMapper}
 */
@Mapper
public interface BoardMapper {

    // ============================================================
    // 기본 CRUD
    // ============================================================

    /** 단건 상세 조회 */
    BoardDTO findById(Long id);

    /** 등록 — useGeneratedKeys로 채번된 PK가 dto.id에 자동 반영 */
    int insert(BoardDTO board);

    /** 수정 — BoardDTO.forUpdate(id, form) 패턴과 함께 사용 */
    int update(BoardDTO board);

    /** 삭제 */
    int delete(Long id);

    // ============================================================
    // 검색 + 페이징 (PageDTO 활용)
    // ============================================================

    /**
     * 검색 + 정렬 + 페이징 적용된 게시글 목록
     *
     * @param page 검색어·정렬·offset/size 정보
     * @return 현재 페이지의 게시글 목록
     */
    List<BoardDTO> findAllWithPaging(PageDTO page);

    /**
     * 전체 게시글 수 — 페이지 계산용
     *
     * <p>같은 검색 조건({@code <sql id="searchCondition"/>})이
     * findAllWithPaging과 countAll 양쪽에서 {@code <include/>}로 재사용된다.
     */
    int countAll(PageDTO page);
}
