package kimp.community.entity;

import jakarta.persistence.*;

@Entity
@Table(name="comments_count")
public class CommentCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Board board;

    @Column
    private Integer counts;

    public CommentCount() {
    }

    public CommentCount(Long id, Board board, Integer counts) {
        this.id = id;
        this.board = board;
        this.counts = counts;
    }
}
