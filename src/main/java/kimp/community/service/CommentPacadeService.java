package kimp.community.service;

import jakarta.transaction.Transactional;
import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.community.entity.CommentLikeCount;
import kimp.user.entity.User;
import kimp.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


@Service
public class CommentPacadeService {

    private final UserService userService;
    private final CommentService commentService;
    private final BoardService boardService;

    public CommentPacadeService(UserService userService, CommentService commentService, BoardService boardService) {
        this.userService = userService;
        this.commentService = commentService;
        this.boardService = boardService;
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

    @Transactional
    public Boolean commentLikeById(long commentId, long userId) {
        User user = userService.getUserById(userId);
        Comment comment = commentService.getCommentById(commentId);
        CommentLikeCount commentLikeCount = comment.getLikeCount();
        int beforeLikeCount = commentLikeCount.getLikes();
        commentLikeCount.addLikes(user);
        int prevLikeCount = commentLikeCount.getLikes();
        if(beforeLikeCount + 1 == prevLikeCount) {
            return true;
        }
        return false;
    }

}
