package kimp.community.controller;

import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.dto.comment.request.RequestUpdateCommentDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.entity.Comment;
import kimp.community.service.BoardPacadeService;
import kimp.community.service.CommentPacadeService;
import kimp.community.service.CommentService;
import kimp.exception.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import kimp.security.user.CustomUserDetails;
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
    public ApiResponse<List<ResponseCommentDto>> getComment(@RequestParam("boardId") Long boardId, @RequestParam("page") int page) {
        Page<Comment> comments = boardPacadeService.getComments(boardId, page);
        List<ResponseCommentDto> result = commentService.converCommentsToResponseDtoList(comments.getContent());
        return ApiResponse.success(result);
    }

    @PostMapping("/{boardId}/create")
    public ApiResponse<ResponseCommentDto> createComment(@AuthenticationPrincipal UserDetails UserDetails, @PathVariable Long boardId, @RequestBody RequestCreateCommentDto requestCreateCommentDto){
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        long memberId = customUserDetails.getId();

        Comment comment = boardPacadeService.createComment(memberId, boardId, requestCreateCommentDto);
        ResponseCommentDto result = commentService.convertCommentToResponseDto(comment);
        return ApiResponse.success(result);
    }

    @PatchMapping("/update")
    public ApiResponse<ResponseCommentDto> updateComment(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody RequestUpdateCommentDto requestUpdateCommentDto){
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;
        long memberId = customUserDetails.getId();
        Comment comment = commentService.updateComment(memberId, requestUpdateCommentDto);
        ResponseCommentDto result = commentService.convertCommentToResponseDto(comment);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteComment(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody long commentId){
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;
        long memberId = customUserDetails.getId();
        Boolean isDeleted = commentService.deleteComment(memberId, commentId);
        return ApiResponse.success(isDeleted);
    }

    @PatchMapping("/like")
    public ApiResponse<Boolean> likeComment(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody long commentId){
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;
        long memberId = customUserDetails.getId();

        Boolean isCompleted = commentPacadeService.commentLikeById(memberId, commentId);
        return ApiResponse.success(isCompleted);
    }



}
