package kimp.community.dao;

import kimp.community.entity.Board;
import kimp.community.entity.BoardLikeCount;

public interface BoardLikeCountDao {

    public BoardLikeCount createBoardLikeCount(Board board);

}
