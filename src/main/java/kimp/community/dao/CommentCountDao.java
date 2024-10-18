package kimp.community.dao;

import kimp.community.entity.Board;
import kimp.community.entity.CommentCount;

public interface CommentCountDao {

    public CommentCount createCommentCount(Board board);
}
