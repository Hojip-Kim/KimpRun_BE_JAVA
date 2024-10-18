package kimp.community.entity;

import jakarta.persistence.*;
import kimp.user.entity.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment_likes")
@Getter
public class CommentLikeCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column
    private Integer likes = 0;

    @OneToMany
    @JoinColumn(name = "user_ids")
    private List<User> users = new ArrayList<>();


    public CommentLikeCount() {
    }

    public CommentLikeCount(Comment comment) {
        this.comment = comment;
    }

    public CommentLikeCount addLikes(User user){
        if(!users.contains(user)) {
            this.likes++;
            this.users.add(user);
        }else{
            throw new IllegalArgumentException("user already liked");
        }

        return this;
    }
}
