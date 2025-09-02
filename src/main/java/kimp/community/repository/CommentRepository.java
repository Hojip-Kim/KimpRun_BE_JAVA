package kimp.community.repository;

import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    public Page<Comment> findByBoardAndIsDeletedFalse(Board board, Pageable pageable);
    
    Page<Comment> findByMemberAndIsDeletedFalseOrderByRegistedAtDesc(Member member, Pageable pageable);
    
    @Query("SELECT c FROM Comment c " +
           "LEFT JOIN FETCH c.member m " +
           "LEFT JOIN FETCH c.board b " +
           "LEFT JOIN FETCH c.likeCount " +
           "WHERE m.id = :memberId " +
           "AND m.isActive = true " +
           "AND c.isDeleted = false " +
           "ORDER BY c.registedAt DESC")
    Page<Comment> findByMemberIdWithAllFetchOrderByRegistedAtDesc(@Param("memberId") Long memberId, Pageable pageable);
    
    public Page<Comment> findByBoard(Board board, Pageable pageable);
    Page<Comment> findByMemberOrderByRegistedAtDesc(Member member, Pageable pageable);
}
