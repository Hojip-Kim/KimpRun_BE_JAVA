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
    @JoinColumn(name = "board_id")
    private Board board;

    @Column
    private Integer views = 0;

    public BoardViews() {
    }

    public BoardViews(Board board) {
        this.board = board;
    }

    private void viewCount(){
        this.views++;
    }
}
