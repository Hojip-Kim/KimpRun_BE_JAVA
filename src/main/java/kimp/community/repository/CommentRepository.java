package kimp.community.repository;

import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    public Page<Comment> findByBoard(Board board, Pageable pageable);
}
