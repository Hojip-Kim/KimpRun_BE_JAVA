package kimp.community.service;

import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.dto.comment.request.RequestUpdateCommentDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.community.entity.CommentCount;
import kimp.community.entity.CommentLikeCount;
import kimp.user.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {

    // comment 관련
    public Page<Comment> getCommentByBoardId(long id, int page);

    public Comment getCommentById(long commentId);

    public Comment createComment(User user, Board board, RequestCreateCommentDto createCommentDto);

    public CommentLikeCount createCommentLikeCount(Comment comment);

    public CommentCount createCommentCount(Board board);

    public Comment updateComment(long userId, RequestUpdateCommentDto updateCommentDto);

    public Boolean deleteComment(long userId, long commentId);

    public ResponseCommentDto convertCommentToResponseDto(Comment comment);

    public List<ResponseCommentDto> converCommentsToResponseDtoList(List<Comment> comments);

}
