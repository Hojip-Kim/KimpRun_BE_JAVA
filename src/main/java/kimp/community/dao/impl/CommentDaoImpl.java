package kimp.community.dao.impl;

import jakarta.persistence.EntityManager;
import kimp.community.dao.CommentDao;
import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.community.repository.CommentRepository;
import kimp.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class CommentDaoImpl implements CommentDao {
    private final CommentRepository commentRepository;

    private final EntityManager entityManager;

    public CommentDaoImpl(CommentRepository commentRepository, EntityManager entityManager) {
        this.commentRepository = commentRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Comment getComment(long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);

        if(comment.isEmpty()){
            throw new IllegalArgumentException("comment not found");
        }

        return comment.get();
    }

    @Override
    public Comment createComment(User user, Board board, String content, long parentCommentId, int depth) {

        Comment comment = new Comment(user, board, content, parentCommentId, depth);

        return commentRepository.save(comment);
    }

    @Override
    public Page<Comment> getComments(long boardId, Pageable pageable) {
        return this.commentRepository.findByBoardId(boardId, pageable);
    }

    @Override
    public boolean deleteComment(long commentId) {
        Comment comment = getComment(commentId);
        commentRepository.delete(comment);

        return !entityManager.contains(comment);
    }

}
