package kimp.community.dao.impl;

import kimp.community.dao.BoardDao;
import kimp.community.entity.Board;
import kimp.community.entity.Category;
import kimp.community.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class BoardDaoImpl implements BoardDao {

    private final BoardRepository boardRepository;

    public BoardDaoImpl(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public Board createBoard(String title, String content) {
        if((title == null || title.isEmpty()) || (content == null || content.isEmpty())){
            throw new IllegalArgumentException("title or content must not empty");
        }
        Board board = new Board(title, content);

        return boardRepository.save(board);
    }

    @Override
    public Board getBoardById(Long id) {
        Optional<Board> board = boardRepository.findById(id);
        if(board.isEmpty()){
            throw new IllegalArgumentException("not have board id :" + id);
        }
        return board.orElse(null);
    }

    @Override
    public Long getBoardCount() {
        return boardRepository.count();
    }

    @Override
    public Board updateBoardById(Long id, String title, String content){
        Board board = getBoardById(id);

        return updateBoard(board, title, content);
    }

    @Override
    @Transactional
    public Board updateBoard(Board board, String title, String content){

        if(!title.isEmpty()){
            board.updateTitle(title);
        }
        if(!content.isEmpty()){
            board.updateContent(content);
        }
        return board;
    }

    @Override
    public Boolean deleteBoardById(Long id) {
        this.boardRepository.deleteById(id);

        if(getBoardById(id) != null){
            throw new IllegalArgumentException("board not deleted : id " + id);
        }

        return true;
    }

    @Override
    @Transactional
    public Page<Board> findByCategoryWithPage(Category category, Pageable pageable){
        return this.boardRepository.findByCategoryOrderByRegistedAtDesc(category, pageable);
    }

    @Override
    @Transactional
    public Page<Board> findAllWithPage(Pageable pageable){
        return this.boardRepository.findAllByOrderByRegistedAtDesc(pageable);
    }
}
