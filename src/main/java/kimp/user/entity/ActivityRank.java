package kimp.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "activity_rank")
@Getter
public class ActivityRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String rank_key;

    @Column
    private String grade;

    public ActivityRank() {
    }

    public ActivityRank(Long id, String rank_key, String grade) {
        this.id = id;
        this.rank_key = rank_key;
        this.grade = grade;
    }
}
