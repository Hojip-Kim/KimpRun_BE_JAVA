package kimp.cmc.entity.exchange;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cmc_exchange_meta",
        indexes = {
                @Index(name = "idx_cmc_exchange_meta_exchange_id", columnList = "cmc_exchange_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = "cmc_exchange_id")
)
@NoArgsConstructor
@Getter
public class CmcExchangeMeta extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "cmc_exchange_id")
    private Long cmcExchangeId;

    @OneToOne(mappedBy = "cmcExchangeMeta", fetch = FetchType.LAZY)
    @JoinColumn(name="cmc_exchange_id", referencedColumnName = "cmc_exchange_id", nullable = true, insertable = false, updatable = false)
    private CmcExchange cmcExchange;

    @Column(nullable = false, name = "market_fee")
    private BigDecimal marketFee;

    @Column(nullable = false, name = "taker_fee")
    private BigDecimal takerFee;

    @Column(nullable = false, name = "spot_volume_usd")
    private BigDecimal spotVolumeUsd;

    @Column(nullable = false, name = "spot_volume_last_updated")
    private LocalDateTime spotVolumeLastUpdated;

    @Column(nullable = false, name = "weekly_visits")
    private Long weeklyVisits;


    public CmcExchangeMeta(Long cmcExchangeId, BigDecimal marketFee, BigDecimal takerFee, BigDecimal spotVolumeUsd, LocalDateTime spotVolumeLastUpdated, Long weeklyVisits) {
        this.cmcExchangeId = cmcExchangeId;
        this.marketFee = marketFee;
        this.takerFee = takerFee;
        this.spotVolumeUsd = spotVolumeUsd;
        this.spotVolumeLastUpdated = spotVolumeLastUpdated;
        this.weeklyVisits = weeklyVisits;
    }

    public CmcExchangeMeta setCmcExchange(CmcExchange cmcExchange) {
        if( cmcExchange != null){
            throw new IllegalArgumentException("cmcExchange is not null");
        }
        this.cmcExchange = cmcExchange;
        return this;
    }

    public CmcExchangeMeta updateCmcExchange(CmcExchange cmcExchange) {
        if( cmcExchange == null){
            throw new IllegalArgumentException("cmcExchange is null");
        }
        this.cmcExchange = cmcExchange;
        return this;
    }
}
