package kimp.community.controller;

import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.dto.comment.request.RequestUpdateCommentDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.entity.Comment;
import kimp.community.service.BoardPacadeService;
import kimp.community.service.CommentPacadeService;
import kimp.community.service.CommentService;
import kimp.security.user.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final BoardPacadeService boardPacadeService;
    private final CommentPacadeService commentPacadeService;

    public CommentController(CommentService commentService, BoardPacadeService boardPacadeService, CommentPacadeService commentPacadeService) {
        this.commentService = commentService;
        this.boardPacadeService = boardPacadeService;
        this.commentPacadeService = commentPacadeService;
    }

    @GetMapping
    public List<ResponseCommentDto> getComment(@RequestParam("boardId") Long boardId, @RequestParam("page") int page) {
        Page<Comment> comments = boardPacadeService.getComments(boardId, page);

        return commentService.converCommentsToResponseDtoList(comments.getContent());
    }

    @PostMapping("/{boardId}/create")
    public ResponseCommentDto createComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long boardId, @RequestBody RequestCreateCommentDto requestCreateCommentDto){
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        long userId = customUserDetails.getId();

        Comment comment = boardPacadeService.createComment(userId, boardId, requestCreateCommentDto);

        return commentService.convertCommentToResponseDto(comment);
    }

    @PatchMapping("/update")
    public ResponseCommentDto updateComment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody RequestUpdateCommentDto requestUpdateCommentDto){
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        long userId = customUserDetails.getId();
        Comment comment = commentService.updateComment(userId, requestUpdateCommentDto);
        return commentService.convertCommentToResponseDto(comment);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteComment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody long commentId){
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        long userId = customUserDetails.getId();
        Boolean isDeleted = commentService.deleteComment(userId, commentId);

        return ResponseEntity.ok(isDeleted);
    }

    @PatchMapping("/like")
    public ResponseEntity<Boolean> likeComment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody long commentId){
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        long userId = customUserDetails.getId();

        Boolean isCompleted = commentPacadeService.commentLikeById(userId, commentId);

        return ResponseEntity.ok(isCompleted);
    }



}
