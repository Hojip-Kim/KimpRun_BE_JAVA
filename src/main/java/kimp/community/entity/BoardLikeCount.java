package kimp.community.entity;

import jakarta.persistence.*;
import lombok.Getter;

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

    public BoardLikeCount() {
    }

    public BoardLikeCount(Board board) {
        this.board = board;
    }

    public void incrementLike() {
        this.likes++;
    }

    public void decrementLike() {
        if (this.likes > 0) {
            this.likes--;
        }
    }
}
