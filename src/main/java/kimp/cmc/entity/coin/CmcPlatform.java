package kimp.cmc.entity.coin;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cmc_platform",
        indexes = {
                @Index(name = "idx_cmc_platform_coin_id", columnList = "cmc_coin_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = "cmc_coin_id")
)
@Getter
@NoArgsConstructor
public class CmcPlatform extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "cmc_coin_id")
    private Long cmcCoinId;

    @Column(nullable = true, name="name")
    private String name;

    @Column(nullable = true, name="symbol")
    private String symbol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cmc_coin_id", referencedColumnName = "cmc_coin_id", nullable = true, insertable = false, updatable = false)
    private CmcCoin cmcCoin;

    public CmcPlatform(Long cmcCoinId, String name, String symbol) {
        this.cmcCoinId = cmcCoinId;
        this.name = name;
        this.symbol = symbol;
    }

    public CmcPlatform setCmcCoin(CmcCoin cmcCoin) {
        if( cmcCoin != null){
            throw new IllegalArgumentException("cmcCoin is not null");
        }
        this.cmcCoin = cmcCoin;
        this.cmcCoin.addCmcPlatform(this);
        return this;
    }
    public CmcPlatform updateCmcCoin(CmcCoin cmcCoin) {
        if( cmcCoin == null){
            throw new IllegalArgumentException("cmcCoin is null");
        }
        this.cmcCoin = cmcCoin;
        return this;
    }

}
