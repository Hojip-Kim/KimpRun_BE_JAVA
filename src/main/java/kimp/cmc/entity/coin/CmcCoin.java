package kimp.cmc.entity.coin;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.market.entity.Coin;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="cmc_coin",
        indexes = {
                @Index(name = "idx_cmc_coin_id", columnList = "cmc_coin_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = "cmc_coin_id")
)
@Getter
@NoArgsConstructor
public class CmcCoin extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = true, name="logo")
    private String logo;

    @Column(nullable = false, name="cmc_coin_id")
    private Long cmcCoinId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id", referencedColumnName = "id", nullable = true)
    private Coin coin;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private CmcCoinInfo cmcCoinInfo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CmcMainnet> cmcMainnet = new ArrayList<>() ;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CmcPlatform> cmcPlatforms = new ArrayList<>() ;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private CmcRank cmcRank;

    @Column(nullable = false, name="name")
    private String name;

    @Column(nullable = false, name="symbol")
    private String symbol;

    @Column(nullable = false, name="slug")
    private String slug;

    // 이 코인이 CMC에서 활성화(1) 되어 있는지 여부
    @Column(nullable = false, name="is_active")
    private Boolean isActive;

    @Column(nullable = false, name="status")
    private Boolean status;

    @Column(nullable = false, name="is_mainnet")
    private Boolean isMainnet;

    @Column(nullable = false, name="first_historical_data")
    private LocalDateTime firstHistoricalData;

    @Column(nullable = false, name="last_historical_data")
    private LocalDateTime lastHistoricalData;

    public CmcCoin(long cmcCoinId, String logo, String name, String symbol, String slug, Boolean isActive, Boolean status, Boolean isMainnet, LocalDateTime firstHistoricalData, LocalDateTime lastHistoricalData) {
        this.cmcCoinId = cmcCoinId;
        this.logo = logo;
        this.name = name;
        this.symbol = symbol;
        this.slug = slug;
        this.isActive = isActive;
        this.status = status;
        this.isMainnet = isMainnet;
        this.firstHistoricalData = firstHistoricalData;
        this.lastHistoricalData = lastHistoricalData;
    }

    public CmcCoin setCmcCoinInfo(CmcCoinInfo cmcCoinInfo) {
        if( cmcCoinInfo != null){
            throw new IllegalArgumentException("cmcCoinInfo is not null");
        }
        this.cmcCoinInfo = cmcCoinInfo;
        return this;
    }

    public CmcCoin updateCmcCoinInfo(CmcCoinInfo cmcCoinInfo) {
        if( cmcCoinInfo == null){
            throw new IllegalArgumentException("cmcCoinInfo is null");
        }
        this.cmcCoinInfo = cmcCoinInfo;
        return this;
    }

    public CmcCoin addCmcMainnet(CmcMainnet cmcMainnet) {
        if( cmcMainnet == null){
            throw new IllegalArgumentException("cmcMainnet is null");
        }
        this.cmcMainnet.add(cmcMainnet);
        return this;
    }

    public CmcCoin deleteCmcMainnet(CmcMainnet cmcMainnet) {
        if( cmcMainnet == null){
            throw new IllegalArgumentException("cmcMainnet is null");
        }
        this.cmcMainnet.remove(cmcMainnet);
        return this;
    }

    public CmcCoin addCmcPlatform(CmcPlatform cmcPlatform) {
        if( cmcPlatform == null){
            throw new IllegalArgumentException("cmcPlatform is null");
        }
        this.cmcPlatforms.add(cmcPlatform);
        return this;
    }

    public CmcCoin deleteCmcPlatform(CmcPlatform cmcPlatform) {
        if( cmcPlatform == null){
            throw new IllegalArgumentException("cmcPlatform is null");
        }
        this.cmcPlatforms.remove(cmcPlatform);
        return this;
    }

    public CmcCoin setCoin(Coin coin) {
        if( coin != null){
            throw new IllegalArgumentException("coin is not null");
        }
        this.coin = coin;
        return this;
    }
    public CmcCoin updateCoin(Coin coin) {
        if( coin == null){
            throw new IllegalArgumentException("coin is null");
        }
        this.coin = coin;
        return this;
    }
    public CmcCoin deleteCoin() {
        this.coin = null;
        return this;
    }

    public CmcCoin setCmcRank(CmcRank cmcRank) {
        if( cmcRank != null){
            throw new IllegalArgumentException("cmcRank is not null");
        }
        this.cmcRank = cmcRank;
        return this;
    }

    public CmcCoin updateCmcRank(CmcRank cmcRank) {
        if( cmcRank == null){
            throw new IllegalArgumentException("cmcRank is null");
        }
        this.cmcRank = cmcRank;
        return this;
    }

    public CmcCoin deleteCmcRank() {
        this.cmcRank = null;
        return this;
    }


}
