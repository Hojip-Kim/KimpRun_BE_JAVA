package kimp.community.service.impl;

import jakarta.transaction.Transactional;
import kimp.community.dao.CommentCountDao;
import kimp.community.dao.CommentDao;
import kimp.community.dao.CommentLikeCountDao;
import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.dto.comment.request.RequestUpdateCommentDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.community.entity.CommentCount;
import kimp.community.entity.CommentLikeCount;
import kimp.community.service.CommentService;
import kimp.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final CommentLikeCountDao commentLikeCountDao;
    private final CommentCountDao commentCountDao;

    public CommentServiceImpl(CommentDao commentDao, CommentLikeCountDao commentLikeCountDao, CommentCountDao commentCountDao) {
        this.commentDao = commentDao;
        this.commentLikeCountDao = commentLikeCountDao;
        this.commentCountDao = commentCountDao;
    }

    @Override
    public Page<Comment> getCommentByBoardId(long boardId, int page) {

        PageRequest pageRequest = PageRequest.of(page, 30);

        Page<Comment> comments = commentDao.getComments(boardId, pageRequest);


        return comments;
    }

    @Override
    public Comment getCommentById(long commentId) {
        return commentDao.getComment(commentId);
    }

    @Override
    public Comment createComment(User user, Board board, RequestCreateCommentDto createCommentDto) {
        if(user == null){
            throw new IllegalArgumentException("user must not be null");
        }
        if(board == null){
            throw new IllegalArgumentException("board must not be null");
        }

        return commentDao.createComment(user, board, createCommentDto.getContent(), createCommentDto.getParentCommentId(), createCommentDto.getDepth());
    }

    @Override
    public CommentCount createCommentCount(Board board){
        return commentCountDao.createCommentCount(board);
    }

    @Override
    @Transactional
    public Comment updateComment(long userId, RequestUpdateCommentDto updateCommentDto) {

        Comment comment = commentDao.getComment(updateCommentDto.getCommentId());
        if(!comment.user.getId().equals(userId)){
            throw new IllegalArgumentException("comment id mismatch");
        }

        return comment.updateCommentContent(updateCommentDto.getContent());
    }

    @Override
    public Boolean deleteComment(long userId, long commentId) {
        Comment comment = commentDao.getComment(commentId);
        if(!comment.user.getId().equals(userId)){
            throw new IllegalArgumentException("comment id mismatch");
        }

        return commentDao.deleteComment(commentId);
    }

    @Override
    public CommentLikeCount createCommentLikeCount(Comment comment) {

        return commentLikeCountDao.createCommentLikeCount(comment);
    }

    @Override
    public ResponseCommentDto convertCommentToResponseDto(Comment comment) {
        long commentId = comment.getId();
        long parentCommentId = comment.getParentCommentId();
        String content = comment.getContent();
        int depth = comment.getDepth();
        String userLoginId = comment.getUser().getLoginId();
        String userNickName = comment.getUser().getNickname();
        return new ResponseCommentDto(commentId, parentCommentId, content, depth, userLoginId, userNickName);
    }

    @Override
    public List<ResponseCommentDto> converCommentsToResponseDtoList(List<Comment> comments){
        return comments.stream().map(comment->convertCommentToResponseDto(comment)).collect(Collectors.toList());
    }


}
