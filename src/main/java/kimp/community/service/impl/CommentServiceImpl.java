package kimp.community.service.impl;

import kimp.community.entity.Comment;
import kimp.community.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Override
    public List<Comment> getCommentByBoardId(int id) {
        return List.of();
    }
}
