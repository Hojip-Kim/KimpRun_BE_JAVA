package unit.kimp.exchange.entity;

import kimp.exchange.entity.Exchange;
import kimp.exception.KimprunException;
import kimp.market.Enum.MarketType;
import kimp.market.entity.Coin;
import kimp.market.entity.CoinExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Exchange Entity 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class ExchangeTest {

    private Exchange exchange;
    private final MarketType marketType = MarketType.UPBIT;
    private final String link = "https://upbit.com";

    @BeforeEach
    void setup() {
        exchange = new Exchange(marketType, link);
    }

    @Test
    @DisplayName("Exchange 생성자 테스트")
    void constructorTest() {
        assertThat(exchange.getMarket()).isEqualTo(marketType);
        assertThat(exchange.getLink()).isEqualTo(link);
        assertThat(exchange.getCoinExchanges()).isEmpty();
    }

    @Test
    @DisplayName("updateExchangeName 메서드 테스트")
    void updateExchangeNameTest() {
        // Given
        MarketType newMarketType = MarketType.BINANCE;

        // When
        Exchange result = exchange.updateExchangeName(newMarketType);

        // Then
        assertThat(result).isSameAs(exchange);
        assertThat(exchange.getMarket()).isEqualTo(newMarketType);
    }

    @Test
    @DisplayName("updateExchangeName 메서드 - 빈 이름 예외 테스트")
    void updateExchangeNameEmptyNameExceptionTest() {
        // Given
        MarketType invalidMarketType = mock(MarketType.class);
        when(invalidMarketType.name()).thenReturn("");

        // When & Then
        assertThatThrownBy(() -> exchange.updateExchangeName(invalidMarketType))
                .isInstanceOf(KimprunException.class)
                .hasMessage("Exchange name cannot be null or empty");
    }

    @Test
    @DisplayName("updateExchangeName 메서드 - null 이름 예외 테스트")
    void updateExchangeNameNullNameExceptionTest() {
        // When & Then
        assertThatThrownBy(() -> exchange.updateExchangeName(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("updateExchangeLink 메서드 테스트")
    void updateExchangeLinkTest() {
        // Given
        String newLink = "https://binance.com";

        // When
        Exchange result = exchange.updateExchangeLink(newLink);

        // Then
        assertThat(result).isSameAs(exchange);
        assertThat(exchange.getLink()).isEqualTo(newLink);
    }

    @Test
    @DisplayName("updateExchangeLink 메서드 - 빈 링크 예외 테스트")
    void updateExchangeLinkEmptyLinkExceptionTest() {
        // When & Then
        assertThatThrownBy(() -> exchange.updateExchangeLink(""))
                .isInstanceOf(KimprunException.class)
                .hasMessage("Exchange link cannot be null or empty");
    }

    @Test
    @DisplayName("updateExchangeLink 메서드 - null 링크 예외 테스트")
    void updateExchangeLinkNullLinkExceptionTest() {
        // When & Then
        assertThatThrownBy(() -> exchange.updateExchangeLink(null))
                .isInstanceOf(KimprunException.class)
                .hasMessage("Exchange link cannot be null or empty");
    }

    @Test
    @DisplayName("addCoinExchanges 메서드 테스트")
    void addCoinExchangesTest() {
        // Given
        Coin coin = new Coin("BTC", "비트코인", "Bitcoin");
        CoinExchange coinExchange = new CoinExchange(coin, null);

        // When
        exchange.addCoinExchanges(coinExchange);

        // Then
        assertThat(exchange.getCoinExchanges()).hasSize(1);
        assertThat(exchange.getCoinExchanges().get(0)).isEqualTo(coinExchange);
        assertThat(coinExchange.getExchange()).isEqualTo(exchange);
    }

    @Test
    @DisplayName("removeCoinExchanges 메서드 테스트")
    void removeCoinExchangesTest() {
        // Given
        Coin coin = new Coin("BTC", "비트코인", "Bitcoin");
        CoinExchange coinExchange = new CoinExchange(coin, null);
        exchange.addCoinExchanges(coinExchange);

        // When
        exchange.removeCoinExchanges(coinExchange);

        // Then
        assertThat(exchange.getCoinExchanges()).isEmpty();
        assertThat(coinExchange.getExchange()).isNull();
    }
}