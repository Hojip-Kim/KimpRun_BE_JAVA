package kimp.community.dao.impl;

import kimp.community.dao.CommentCountDao;
import kimp.community.entity.Board;
import kimp.community.entity.CommentCount;
import kimp.community.repository.CommentCountRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CommentCountDaoImpl implements CommentCountDao {

    private final CommentCountRepository commentCountRepository;

    public CommentCountDaoImpl(CommentCountRepository commentCountRepository) {
        this.commentCountRepository = commentCountRepository;
    }

    @Override
    public CommentCount createCommentCount(Board board){
        CommentCount findCommentCount = commentCountRepository.findByBoard(board);
        if(findCommentCount != null){
            throw new RuntimeException("Comment count already exists");
        }
        CommentCount commentCount = new CommentCount(board);
        return commentCountRepository.save(commentCount);
    }

}
