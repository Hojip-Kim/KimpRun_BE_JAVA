package kimp.community.repository;

import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    public Page<Comment> findByBoardAndIsDeletedFalse(Board board, Pageable pageable);
    
    Page<Comment> findByMemberAndIsDeletedFalseOrderByRegistedAtDesc(Member member, Pageable pageable);
    
    public Page<Comment> findByBoard(Board board, Pageable pageable);
    Page<Comment> findByMemberOrderByRegistedAtDesc(Member member, Pageable pageable);
}
