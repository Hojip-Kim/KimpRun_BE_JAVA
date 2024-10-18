package kimp.community.entity;

import jakarta.persistence.*;
import kimp.user.entity.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board_like_cnt")
@Getter
public class BoardLikeCount{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false)
    private Integer likes = 0;

    @OneToMany
    @JoinColumn(name = "user_ids")
    private List<User> users = new ArrayList<>();

    public BoardLikeCount() {
    }

    public BoardLikeCount(Board board) {
        this.board = board;
    }

    public BoardLikeCount addLikes(User user){
        if(!users.contains(user)) {
            this.likes++;
            this.users.add(user);
        }else{
            throw new IllegalArgumentException("user already like board");
        }

        return this;
    }
}
