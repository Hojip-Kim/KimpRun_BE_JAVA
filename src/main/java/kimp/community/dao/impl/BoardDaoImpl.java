package kimp.community.dao.impl;

import kimp.community.dao.BoardDao;
import kimp.community.entity.Board;
import kimp.community.entity.Category;
import kimp.community.repository.BoardRepository;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Board title and content cannot be null or empty", HttpStatus.BAD_REQUEST, "BoardDaoImpl.createBoard");
        }
        Board board = new Board(title, content);

        return boardRepository.save(board);
    }

    @Override
    public Board getBoardById(Long id) {
        Optional<Board> board = boardRepository.findById(id);
        if(board.isEmpty()){
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, "Board not found with ID: " + id, HttpStatus.NOT_FOUND, "BoardDaoImpl.getBoardById");
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
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "Failed to delete board with ID: " + id, HttpStatus.INTERNAL_SERVER_ERROR, "BoardDaoImpl.deleteBoardById");
        }

        return true;
    }

    @Override
    @Transactional
    public Page<Board> findByCategoryWithPage(Category category, Pageable pageable){
        return this.boardRepository.findByCategoryWithFetchJoinOrderByRegistedAtDesc(category, pageable);
    }

    @Override
    @Transactional
    public Page<Board> findAllWithPage(Pageable pageable){
        return this.boardRepository.findAllWithFetchJoinOrderByRegistedAtDesc(pageable);
    }

    @Override
    @Transactional
    public List<Board> findAllByIds(List<Long> ids) {

        List<Board> boards = this.boardRepository.findAllById(ids);

        if(boards.isEmpty()){
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, "No boards found for the provided IDs", HttpStatus.NOT_FOUND, "BoardDaoImpl.findAllByIds");
        }

        return boards;
    }

    @Override
    @Transactional
    public List<Board> activateBoardsPin(List<Board> boards) {
        for(Board board : boards){
            board.activePin();
        }

        boolean isCompleted = true;

        for(Board board : boards){
            if(!board.isPin()){
                isCompleted = false;
            }
        }

        if(!isCompleted){
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "Failed to activate pin for some boards", HttpStatus.INTERNAL_SERVER_ERROR, "BoardDaoImpl.activateBoardsPin");
        }

        return boards;
    }

    @Override
    @Transactional
    public List<Board> deActivateBoardsPin(List<Board> boards) {
        for(Board board : boards){
            board.deactivePin();
        }

        boolean isCompleted = true;

        for(Board board : boards){
            if(board.isPin()){
                isCompleted = false;
            }
        }

        if(!isCompleted){
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "Failed to deactivate pin for some boards", HttpStatus.INTERNAL_SERVER_ERROR, "BoardDaoImpl.deActivateBoardsPin");
        }

        return boards;
    }

}
