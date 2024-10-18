package kimp.community.dao.impl;

import kimp.community.dao.BoardViewDao;
import kimp.community.entity.Board;
import kimp.community.entity.BoardViews;
import kimp.community.repository.BoardViewsRepository;
import org.springframework.stereotype.Repository;

@Repository
public class BoardViewDaoImpl implements BoardViewDao {
    private final BoardViewsRepository boardViewsRepository;

    public BoardViewDaoImpl(BoardViewsRepository boardViewsRepository) {
        this.boardViewsRepository = boardViewsRepository;
    }

    @Override
    public BoardViews createBoardView(Board board) {

        BoardViews boardViews = new BoardViews(board);

        return boardViewsRepository.save(boardViews);
    }
}
