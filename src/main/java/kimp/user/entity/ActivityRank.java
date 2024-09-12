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

    @Column(name="rank_key",unique = true, nullable = false)
    private String rankKey;

    @Column
    private String grade;

    public ActivityRank() {
    }

    public ActivityRank(Long id, String rankKey, String grade) {
        this.id = id;
        this.rankKey = rankKey;
        this.grade = grade;
    }
}
