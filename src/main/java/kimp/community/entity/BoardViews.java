package kimp.community.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "board_views")
@Getter
public class BoardViews{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Board board;

    @Column
    private Integer views;

    public BoardViews() {
    }

    public BoardViews(Long id, Board board, Integer views) {
        this.id = id;
        this.board = board;
        this.views = views;
    }
}
