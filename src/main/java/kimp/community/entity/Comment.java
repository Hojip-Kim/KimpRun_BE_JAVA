package kimp.community.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.user.entity.User;
import lombok.Getter;

@Entity
@Getter
@Table(name = "board_comment")
public class Comment extends TimeStamp {


    @Column(nullable = false)
    public String content;

    @Column(nullable = false)
    private long parentCommentId = 0;

    @Column(nullable = false)
    private int depth = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    public User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "board_id")
    public Board board;

    @OneToOne(fetch = FetchType.LAZY)
    public CommentLikeCount likeCount;

    public Comment() {
    }

    public Comment(User user, Board board, String content, long parentCommentId, int depth) {
        this.content = content;
        this.user = user;
        this.board = board;
        this.parentCommentId = parentCommentId;
        this.depth = depth;
    }

    public Comment setCommentLikeCount(CommentLikeCount commentLikeCount){
        this.likeCount = commentLikeCount;
        return this;
    }

    public Comment updateCommentContent(String content){
        this.content = content;
        return this;
    }
}
