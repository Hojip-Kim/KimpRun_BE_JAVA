package kimp.cmc.entity.exchange;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cmc_exchange_info")
@Getter
@NoArgsConstructor
public class CmcExchangeInfo extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = true, name = "fiats")
    private String fiats;

    @OneToOne(mappedBy = "cmcExchangeInfo", fetch = FetchType.LAZY)
    @JoinColumn(name="cmc_exchange_id", referencedColumnName = "cmc_exchange_id", nullable = true)
    private CmcExchange cmcExchange;

    public CmcExchangeInfo(String fiats) {
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
