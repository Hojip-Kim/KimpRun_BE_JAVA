package kimp.cmc.entity.coin;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="cmc_rank",
        indexes = {
                @Index(name = "idx_cmc_rank_coin_id", columnList = "cmc_coin_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = "cmc_coin_id")
)
@NoArgsConstructor
@Getter
public class CmcRank extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name="cmc_coin_id")
    private Long cmcCoinId;

    @Column(nullable = false, name="rank")
    private Long rank;

    public CmcRank(Long cmcCoinId, Long rank) {
        this.cmcCoinId = cmcCoinId;
        this.rank = rank;
    }

    public CmcRank setCmcCoinId(Long cmcCoinId) {
        if( cmcCoinId != null){
            throw new IllegalArgumentException("cmcCoinId is not null");
        }
        this.cmcCoinId = cmcCoinId;
        return this;
    }

    public CmcRank updateCmcCoinId(Long cmcCoinId) {
        if( cmcCoinId == null){
            throw new IllegalArgumentException("cmcCoinId is null");
        }
        this.cmcCoinId = cmcCoinId;
        return this;
    }
}
