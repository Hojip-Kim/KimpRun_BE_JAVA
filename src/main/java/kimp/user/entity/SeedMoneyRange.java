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

    @Column(name="seed_range_key",unique = true, nullable = false)
    private String seedRangeKey;

    @Column
    private String range;

    @Column
    private String rank;

    public SeedMoneyRange() {
    }

    public SeedMoneyRange(Long id, String seedRangeKey, String range, String rank) {
        this.id = id;
        this.seedRangeKey = seedRangeKey;
        this.range = range;
        this.rank = rank;
    }
}
