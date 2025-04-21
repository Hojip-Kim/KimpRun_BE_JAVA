package kimp.community.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.user.entity.Member;
import lombok.Getter;

@Entity
@Getter
@Table(name = "board_comment")
public class Comment extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    public String content;

    @Column(nullable = false)
    private long parentCommentId = 0;

    @Column(nullable = false)
    private int depth = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="member_id")
    public Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "board_id")
    public Board board;

    @OneToOne(fetch = FetchType.LAZY)
    public CommentLikeCount likeCount;

    public Comment() {
    }

    public Comment(Member member, Board board, String content, long parentCommentId, int depth) {
        this.content = content;
        this.member = member;
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
