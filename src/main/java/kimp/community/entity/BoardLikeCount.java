package kimp.community.entity;

import jakarta.persistence.*;
import kimp.user.entity.Member;
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
    @JoinColumn(name = "board_like")
    private List<Member> members = new ArrayList<>();

    public BoardLikeCount() {
    }

    public BoardLikeCount(Board board) {
        this.board = board;
    }

    public BoardLikeCount addLikes(Member member){
        if(!members.contains(member)) {
            this.likes++;
            this.members.add(member);
        }else{
            throw new IllegalArgumentException("member already like board");
        }

        return this;
    }
}
