package kimp.community.service.impl;

import kimp.common.dto.PageRequestDto;
import kimp.community.dao.BoardDao;
import kimp.community.dao.BoardLikeCountDao;
import kimp.community.dao.BoardViewDao;
import kimp.community.dto.board.request.CreateBoardRequestDto;
import kimp.community.dto.board.request.UpdateBoardRequestDto;
import kimp.community.dto.board.response.AllBoardResponseDto;
import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.entity.Board;
import kimp.community.entity.BoardLikeCount;
import kimp.community.entity.BoardViews;
import kimp.community.entity.Category;
import kimp.community.service.BoardService;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kimp.community.entity.QBoard.board;


@Service
public class BoardServiceImpl implements BoardService {
    private final BoardDao boardDao;
    private final BoardViewDao boardViewDao;
    private final BoardLikeCountDao boardLikeCountDao;


    public BoardServiceImpl(BoardDao boardDao, BoardViewDao boardViewDao, BoardLikeCountDao boardLikeCountDao) {
        this.boardDao = boardDao;
        this.boardViewDao = boardViewDao;
        this.boardLikeCountDao = boardLikeCountDao;
    }

    @Override
    public Board createBoard(CreateBoardRequestDto createBoardRequestDto) {

        return boardDao.createBoard(createBoardRequestDto.getTitle(), createBoardRequestDto.getContent());
    }

    @Override
    public Board getBoardById(long id) {
        return boardDao.getBoardById(id);
    }

    @Override
    public Long getBoardsCount() {

        return boardDao.getBoardCount();
    }

    @Override
    @Transactional
    public List<Board> activatePinWithBoard(List<Long> boardIds) {
        if(board == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Board object cannot be null", HttpStatus.BAD_REQUEST, "BoardServiceImpl.activatePinWithBoard");
        }

        List<Board> boards = this.boardDao.findAllByIds(boardIds);

        return this.boardDao.activateBoardsPin(boards);
    }

    @Override
    @Transactional
    public List<Board> deactivatePinWithBoard(List<Long> boardIds) {
        if(board == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Board object cannot be null", HttpStatus.BAD_REQUEST, "BoardServiceImpl.deactivatePinWithBoard");
        }

        List<Board> boards = this.boardDao.findAllByIds(boardIds);

        return this.boardDao.deActivateBoardsPin(boards);
    }

    @Override
    @Transactional
    public Page<Board> getBoardsPageByCategory(Category category, Pageable pageable){
        Page<Board> boardPages = boardDao.findByCategoryWithPage(category, pageable);
        if(boardPages.isEmpty()){
            throw new KimprunException(KimprunExceptionEnum.REQUEST_ACCEPTED, "Not have data", HttpStatus.ACCEPTED, "hello");
        }
        return boardPages;
    }

    @Override
    public Page<Board> getBoardsByPage(PageRequestDto pageRequestDto){
        PageRequest pageRequest = PageRequest.of(pageRequestDto.getPage()-1, pageRequestDto.getSize());
        Page<Board> boardPages = boardDao.findAllWithPage(pageRequest);
        return boardPages;
    }


    @Override
    @Transactional
    public Board updateBoard(Board board, UpdateBoardRequestDto updateBoardRequestDto ){
        if(board == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Board object cannot be null", HttpStatus.BAD_REQUEST, "BoardServiceImpl.updateBoard");
        }
        return boardDao.updateBoard(board, updateBoardRequestDto.getTitle(), updateBoardRequestDto.getContent());
    }

    @Override
    public Boolean deleteBoard(Board board){
        if(board == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Board object cannot be null", HttpStatus.BAD_REQUEST, "BoardServiceImpl.deleteBoard");
        }
        this.boardDao.deleteBoardById(board.getId());
        return true;
    }

    @Override
    public BoardLikeCount createBoardLikeCount(Board board) {
        return boardLikeCountDao.createBoardLikeCount(board);
    }

    @Override
    public BoardViews createBoardViews(Board board) {
        return boardViewDao.createBoardView(board);
    }

    @Override
    public BoardResponseDto convertBoardToBoardResponseDto(Board board) {
        if(board == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Board object cannot be null", HttpStatus.BAD_REQUEST, "BoardServiceImpl.convertBoardToBoardResponseDto");
        }

        return BoardResponseDto.builder()
                .boardId(board.getId())
                .memberId(board.getMember().getId())
                .categoryId(board.getCategory().getId())
                .categoryName(board.getCategory().getCategoryName())
                .memberNickName(board.getMember().getNickname())
                .title(board.getTitle())
                .content(board.getContent())
                .boardViewsCount(board.getViews().getViews())
                .boardLikesCount(board.getBoardLikeCount().getLikes())
                .createdAt(board.getRegistedAt())
                .updatedAt(board.getUpdatedAt())
                .commentsCount(board.getCommentCount().getCounts())
                .isPin(board.isPin())
                .build();
    }

    @Override
    public AllBoardResponseDto convertBoardPagesToAllBoardResponseDtos(Page<Board> boardPages, Long boardCount){
        List<BoardResponseDto> boardResponseDto =  boardPages.stream()
                .map(this::convertBoardToBoardResponseDto)
                .collect(Collectors.toList());
        return AllBoardResponseDto.builder()
                .boards(boardResponseDto)
                .boardCount(boardCount)
                .build();
    }

}
