package kimp.community.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name="comments_count")
@Getter
public class CommentCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column
    private Integer counts = 0;

    public CommentCount() {
    }

    public CommentCount(Board board) {
        this.board = board;
    }

    public void addCount(){
        this.counts++;
    }
}
