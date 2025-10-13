package kimp.community.factory;

import kimp.community.repository.BoardLikeRepository;
import kimp.community.repository.BoardRepository;
import org.springframework.stereotype.Component;

@Component
public class BoardRepositoryFactory {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;

    public BoardRepositoryFactory(BoardRepository boardRepository, BoardLikeRepository boardLikeRepository) {
        this.boardRepository = boardRepository;
        this.boardLikeRepository = boardLikeRepository;
    }

    public BoardRepository getBoardRepository() {
        return boardRepository;
    }

    public BoardLikeRepository getBoardLikeRepository() {
        return boardLikeRepository;
    }
}
