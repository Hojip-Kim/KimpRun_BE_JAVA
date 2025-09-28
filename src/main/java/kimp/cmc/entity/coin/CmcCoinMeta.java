package kimp.cmc.entity.coin;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="cmc_coin_meta",
        indexes = {
                @Index(name = "idx_cmc_coin_meta_coin_id", columnList = "cmc_coin_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = "cmc_coin_id")
)
@NoArgsConstructor
@Getter
public class CmcCoinMeta extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "cmc_coin_id")
    private Long cmcCoinId;

    @OneToOne(mappedBy = "cmcCoinMeta", fetch = FetchType.LAZY)
    @JoinColumn(name="cmc_coin_info_id", referencedColumnName = "id", nullable = true)
    private CmcCoinInfo cmcCoinInfo;
    // 암호화폐인지 여부
    // 시가총액 (price * circulating_supply)
    @Column(nullable = false, name = "market_cap")
    private String marketCap;
    // 전체 암호화폐 시가총액 중 이 코인이 차지하는 비율(%)
    @Column(nullable = false, name = "market_cap_dominance")
    private Double marketCapDominance;
    // 현재 “유통 중”인 코인 수
    @Column(nullable = false, name="fully_diluted_market_cap")
    private String fullyDilutedMarketCap;
    // 현재 발행된 코인 수
    @Column(nullable = false, name="circulating_supply")
    private String circulatingSupply;
    // 지금까지 발행된(생성된) 전체 코인 수
    @Column(nullable = false, name="total_supply")
    private String totalSupply;
    // 프로토콜상 발행 가능한 “최대” 코인 수 (제한 없으면 null)
    @Column(nullable = false, name="max_supply")
    private String maxSupply;
    // 프로젝트 측이 직접 보고한 유통량 (없으면 null)
    @Column(nullable = false, name="self_reported_circulating_supply")
    private String selfReportedCirculatingSupply;
    // 프로젝트 측이 직접 보고한 시가총액
    @Column(nullable = false, name="self_reported_market_cap")
    private String selfReportedMarketCap;

    public CmcCoinMeta(Long cmcCoinId, String marketCap, double marketCapDominance, String fullyDilutedMarketCap, String circulatingSupply, String totalSupply, String maxSupply, String selfReportedCirculatingSupply, String selfReportedMarketCap) {
        this.cmcCoinId = cmcCoinId;
        this.marketCap = marketCap;
        this.marketCapDominance = marketCapDominance;
        this.fullyDilutedMarketCap = fullyDilutedMarketCap;
        this.circulatingSupply = circulatingSupply;
        this.totalSupply = totalSupply;
        this.maxSupply = maxSupply;
        this.selfReportedCirculatingSupply = selfReportedCirculatingSupply;
        this.selfReportedMarketCap = selfReportedMarketCap;
    }

    public CmcCoinMeta setCmcCoinInfo(CmcCoinInfo cmcCoinInfo) {
        if( cmcCoinInfo != null){
            throw new IllegalArgumentException("cmcCoinInfo is not null");
        }
        this.cmcCoinInfo = cmcCoinInfo;
        return this;
    }

    public CmcCoinMeta updateCmcCoinInfo(CmcCoinInfo cmcCoinInfo) {
        if( cmcCoinInfo == null){
            throw new IllegalArgumentException("cmcCoinInfo is null");
        }
        this.cmcCoinInfo = cmcCoinInfo;
        return this;
    }
}
