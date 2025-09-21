package kimp.community.repository;

import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface CommentRepositoryCustom {
    
    /**
     * Board와 함께 사용할 때 - 삭제된 댓글도 포함하여 모든 댓글을 Fetch Join으로 조회
     * N+1 문제와 Lazy Loading 문제를 해결
     */
    Page<Comment> findByBoardWithMemberFetchJoin(Board board, Pageable pageable);
    
    /**
     * 회원 ID로 댓글을 모든 연관 엔티티와 함께 Fetch Join으로 조회
     * N+1 문제와 Lazy Loading 문제를 해결
     * 
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 페이징된 댓글 목록 (Member, Board, LikeCount 포함)
     */
    Page<Comment> findByMemberIdWithAllFetchOrderByRegistedAtDesc(Long memberId, Pageable pageable);
    
    /**
     * 특정 날짜 이전에 소프트 삭제된 댓글 개수 조회
     * 
     * @param beforeDate 기준 날짜 (이 날짜 이전에 업데이트된 항목들)
     * @param pageable 배치 크기 제한
     * @return 소프트 삭제된 댓글 개수
     */
    long countSoftDeletedCommentsBeforeDate(LocalDateTime beforeDate, Pageable pageable);
    
    /**
     * 특정 날짜 이전에 소프트 삭제된 댓글들을 완전 삭제
     * 
     * @param beforeDate 기준 날짜 (이 날짜 이전에 업데이트된 항목들)
     * @param pageable 배치 크기 제한
     * @return 삭제된 댓글 개수
     */
    long deleteSoftDeletedCommentsBeforeDate(LocalDateTime beforeDate, Pageable pageable);
}