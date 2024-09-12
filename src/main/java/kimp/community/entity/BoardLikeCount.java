package kimp.community.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "post_like_cnt")
@Getter
public class BoardLikeCount{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private Board board;

    @Column(nullable = false)
    private Integer likes;

    public BoardLikeCount() {
    }

    public BoardLikeCount(Long id, Board board, Integer likes) {
        this.id = id;
        this.board = board;
        this.likes = likes;
    }
}
