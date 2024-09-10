package kimp.community.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "board_counts")
@Getter
public class BoardCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Category category;

    @Column
    private Integer counts;

    public BoardCount() {
    }

    public BoardCount(Long id, Category category, Integer counts) {
        this.id = id;
        this.category = category;
        this.counts = counts;
    }
}
