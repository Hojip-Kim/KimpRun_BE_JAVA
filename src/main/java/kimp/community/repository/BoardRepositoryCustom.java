package kimp.community.repository;

import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.entity.Board;
import kimp.community.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

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
    
    /**
     * 특정 날짜 이전에 소프트 삭제된 게시물 개수 조회
     * 
     * @param beforeDate 기준 날짜 (이 날짜 이전에 업데이트된 항목들)
     * @param pageable 배치 크기 제한
     * @return 소프트 삭제된 게시물 개수
     */
    long countSoftDeletedBoardsBeforeDate(LocalDateTime beforeDate, Pageable pageable);
    
    /**
     * 특정 날짜 이전에 소프트 삭제된 게시물들을 완전 삭제
     * 
     * @param beforeDate 기준 날짜 (이 날짜 이전에 업데이트된 항목들)
     * @param pageable 배치 크기 제한
     * @return 삭제된 게시물 개수
     */
    long deleteSoftDeletedBoardsBeforeDate(LocalDateTime beforeDate, Pageable pageable);
}