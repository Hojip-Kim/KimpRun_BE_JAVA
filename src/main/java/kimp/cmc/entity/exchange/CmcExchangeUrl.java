package kimp.cmc.entity.exchange;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table( name = "cmc_exchange_url")
@Getter
@NoArgsConstructor
public class CmcExchangeUrl extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(mappedBy = "cmcExchangeUrl", fetch = FetchType.LAZY)
    @JoinColumn(name="cmc_exchange_id", referencedColumnName = "cmc_exchange_id", nullable = true)
    private CmcExchange cmcExchange;

    @Column(nullable = false, name = "website")
    private String website;

    @Column(nullable = false, name = "twitter")
    private String twitter;

    @Column(nullable = false, name = "register")
    private String register;


    public CmcExchangeUrl(String website, String twitter, String register) {
        this.website = website;
        this.twitter = twitter;
        this.register = register;
    }

    public CmcExchangeUrl setCmcExchange(CmcExchange cmcExchange) {
        if( cmcExchange != null){
            throw new IllegalArgumentException("cmcExchange is not null");
        }
        this.cmcExchange = cmcExchange;
        return this;
    }

    public CmcExchangeUrl updateCmcExchange(CmcExchange cmcExchange) {
        if( cmcExchange == null){
            throw new IllegalArgumentException("cmcExchange is null");
        }
        this.cmcExchange = cmcExchange;
        return this;
    }
}
