package kimp.community.dao;

import kimp.community.entity.Board;
import kimp.community.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardDao {

    public Board createBoard(String title, String content);

    public Board getBoardById(Long id);

    public Long getBoardCount();

    public Board updateBoardById(Long id, String title, String content);

    public Board updateBoard(Board board, String title, String content);

    public Boolean deleteBoardById(Long id);

    public Page<Board> findByCategoryWithPage(Category category, Pageable pageable);

    public Page<Board> findAllWithPage(Pageable pageable);

    public List<Board> findAllByIds(List<Long> ids);

    public List<Board> activateBoardsPin(List<Board> ids);

    public List<Board> deActivateBoardsPin(List<Board> ids);
}
