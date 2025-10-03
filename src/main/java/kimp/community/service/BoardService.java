package kimp.community.service;

import kimp.common.dto.request.PageRequestDto;
import kimp.community.dto.board.request.CreateBoardRequestDto;
import kimp.community.dto.board.request.UpdateBoardRequestDto;
import kimp.community.dto.board.response.AllBoardResponseDto;
import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface BoardService {

    // 전체 카테고리에서의 게시글 페이지
    public Page<Board> getBoardsByPage(PageRequestDto pageRequestDto);

    // 특정 카테고리에서의 게시글 페이지
    public Page<Board> getBoardsPageByCategory(Category category, Pageable pageable);

    public Board getBoardById(long id);

    public Long getBoardsCount();

    public Board createBoard(CreateBoardRequestDto createBoardRequestDto);

    public Board updateBoard(Board board, UpdateBoardRequestDto updateBoardRequestDto );

    public Boolean deleteBoard(Board board);

    public BoardLikeCount createBoardLikeCount(Board board);

    public BoardViews createBoardViews(Board board);

    public BoardResponseDto convertBoardToBoardResponseDto(Board board);

    public AllBoardResponseDto convertBoardPagesToAllBoardResponseDtos(Page<Board> boardPages, Long boardCount);

    public List<Board> activatePinWithBoard(List<Long> boardIds);

    public List<Board> deactivatePinWithBoard(List<Long> boardIds);

}
