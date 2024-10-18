package kimp.community.dao.impl;

import kimp.community.dao.CommentLikeCountDao;
import kimp.community.entity.Comment;
import kimp.community.entity.CommentLikeCount;
import kimp.community.repository.CommentLikeCountRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CommentLikeCountDaoImpl implements CommentLikeCountDao {

    private final CommentLikeCountRepository commentLikeCountRepository;

    public CommentLikeCountDaoImpl(CommentLikeCountRepository commentLikeCountRepository) {
        this.commentLikeCountRepository = commentLikeCountRepository;
    }

    @Override
    public CommentLikeCount createCommentLikeCount(Comment comment){
        CommentLikeCount findCommentLikeCount = commentLikeCountRepository.findByComment(comment);
        if(findCommentLikeCount != null){
            throw new RuntimeException("CommentLikeCount already exists");
        }
        CommentLikeCount commentLikeCount = new CommentLikeCount(comment);

        return commentLikeCountRepository.save(commentLikeCount);
    }
}
