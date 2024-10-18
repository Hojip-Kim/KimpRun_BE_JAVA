package kimp.community.dao;

import kimp.community.entity.Comment;
import kimp.community.entity.CommentLikeCount;

public interface CommentLikeCountDao {

    public CommentLikeCount createCommentLikeCount(Comment comment);
}
