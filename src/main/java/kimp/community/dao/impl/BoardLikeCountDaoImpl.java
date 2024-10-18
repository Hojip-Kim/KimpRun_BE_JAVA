package kimp.community.dao.impl;

import kimp.community.dao.BoardLikeCountDao;
import kimp.community.entity.Board;
import kimp.community.entity.BoardLikeCount;
import kimp.community.repository.BoardLikeCountRepository;
import org.springframework.stereotype.Repository;

@Repository
public class BoardLikeCountDaoImpl implements BoardLikeCountDao {

    private final BoardLikeCountRepository boardLikeCountRepository;

    public BoardLikeCountDaoImpl(BoardLikeCountRepository boardLikeCountRepository) {
        this.boardLikeCountRepository = boardLikeCountRepository;
    }

    @Override
    public BoardLikeCount createBoardLikeCount(Board board) {

        BoardLikeCount boardLikeCount = new BoardLikeCount(board);

        return boardLikeCountRepository.save(boardLikeCount);
    }
}
