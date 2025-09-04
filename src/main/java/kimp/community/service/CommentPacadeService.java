package kimp.community.service;

import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.community.entity.CommentLikeCount;
import kimp.user.entity.Member;
import kimp.user.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class CommentPacadeService {

    private final MemberService memberService;
    private final CommentService commentService;
    private final BoardService boardService;

    public CommentPacadeService(MemberService memberService, CommentService commentService, BoardService boardService) {
        this.memberService = memberService;
        this.commentService = commentService;
        this.boardService = boardService;
    }

    @Transactional
    public Comment createComment(long memberId, long boardId, RequestCreateCommentDto requestCreateCommentDto) {
        Board board = boardService.getBoardById(boardId);
        Member member = memberService.getMemberEntityById(memberId);
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
    public Boolean commentLikeById(long commentId, long memberId) {
        Member member = memberService.getMemberEntityById(memberId);
        Comment comment = commentService.getCommentById(commentId);
        CommentLikeCount commentLikeCount = comment.getLikeCount();
        int beforeLikeCount = commentLikeCount.getLikes();
        commentLikeCount.addLikes(member);
        int prevLikeCount = commentLikeCount.getLikes();
        if(beforeLikeCount + 1 == prevLikeCount) {
            return true;
        }
        return false;
    }

    // DTO 반환 메소드들 (Controller용)
    public Page<ResponseCommentDto> getCommentsDto(long boardId, int page) {
        Page<Comment> comments = getComments(boardId, page);
        List<ResponseCommentDto> commentDtos = commentService.converCommentsToResponseDtoList(comments.getContent());
        PageRequest pageRequest = PageRequest.of(page - 1, 15); // 기본 페이지 사이즈 15
        return new PageImpl<>(commentDtos, pageRequest, comments.getTotalElements());
    }

    @Transactional
    public ResponseCommentDto createCommentDto(long memberId, long boardId, RequestCreateCommentDto requestCreateCommentDto) {
        Comment comment = createComment(memberId, boardId, requestCreateCommentDto);
        return commentService.convertCommentToResponseDto(comment);
    }

}
