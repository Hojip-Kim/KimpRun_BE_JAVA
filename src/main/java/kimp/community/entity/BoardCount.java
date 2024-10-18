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
    @JoinColumn(name = "category_id")
    public Category category;

    @Column
    private Integer counts = 0;

    public BoardCount() {
    }

    public BoardCount(Category category) {
        this.category = category;
    }

    public void viewCounts(){
        this.counts++;
    }
}
