package kimp.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "seed_money_range")
@Getter
public class SeedMoneyRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String seed_range_key;

    @Column
    private String range;

    @Column
    private String rank;

    public SeedMoneyRange() {
    }

    public SeedMoneyRange(Long id, String seed_range_key, String range, String rank) {
        this.id = id;
        this.seed_range_key = seed_range_key;
        this.range = range;
        this.rank = rank;
    }
}
