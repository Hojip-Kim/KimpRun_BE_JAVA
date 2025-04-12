package kimp.market.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.market.Enum.MarketType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Table(name = "coin")
@NoArgsConstructor
public class Coin extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy ="coin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CoinExchange> coinExchanges;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name="en_name")
    private String englishName;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String content;

    public Coin(String symbol, String name, String englishName) {
        this.symbol = symbol;
        this.name = name;
        this.englishName = englishName;
    }

    public Coin(String symbol, String name, String englishName, String content) {
        this.symbol = symbol;
        this.name = name;
        this.englishName = englishName;
        this.content = content;
    }

    public Coin writeContent(String content) {
        this.content = content;
        return this;
    }

    public List<MarketType> getMarketTypes() {
        return this.coinExchanges.stream()
                .map(coinExchange -> coinExchange.getExchange().getMarket())
                .toList();
    }

    public void addCoinExchanges(CoinExchange coinExchange) {
        this.coinExchanges.add(coinExchange);
        coinExchange.setCoin(this);
    }

    public void removeCoinExchange(CoinExchange coinExchange) {
        this.coinExchanges.remove(coinExchanges);
        coinExchange.setCoin(null);
    }

    public Coin updateSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public Coin updateName(String name) {
        this.name = name;
        return this;
    }
    public Coin updateContent(String content) {
        this.content = content;
        return this;
    }
    public Coin updateEnglishName(String englishName) {
        this.englishName = englishName;
        return this;
    }

}
