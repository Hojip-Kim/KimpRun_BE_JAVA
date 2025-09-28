package kimp.cmc.entity.coin;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="cmc_coin_info",
        indexes = {
                @Index(name = "idx_cmc_coin_info_coin_id", columnList = "cmc_coin_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = "cmc_coin_id")
)
@NoArgsConstructor
public class CmcCoinInfo extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "cmc_coin_id")
    private Long cmcCoinId;

    @OneToOne(mappedBy = "cmcCoinInfo", fetch = FetchType.LAZY)
    @JoinColumn(name="cmc_coin_id", referencedColumnName = "cmc_coin_id", nullable = true)
    private CmcCoin cmcCoin;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CmcCoinMeta cmcCoinMeta;
    // 백서 요약 또는 코인 설명
    @Column(nullable = false, name="description", columnDefinition = "TEXT")
    private String description;
    // 총 발행량 제한이 없는 코인인지 (true면 제한 없음)
    @Column(nullable = false, name="infinite_supply")
    private boolean infiniteSupply;
    // 법정화폐인지 여부 (0=암호화폐)
    @Column(nullable = false, name="is_fiat")
    private int isFiat;
    // 이 메타데이터가 마지막으로 갱신된 시각
    @Column(nullable = false, name="last_updated")
    private LocalDateTime lastUpdated;

    public CmcCoinInfo(Long cmcCoinId, String description, boolean infiniteSupply, int isFiat, LocalDateTime lastUpdated) {
        this.cmcCoinId = cmcCoinId;
        this.description = description;
        this.infiniteSupply = infiniteSupply;
        this.isFiat = isFiat;
        this.lastUpdated = lastUpdated;
    }

    public CmcCoinInfo setCmcCoin(CmcCoin cmcCoin) {
        if( cmcCoin != null){
            throw new IllegalArgumentException("cmcCoin is not null");
        }
        this.cmcCoin = cmcCoin;
        return this;
    }

    public CmcCoinInfo updateCmcCoin(CmcCoin cmcCoin) {
        if( cmcCoin == null){
            throw new IllegalArgumentException("cmcCoin is null");
        }
        this.cmcCoin = cmcCoin;
        return this;
    }

    public CmcCoinInfo setCmcCoinMeta(CmcCoinMeta cmcCoinMeta) {
        if( cmcCoinMeta != null){
            throw new IllegalArgumentException("cmcCoinMeta is not null");
        }
        this.cmcCoinMeta = cmcCoinMeta;
        return this;
    }

    public CmcCoinInfo updateCmcCoinMeta(CmcCoinMeta cmcCoinMeta) {
        if( cmcCoinMeta == null){
            throw new IllegalArgumentException("cmcCoinMeta is null");
        }
        this.cmcCoinMeta = cmcCoinMeta;
        return this;
    }
}
