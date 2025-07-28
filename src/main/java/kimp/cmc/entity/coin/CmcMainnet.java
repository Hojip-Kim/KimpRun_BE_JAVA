package kimp.cmc.entity.coin;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="cmc_mainnet")
@NoArgsConstructor
@Getter
public class CmcMainnet extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cmc_coin_id", referencedColumnName = "cmc_coin_id", nullable = true)
    private CmcCoin cmcCoin;

    @Column(nullable = false, name="explorer_url")
    private String explorerUrl;

    public CmcMainnet(String explorerUrl) {
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
