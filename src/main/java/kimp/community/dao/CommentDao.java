package kimp.community.dao;

import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CommentDao {

    public Comment getComment(long commentId);

    public Comment createComment(User user, Board board, String content, long parentCommentId, int depth);

    public Page<Comment> getComments(long boardId, Pageable pageable);

    public boolean deleteComment(long commentId);

}
