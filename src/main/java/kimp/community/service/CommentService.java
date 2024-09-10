package kimp.community.service;

import kimp.community.entity.Comment;

import java.util.List;

public interface CommentService {

    public List<Comment> getCommentByBoardId(int id);
}
