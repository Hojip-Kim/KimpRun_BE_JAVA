package kimp.market.entity;

import jakarta.persistence.*;
import kimp.exchange.entity.Exchange;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="coin_exchange")
@Getter
@NoArgsConstructor
public class CoinExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="coin_id")
    private Coin coin;

    @ManyToOne
    @JoinColumn(name="exchange_id")
    private Exchange exchange;


    public CoinExchange(Coin coin, Exchange exchange) {
        this.coin = coin;
        this.exchange = exchange;
    }

    public CoinExchange setCoin(Coin coin) {
        this.coin = coin;
        return this;
    }

    public CoinExchange setExchange(Exchange exchange) {
        this.exchange = exchange;
        return this;
    }




}
