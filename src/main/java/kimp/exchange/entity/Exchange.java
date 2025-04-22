package kimp.exchange.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.market.Enum.MarketType;
import kimp.market.entity.CoinExchange;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "exchange")
@NoArgsConstructor
public class Exchange extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MarketType market;

    @Column(nullable = false)
    private String link;

    @OneToMany(mappedBy = "exchange", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CoinExchange> coinExchanges = new ArrayList<>();

    public Exchange(MarketType market, String link) {
        this.market = market;
        this.link = link;
    }

    public Exchange updateExchangeName(MarketType market){
        if(!isValidString(market.name())){
            throw new IllegalArgumentException("Exchange (update) name cannot be null or empty");
        }

        if (this.market == null || (this.market != null && this.market.name().isEmpty())) {
            throw new IllegalArgumentException("can't update exchange name");
        }

        this.market = market;

        return this;

    }

    public Exchange updateExchangeLink(String link){
        if(!isValidString(link)){
            throw new IllegalArgumentException("Exchange link cannot be null or empty");
        }

        if (this.link == null || (this.link != null && this.link.isBlank())) {
            throw new IllegalArgumentException("can't update exchange link");
        }

        this.link = link;

        return this;
    }

    private boolean isValidString(String param){
        if(param == null || param.isEmpty()) {
            return false;
        }
        return true;
    }

    public void addCoinExchanges(CoinExchange coinExchange){
        this.coinExchanges.add(coinExchange);
        coinExchange.setExchange(this);
    }

    public void removeCoinExchanges(CoinExchange coinExchange){
        this.coinExchanges.remove(coinExchange);
        coinExchange.setExchange(null);
    }


}
