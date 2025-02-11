package kimp.community.service;

import kimp.community.dto.board.request.CreateBoardRequestDto;
import kimp.community.dto.board.request.UpdateBoardRequestDto;
import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.dto.board.response.BoardWithCommentResponseDto;
import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.entity.*;
import kimp.user.entity.Member;
import kimp.user.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardPacadeService {

    private final BoardService boardService;
    private final MemberService memberService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final CommentPacadeService commentPacadeService;

    public BoardPacadeService(BoardService boardService, MemberService memberService, CategoryService categoryService, CommentService commentService, CommentPacadeService commentPacadeService) {
        this.boardService = boardService;
        this.memberService = memberService;
        this.categoryService = categoryService;
        this.commentService = commentService;
        this.commentPacadeService = commentPacadeService;
    }

    @Transactional
    public Board createBoard(Long memberId, Long categoryId, CreateBoardRequestDto createBoardRequestDto) {

        Member member = memberService.getmemberById(memberId);

        Category category = categoryService.getCategoryByID(categoryId);

        Board board = boardService.createBoard(createBoardRequestDto);
        BoardViews boardViews = boardService.createBoardViews(board);
        BoardLikeCount boardLikeCount = boardService.createBoardLikeCount(board);
        CommentCount commentCount = commentService.createCommentCount(board);

        board.setMember(member).setCategory(category).setViews(boardViews).setBoardLikeCounts(boardLikeCount).setCommentCount(commentCount);

        member.addBoard(board);

        category.getBoardCount().viewCounts();

        return board;
    }

    // 게시글 조회
    // board를 Id로 찾은 후 comment list와 함께 response
    @Transactional
    public BoardWithCommentResponseDto getBoardByIdWithCommentPage(long memberId ,Long requestBoardId, int page) {
        Board board = boardService.getBoardById(requestBoardId);

        if(memberId == -1 || board.getMember().getId() != memberId ) {
            board.getViews().viewCount();
        }
        Page<Comment> comments = commentPacadeService.getComments(requestBoardId, page);

        List<ResponseCommentDto> commentDtos =  commentService.converCommentsToResponseDtoList(comments.getContent());

        return new BoardWithCommentResponseDto(board.getId(), board.getMember().getId(),board.getCategory().getId(),board.getCategory().getCategoryName(), board.getMember().getNickname(), board.getTitle(), board.getContent(), board.getViews().getViews(), board.getBoardLikeCount().getLikes(), board.getRegistedAt(), board.getUpdatedAt(), commentDtos, board.getCommentCount().getCounts());

    }

    public Board updateBoard(Long memberId, Long boardId, UpdateBoardRequestDto updateBoardRequestDto) {
        Board board = boardService.getBoardById(boardId);
        if(!board.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("Not valid member to updateBoard : " + boardId);
        }

        return boardService.updateBoard(board, updateBoardRequestDto);
    }

    public Boolean deleteBoard(Long memberId, Long boardId){
        Board board = boardService.getBoardById(boardId);
        if(!board.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("Not valid member to deleteBoard : " + boardId);
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
        return new BoardResponseDto(board.getId(), board.getMember().getId(),board.getCategory().getId(),board.getCategory().getCategoryName(), board.getMember().getNickname(), board.getTitle(), board.getContent(),board.getViews().getViews(), board.getBoardLikeCount().getLikes(), board.getRegistedAt(), board.getUpdatedAt(), board.getCommentCount().getCounts());
    }

    public String summaryContent(String content) {
        if (content.length() > 100) {
            return content.substring(0, 100) + "...";
        }
        return content;
    }



    public BoardResponseDto convertBoardToBoardResponseDtoWithSummaryContent(Board board){

        String summaryContent = summaryContent(board.getContent());

        return new BoardResponseDto(board.getId(), board.getMember().getId(),board.getCategory().getId(),board.getCategory().getCategoryName(), board.getMember().getNickname(), board.getTitle(), summaryContent, board.getViews().getViews(), board.getBoardLikeCount().getLikes(), board.getRegistedAt(), board.getUpdatedAt(), board.getCommentCount().getCounts());
    }

    public List<BoardResponseDto> convertBoardsToBoardResponseDtos(Page<Board> boards){
        return boards.stream()
                .map(board -> convertBoardToBoardResponseDtoWithSummaryContent(board))
                .collect(Collectors.toList());
    }

    public Integer getBoardCountByCategoryId(long categoryId){
        Category category = this.categoryService.getCategoryByID(categoryId);
        return category.getBoardCount().getCounts();
    }


    @Transactional
    public Comment createComment(long memberId, long boardId, RequestCreateCommentDto requestCreateCommentDto) {
        Board board = boardService.getBoardById(boardId);
        Member member = memberService.getmemberById(memberId);
        Comment comment = commentService.createComment(member, board, requestCreateCommentDto);
        CommentLikeCount commentLikeCount = commentService.createCommentLikeCount(comment);
        comment.setCommentLikeCount(commentLikeCount);
        member.addComment(comment);
        board.addComment(comment);
        board.getCommentCount().addCount();

        return comment;
    }

    public Page<Comment> getComments(long boardId, int page) {
        Board board = boardService.getBoardById(boardId);
        Page<Comment> comments = commentService.getCommentByBoard(board, page);

        return comments;

    }

    @Transactional
    public Boolean likeBoardById(Long boardId, Long memberId) {
        try {
            Board board = boardService.getBoardById(boardId);
            Member member = memberService.getmemberById(memberId);
            BoardLikeCount boardLikeCount = board.getBoardLikeCount();
            int beforeBoardLike = boardLikeCount.getLikes();
            boardLikeCount.addLikes(member);
            int prevBoardLike = boardLikeCount.getLikes();

            if (beforeBoardLike + 1 == prevBoardLike) {
                return true;
            }
        }catch(Exception e){
            return false;
        }
        return false;
    }
}
