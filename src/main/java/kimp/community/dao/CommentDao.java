package kimp.community.dao;

import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CommentDao {

    public Comment getComment(long commentId);

    public Comment createComment(Member member, Board board, String content, long parentCommentId, int depth);

    public Page<Comment> getComments(Board board, Pageable pageable);

    public boolean deleteComment(long commentId);
    
    // 특정 멤버의 댓글 조회
    public Page<Comment> getCommentsByMember(Member member, Pageable pageable);
    
    // 멤버별 댓글 조회 (N+1 문제 해결)
    public Page<Comment> getCommentsByMemberIdWithAllFetch(Long memberId, Pageable pageable);
    
    // save comment
    public Comment saveComment(Comment comment);

}
