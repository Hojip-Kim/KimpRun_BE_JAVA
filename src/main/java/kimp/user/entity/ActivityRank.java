package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity_rank")
@Getter
@NoArgsConstructor
public class ActivityRank extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="rank_key",unique = true, nullable = false)
    private String rankKey;

    @Column(nullable = false)
    private String grade;

    public ActivityRank(String rankKey, String grade) {
        this.rankKey = rankKey;
        this.grade = grade;
    }

    public ActivityRank updateGrade(String grade) {
        this.grade = grade;
        return this;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
