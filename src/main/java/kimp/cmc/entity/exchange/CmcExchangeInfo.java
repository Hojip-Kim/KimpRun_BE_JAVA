package kimp.cmc.entity.exchange;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cmc_exchange_info",
        indexes = {
                @Index(name = "idx_cmc_exchange_info_exchange_id", columnList = "cmc_exchange_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = "cmc_exchange_id")
)
@Getter
@NoArgsConstructor
public class CmcExchangeInfo extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "cmc_exchange_id")
    private Long cmcExchangeId;

    @Column(nullable = true, name = "fiats")
    private String fiats;

    @OneToOne(mappedBy = "cmcExchangeInfo", fetch = FetchType.LAZY)
    @JoinColumn(name="cmc_exchange_id", referencedColumnName = "cmc_exchange_id", nullable = true, insertable = false, updatable = false)
    private CmcExchange cmcExchange;

    public CmcExchangeInfo(Long cmcExchangeId, String fiats) {
        this.cmcExchangeId = cmcExchangeId;
        this.fiats = fiats;
    }

    public CmcExchangeInfo setCmcExchange(CmcExchange cmcExchange) {
        if( cmcExchange != null){
            throw new IllegalArgumentException("cmcExchange is not null");
        }
        this.cmcExchange = cmcExchange;
        return this;
    }

    public CmcExchangeInfo updateCmcExchange(CmcExchange cmcExchange) {
        if( cmcExchange == null){
            throw new IllegalArgumentException("cmcExchange is null");
        }
        this.cmcExchange = cmcExchange;
        return this;
    }
}
