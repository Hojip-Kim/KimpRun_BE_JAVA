package kimp.community.repository;

import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.entity.Board;
import kimp.community.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
    
    /**
     * 카테고리별 게시글 조회 (QueryDSL, fetch join으로 N+1 방지)
     */
    Page<Board> findByCategoryWithFetchJoinOrderByRegistedAtDesc(Category category, Pageable pageable);
    
    /**
     * 전체 게시글 조회 (QueryDSL, fetch join으로 N+1 방지)
     */
    Page<Board> findAllWithFetchJoinOrderByRegistedAtDesc(Pageable pageable);
    
    /**
     * 카테고리별 게시글 DTO 직접 조회 (완전한 N+1 방지)
     */
    Page<BoardResponseDto> findBoardDtosByCategoryOrderByRegistedAtDesc(Category category, Pageable pageable);
    
    /**
     * 카테고리 ID별 게시글 DTO 직접 조회 (완전한 N+1 방지)
     */
    Page<BoardResponseDto> findBoardDtosByCategoryIdOrderByRegistedAtDesc(Long categoryId, Pageable pageable);
    
    /**
     * 전체 게시글 DTO 직접 조회 (완전한 N+1 방지)
     */
    Page<BoardResponseDto> findAllBoardDtosOrderByRegistedAtDesc(Pageable pageable);
    
    /**
     * 공지사항 우선 전체 게시글 DTO 조회 (첫 페이지용)
     * isPin=true인 게시물을 최대 10개까지 먼저 조회하고, 나머지는 일반 게시물로 채움
     */
    Page<BoardResponseDto> findAllBoardDtosWithPinnedFirstOrderByRegistedAtDesc(Pageable pageable);
    
    /**
     * 공지사항 우선 카테고리별 게시글 DTO 조회 (첫 페이지용)
     * isPin=true인 게시물을 최대 10개까지 먼저 조회하고, 나머지는 일반 게시물로 채움
     */
    Page<BoardResponseDto> findBoardDtosByCategoryIdWithPinnedFirstOrderByRegistedAtDesc(Long categoryId, Pageable pageable);
    
    /**
     * 특정 멤버의 게시글 DTO 조회
     */
    Page<BoardResponseDto> findBoardDtosByMemberOrderByRegistedAtDesc(Long memberId, Pageable pageable);
}