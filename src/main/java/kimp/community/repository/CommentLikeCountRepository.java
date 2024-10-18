package kimp.community.repository;

import kimp.community.entity.Comment;
import kimp.community.entity.CommentLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeCountRepository extends JpaRepository<CommentLikeCount, Long> {

    public CommentLikeCount findByComment(Comment comment);

}
