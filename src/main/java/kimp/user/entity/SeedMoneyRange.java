package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;

@Entity
@Table(name = "seed_money_range")
@Getter
public class SeedMoneyRange extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="seed_range_key",unique = true, nullable = false)
    private String seedRangeKey;

    @Column(nullable = false)
    private String range;

    @Column(nullable = false)
    private String rank;

    public SeedMoneyRange() {
    }

    public SeedMoneyRange(String seedRangeKey, String range, String rank) {
        this.seedRangeKey = seedRangeKey;
        this.range = range;
        this.rank = rank;
    }

    public SeedMoneyRange updateRange(String range) {
        this.range = range;
        return this;
    }

    public SeedMoneyRange updateRank(String rank) {
        this.rank = rank;
        return this;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
