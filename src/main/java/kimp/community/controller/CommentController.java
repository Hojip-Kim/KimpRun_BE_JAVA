package kimp.community.controller;

import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.dto.comment.request.RequestUpdateCommentDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.service.BoardPacadeService;
import kimp.community.service.CommentPacadeService;
import kimp.community.service.CommentService;
import kimp.community.vo.*;
import kimp.exception.response.ApiResponse;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
        GetCommentsVo vo = new GetCommentsVo(boardId, page);
        Page<ResponseCommentDto> commentDtoPage = boardPacadeService.getCommentsDto(vo);
        List<ResponseCommentDto> result = commentDtoPage.getContent();
        return ApiResponse.success(result);
    }

    @PostMapping("/{boardId}/create")
    public ApiResponse<ResponseCommentDto> createComment(@AuthenticationPrincipal UserDetails UserDetails, @PathVariable Long boardId, @RequestBody RequestCreateCommentDto requestCreateCommentDto){
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        long memberId = customUserDetails.getId();

        CreateCommentVo vo = new CreateCommentVo(memberId, boardId, requestCreateCommentDto);
        ResponseCommentDto result = boardPacadeService.createCommentDto(vo);
        return ApiResponse.success(result);
    }

    @PatchMapping("/update")
    public ApiResponse<ResponseCommentDto> updateComment(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody RequestUpdateCommentDto requestUpdateCommentDto){
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;
        long memberId = customUserDetails.getId();
        ResponseCommentDto result = commentService.updateCommentDto(memberId, requestUpdateCommentDto);
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

    @DeleteMapping("/{commentId}/soft")
    public ApiResponse<Void> softDeleteComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable long commentId) {
        if (commentId < 0) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_ID_PARAMETER_EXCEPTION, 
                "Comment ID must be greater than or equal to 0", HttpStatus.BAD_REQUEST, "CommentController.softDeleteComment");
        }
        
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        commentService.softDeleteComment(customUserDetails.getId(), commentId);
        return ApiResponse.success(null);
    }

}
