// Week 09 — Lab 04 게시판 DTO 패턴 (실전 · 선택)
// 프로젝트 경로: src/main/java/kr/ac/tukorea/swframework/dto/BoardDTO.java
//
// sw-framework-demo의 BoardDTO와 동일 — 실전 패턴 학습용
package kr.ac.tukorea.swframework.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 게시글 정보를 계층 간에 전달하기 위한 DTO 클래스
 *
 * <p>Domain ≠ DTO
 * <ul>
 *   <li><b>Domain</b>: DB 테이블과 1:1 매핑되는 객체 (Student.java)</li>
 *   <li><b>DTO</b>: Controller ↔ Service ↔ Mapper 사이에서 데이터를 주고받는 객체</li>
 * </ul>
 *
 * <p>Lombok 어노테이션
 * <ul>
 *   <li>{@code @Getter}/{@code @Setter}: 모든 필드의 접근자 자동 생성</li>
 *   <li>{@code @NoArgsConstructor}: 기본 생성자 (MyBatis 결과 매핑에 필수)</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
public class BoardDTO {

    private Long id;                   // 게시글 번호 (PK, AUTO_INCREMENT)
    private String title;              // 제목
    private String content;            // 내용
    private String author;             // 작성자
    private LocalDateTime createdAt;   // 작성일 (map-underscore-to-camel-case)
    private LocalDateTime updatedAt;   // 수정일

    // ============================================================
    // 정적 팩토리 메서드 — Week 10 심화에서 본격 활용
    // ============================================================

    /**
     * 개별 필드로 BoardDTO 생성
     *
     * <pre>BoardDTO board = BoardDTO.of(1L, "제목", "내용", "홍길동");</pre>
     */
    public static BoardDTO of(Long id, String title, String content, String author) {
        BoardDTO dto = new BoardDTO();
        dto.setId(id);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setAuthor(author);
        return dto;
    }

    /**
     * 수정용 DTO 생성 — URL의 {id} + Form 데이터 합치기
     *
     * <pre>
     * &#64;PostMapping("/edit/{id}")
     * public String edit(&#64;PathVariable Long id, &#64;ModelAttribute BoardDTO form) {
     *     boardService.modify(BoardDTO.forUpdate(id, form));
     *     return "redirect:/board/detail/" + id;
     * }
     * </pre>
     *
     * Controller에서 id 설정을 빠뜨리는 실수를 방지한다.
     */
    public static BoardDTO forUpdate(Long id, BoardDTO form) {
        BoardDTO dto = new BoardDTO();
        dto.setId(id);
        dto.setTitle(form.getTitle());
        dto.setContent(form.getContent());
        return dto;
    }
}
