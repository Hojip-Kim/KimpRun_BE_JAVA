package kimp.community.service;

import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.dto.comment.request.RequestUpdateCommentDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.community.entity.CommentCount;
import kimp.community.entity.CommentLikeCount;
import kimp.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {

    // comment 관련
    public Page<Comment> getCommentByBoard(Board board, int page);
    
    // Board와 함께 가져올 때 사용 (삭제된 댓글도 포함)
    public Page<Comment> getCommentByBoardWithDeleted(Board board, int page);

    public Comment getCommentById(long commentId);

    public Comment createComment(Member member, Board board, RequestCreateCommentDto createCommentDto);

    public CommentLikeCount createCommentLikeCount(Comment comment);

    public CommentCount createCommentCount(Board board);

    public Comment updateComment(long memberId, RequestUpdateCommentDto updateCommentDto);

    public Boolean deleteComment(long memberId, long commentId);

    public ResponseCommentDto convertCommentToResponseDto(Comment comment);

    public List<ResponseCommentDto> converCommentsToResponseDtoList(List<Comment> comments);
    
    // 특정 멤버의 댓글 조회
    public Page<Comment> getCommentsByMember(Member member, Pageable pageable);
    
    // 최적화된 멤버별 댓글 조회 (N+1 문제 해결)
    public Page<Comment> getCommentsByMemberIdWithAllFetch(Long memberId, Pageable pageable);
    
    // soft delete
    public void softDeleteComment(long memberId, long commentId);

    // DTO 반환 메소드들 (Controller용)
    public ResponseCommentDto updateCommentDto(long memberId, RequestUpdateCommentDto updateCommentDto);

}
