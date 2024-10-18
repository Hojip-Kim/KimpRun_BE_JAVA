package kimp.community.service;

import jakarta.transaction.Transactional;
import kimp.community.dto.board.request.CreateBoardRequestDto;
import kimp.community.dto.board.request.UpdateBoardRequestDto;
import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.dto.board.response.BoardWithCommentResponseDto;
import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.entity.*;
import kimp.user.entity.User;
import kimp.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardPacadeService {

    private final BoardService boardService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final CommentPacadeService commentPacadeService;

    public BoardPacadeService(BoardService boardService, UserService userService, CategoryService categoryService, CommentService commentService, CommentPacadeService commentPacadeService) {
        this.boardService = boardService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.commentService = commentService;
        this.commentPacadeService = commentPacadeService;
    }

    @Transactional
    public Board createBoard(Long userId, Long categoryId, CreateBoardRequestDto createBoardRequestDto) {

        User user = userService.getUserById(userId);

        Category category = categoryService.getCategoryByID(categoryId);

        Board board = boardService.createBoard(createBoardRequestDto);
        BoardViews boardViews = boardService.createBoardViews(board);
        BoardLikeCount boardLikeCount = boardService.createBoardLikeCount(board);
        CommentCount commentCount = commentService.createCommentCount(board);

        board.setUser(user).setCategory(category).setViews(boardViews).setBoardLikeCounts(boardLikeCount).setCommentCount(commentCount);

        user.addBoard(board);

        category.getBoardCount().viewCounts();

        return board;
    }

    // 게시글 조회
    // board를 Id로 찾은 후 comment list와 함께 response
    @Transactional
    public BoardWithCommentResponseDto getBoardByIdWithCommentPage(Long requestBoardId, int page) {
        Board board = boardService.getBoardById(requestBoardId);
        board.getViews().viewCount();
        Page<Comment> comments = commentPacadeService.getComments(requestBoardId, page);

        List<ResponseCommentDto> commentDtos =  commentService.converCommentsToResponseDtoList(comments.getContent());

        return new BoardWithCommentResponseDto(board.getId(), board.getUser().getId(), board.getUser().getNickname(), board.getTitle(), board.getContent(), board.getViews().getViews(), board.getBoardLikeCount().getLikes(), board.getRegisted_at(), board.getUpdated_at(), commentDtos);

    }

    public Board updateBoard(Long userId, Long boardId, UpdateBoardRequestDto updateBoardRequestDto) {
        Board board = boardService.getBoardById(boardId);
        if(!board.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not valid user to updateBoard : " + boardId);
        }

        return boardService.updateBoard(board, updateBoardRequestDto);
    }

    public Boolean deleteBoard(Long userId, Long boardId){
        Board board = boardService.getBoardById(boardId);
        if(!board.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not valid user to deleteBoard : " + boardId);
        }

        return boardService.deleteBoard(board);
    }


    // 15개씩 카테고리에 대한 게시글 불러오기
    public Page<Board> getBoardPageWithCategoryId(Long categoryId, Integer page) {
        Category category = categoryService.getCategoryByID(categoryId);

        PageRequest pageRequest = PageRequest.of(page, 15);

        Page<Board> boardPages = boardService.getBoardsPageByCategory(category, pageRequest);

        return boardPages;
    }

    public BoardResponseDto convertBoardToBoardResponseDto(Board board){
        return new BoardResponseDto(board.getId(), board.getUser().getId(), board.getUser().getNickname(), board.getTitle(), board.getContent(),board.getViews().getViews(), board.getBoardLikeCount().getLikes(), board.getRegisted_at(), board.getUpdated_at());
    }

    public List<BoardResponseDto> convertBoardsToBoardResponseDtos(Page<Board> boards){
        return boards.stream()
                .map(board -> convertBoardToBoardResponseDto(board))
                .collect(Collectors.toList());
    }


    @Transactional
    public Comment createComment(long userId, long boardId, RequestCreateCommentDto requestCreateCommentDto) {
        Board board = boardService.getBoardById(boardId);
        User user = userService.getUserById(userId);
        Comment comment = commentService.createComment(user, board, requestCreateCommentDto);
        CommentLikeCount commentLikeCount = commentService.createCommentLikeCount(comment);
        comment.setCommentLikeCount(commentLikeCount);
        user.addComment(comment);
        board.addComment(comment);
        board.getCommentCount().addCount();

        return comment;
    }

    public Page<Comment> getComments(long boardId, int page) {
        Page<Comment> comments = commentService.getCommentByBoardId(boardId, page);

        return comments;

    }

    public Boolean likeBoardById(Long boardId, Long userId) {
        Board board = boardService.getBoardById(boardId);
        User user = userService.getUserById(userId);
        BoardLikeCount boardLikeCount = board.getBoardLikeCount();
        int beforeBoardLike = boardLikeCount.getLikes();
        boardLikeCount.addLikes(user);
        int prevBoardLike = boardLikeCount.getLikes();
        if(beforeBoardLike + 1 == prevBoardLike){
            return true;
        }

        return false;
    }
}
