package kimp.community.dao;

import kimp.community.entity.BoardCount;
import kimp.community.entity.Category;

public interface BoardCountDao {

    public BoardCount createBoardCount(Category category);
}
