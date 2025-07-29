package unit.kimp.market.entity;

import kimp.exchange.entity.Exchange;
import kimp.market.Enum.MarketType;
import kimp.market.entity.Coin;
import kimp.market.entity.CoinExchange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CoinExchange Entity 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class CoinExchangeTest {

    @Test
    @DisplayName("CoinExchange 기본 생성자 테스트")
    void shouldTestDefaultConstructor() {
        // When
        CoinExchange coinExchange = new CoinExchange();

        // Then
        assertThat(coinExchange.getId()).isNull();
        assertThat(coinExchange.getCoin()).isNull();
        assertThat(coinExchange.getExchange()).isNull();
    }

    @Test
    @DisplayName("CoinExchange 생성자 테스트")
    void shouldTestConstructor() {
        // Given
        Coin coin = new Coin("BTC", "비트코인", "Bitcoin");
        Exchange exchange = new Exchange(MarketType.UPBIT, "https://upbit.com");

        // When
        CoinExchange coinExchange = new CoinExchange(coin, exchange);

        // Then
        assertThat(coinExchange.getCoin()).isEqualTo(coin);
        assertThat(coinExchange.getExchange()).isEqualTo(exchange);
    }

    @Test
    @DisplayName("setCoin 메서드 테스트")
    void shouldSetCoin() {
        // Given
        CoinExchange coinExchange = new CoinExchange();
        Coin coin = new Coin("BTC", "비트코인", "Bitcoin");

        // When
        CoinExchange result = coinExchange.setCoin(coin);

        // Then
        assertThat(result).isSameAs(coinExchange);
        assertThat(coinExchange.getCoin()).isEqualTo(coin);
    }

    @Test
    @DisplayName("setExchange 메서드 테스트")
    void shouldSetExchange() {
        // Given
        CoinExchange coinExchange = new CoinExchange();
        Exchange exchange = new Exchange(MarketType.UPBIT, "https://upbit.com");

        // When
        CoinExchange result = coinExchange.setExchange(exchange);

        // Then
        assertThat(result).isSameAs(coinExchange);
        assertThat(coinExchange.getExchange()).isEqualTo(exchange);
    }

    @Test
    @DisplayName("양방향 관계 설정 테스트 - Coin과 CoinExchange")
    void shouldSetBidirectionalRelationshipWithCoin() {
        // Given
        Coin coin = new Coin("BTC", "비트코인", "Bitcoin");
        CoinExchange coinExchange = new CoinExchange();

        // When
        coin.addCoinExchanges(coinExchange);

        // Then
        assertThat(coin.getCoinExchanges()).contains(coinExchange);
        assertThat(coinExchange.getCoin()).isEqualTo(coin);
    }

    @Test
    @DisplayName("양방향 관계 설정 테스트 - Exchange와 CoinExchange")
    void shouldSetBidirectionalRelationshipWithExchange() {
        // Given
        Exchange exchange = new Exchange(MarketType.UPBIT, "https://upbit.com");
        CoinExchange coinExchange = new CoinExchange();

        // When
        exchange.addCoinExchanges(coinExchange);

        // Then
        assertThat(exchange.getCoinExchanges()).contains(coinExchange);
        assertThat(coinExchange.getExchange()).isEqualTo(exchange);
    }

    @Test
    @DisplayName("양방향 관계 해제 테스트 - Coin과 CoinExchange")
    void shouldRemoveBidirectionalRelationshipWithCoin() {
        // Given
        Coin coin = new Coin("BTC", "비트코인", "Bitcoin");
        CoinExchange coinExchange = new CoinExchange();
        coin.addCoinExchanges(coinExchange);

        // When
        coin.removeCoinExchange(coinExchange);

        // Then
        assertThat(coin.getCoinExchanges()).doesNotContain(coinExchange);
        assertThat(coinExchange.getCoin()).isNull();
    }

    @Test
    @DisplayName("양방향 관계 해제 테스트 - Exchange와 CoinExchange")
    void shouldRemoveBidirectionalRelationshipWithExchange() {
        // Given
        Exchange exchange = new Exchange(MarketType.UPBIT, "https://upbit.com");
        CoinExchange coinExchange = new CoinExchange();
        exchange.addCoinExchanges(coinExchange);

        // When
        exchange.removeCoinExchanges(coinExchange);

        // Then
        assertThat(exchange.getCoinExchanges()).doesNotContain(coinExchange);
        assertThat(coinExchange.getExchange()).isNull();
    }
}