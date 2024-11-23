package kimp.community.dao;

import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CommentDao {

    public Comment getComment(long commentId);

    public Comment createComment(Member member, Board board, String content, long parentCommentId, int depth);

    public Page<Comment> getComments(Board board, Pageable pageable);

    public boolean deleteComment(long commentId);

}
