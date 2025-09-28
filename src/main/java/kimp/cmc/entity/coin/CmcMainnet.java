package kimp.cmc.entity.coin;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="cmc_mainnet",
        indexes = {
                @Index(name = "idx_cmc_mainnet_coin_id", columnList = "cmc_coin_id"),
                @Index(name = "idx_cmc_mainnet_url", columnList = "explorer_url")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"cmc_coin_id", "explorer_url"})
)
@NoArgsConstructor
@Getter
public class CmcMainnet extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "cmc_coin_id")
    private Long cmcCoinId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cmc_coin_id", referencedColumnName = "cmc_coin_id", nullable = true, insertable = false, updatable = false)
    private CmcCoin cmcCoin;

    @Column(nullable = false, name="explorer_url")
    private String explorerUrl;

    public CmcMainnet(Long cmcCoinId, String explorerUrl) {
        this.cmcCoinId = cmcCoinId;
        this.explorerUrl = explorerUrl;
    }

    public CmcMainnet setCmcCoin(CmcCoin cmcCoin) {
        if( cmcCoin != null){
            throw new IllegalArgumentException("cmcCoin is not null");
        }
        this.cmcCoin = cmcCoin;
        this.cmcCoin.addCmcMainnet(this);
        return this;
    }

    public CmcMainnet updateCmcCoin(CmcCoin cmcCoin) {
        if( cmcCoin == null){
            throw new IllegalArgumentException("cmcCoin is null");
        }
        this.cmcCoin = cmcCoin;
        return this;
    }
}
