package kimp.community.entity;

import jakarta.persistence.*;
import kimp.user.entity.Member;
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
    @JoinTable(
            name = "board_like_members",
            joinColumns = @JoinColumn(name = "board_like_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<Member> members = new ArrayList<>();


    public CommentLikeCount() {
    }

    public CommentLikeCount(Comment comment) {
        this.comment = comment;
    }

    public CommentLikeCount addLikes(Member member){
        if(!members.contains(member)) {
            this.likes++;
            this.members.add(member);
        }else{
            throw new IllegalArgumentException("member already liked");
        }

        return this;
    }
}
