package kimp.community.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "comment_likes")
@Getter
public class CommentLikeCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Comment comment;

    @Column
    private Integer likes;

    public CommentLikeCount() {
    }

    public CommentLikeCount(Long id, Comment comment, Integer likes) {
        this.id = id;
        this.comment = comment;
        this.likes = likes;
    }
}
