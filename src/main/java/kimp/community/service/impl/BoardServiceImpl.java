package kimp.community.service.impl;

import kimp.community.dao.BoardDao;
import kimp.community.dao.BoardLikeCountDao;
import kimp.community.dao.BoardViewDao;
import kimp.community.dto.board.request.CreateBoardRequestDto;
import kimp.community.dto.board.request.UpdateBoardRequestDto;
import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.entity.Board;
import kimp.community.entity.BoardLikeCount;
import kimp.community.entity.BoardViews;
import kimp.community.entity.Category;
import kimp.community.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


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
    @Transactional
    public Page<Board> getBoardsPageByCategory(Category category, Pageable pageable){
        Page<Board> boardPages = boardDao.findByCategoryWithPage(category, pageable);
        return boardPages;
    }

    @Override
    public Page<Board> getBoardsByPage(int page){
        PageRequest pageRequest = PageRequest.of(page, 15);
        Page<Board> boardPages = boardDao.findAllWithPage(pageRequest);

        return boardPages;
    }


    @Override
    public Board updateBoard(Board board, UpdateBoardRequestDto updateBoardRequestDto ){
        if(board == null){
            throw new IllegalArgumentException("board is null");
        }
        return boardDao.updateBoard(board, updateBoardRequestDto.getTitle(), updateBoardRequestDto.getContent());
    }

    @Override
    public Boolean deleteBoard(Board board){
        if(board == null){
            throw new IllegalArgumentException("board is null");
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
            throw new IllegalArgumentException("board object is null");
        }

        return new BoardResponseDto(board.getId(), board.getMember().getId(), board.getMember().getNickname(), board.getTitle(), board.getContent(), board.getViews().getViews(), board.getBoardLikeCount().getLikes(),board.getRegistedAt(), board.getUpdatedAt(), board.getCommentCount().getCounts());
    }

    @Override
    public List<BoardResponseDto> convertBoardPagesToBoardResponseDtos(Page<Board> boardPages){
        return boardPages.stream()
                .map(this::convertBoardToBoardResponseDto)
                .collect(Collectors.toList());
    }
}
