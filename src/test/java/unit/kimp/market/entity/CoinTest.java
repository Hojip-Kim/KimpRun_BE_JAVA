package unit.kimp.market.entity;

import kimp.exchange.entity.Exchange;
import kimp.market.Enum.MarketType;
import kimp.market.entity.Coin;
import kimp.market.entity.CoinExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Coin Entity 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class CoinTest {

    private Coin coin;
    private final String symbol = "BTC";
    private final String name = "비트코인";
    private final String englishName = "Bitcoin";
    private final String content = "Bitcoin is a cryptocurrency.";

    @BeforeEach
    void setup() {
        coin = new Coin(symbol, name, englishName);
    }

    @Test
    @DisplayName("Coin 생성자 테스트 (3개 파라미터)")
    void shouldTestConstructorWithThreeParams() {
        assertThat(coin.getSymbol()).isEqualTo(symbol);
        assertThat(coin.getName()).isEqualTo(name);
        assertThat(coin.getEnglishName()).isEqualTo(englishName);
        assertThat(coin.getContent()).isNull();
        assertThat(coin.getCoinExchanges()).isEmpty();
    }

    @Test
    @DisplayName("Coin 생성자 테스트 (4개 파라미터)")
    void shouldTestConstructorWithFourParams() {
        // Given
        Coin coinWithContent = new Coin(symbol, name, englishName, content);

        // Then
        assertThat(coinWithContent.getSymbol()).isEqualTo(symbol);
        assertThat(coinWithContent.getName()).isEqualTo(name);
        assertThat(coinWithContent.getEnglishName()).isEqualTo(englishName);
        assertThat(coinWithContent.getContent()).isEqualTo(content);
        assertThat(coinWithContent.getCoinExchanges()).isEmpty();
    }

    @Test
    @DisplayName("writeContent 메서드 테스트")
    void shouldWriteContent() {
        // When
        Coin result = coin.writeContent(content);

        // Then
        assertThat(result).isSameAs(coin);
        assertThat(coin.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("getMarketTypes 메서드 테스트")
    void shouldGetMarketTypes() {
        // Given
        Exchange upbitExchange = new Exchange(MarketType.UPBIT, "https://upbit.com");
        Exchange binanceExchange = new Exchange(MarketType.BINANCE, "https://binance.com");
        
        CoinExchange upbitCoinExchange = new CoinExchange(coin, upbitExchange);
        CoinExchange binanceCoinExchange = new CoinExchange(coin, binanceExchange);
        
        coin.addCoinExchanges(upbitCoinExchange);
        coin.addCoinExchanges(binanceCoinExchange);

        // When
        List<MarketType> marketTypes = coin.getMarketTypes();

        // Then
        assertThat(marketTypes).hasSize(2);
        assertThat(marketTypes).containsExactlyInAnyOrder(MarketType.UPBIT, MarketType.BINANCE);
    }

    @Test
    @DisplayName("addCoinExchanges 메서드 테스트")
    void shouldAddCoinExchanges() {
        // Given
        Exchange exchange = new Exchange(MarketType.UPBIT, "https://upbit.com");
        CoinExchange coinExchange = new CoinExchange(null, exchange);

        // When
        coin.addCoinExchanges(coinExchange);

        // Then
        assertThat(coin.getCoinExchanges()).hasSize(1);
        assertThat(coin.getCoinExchanges().get(0)).isEqualTo(coinExchange);
        assertThat(coinExchange.getCoin()).isEqualTo(coin);
    }

    @Test
    @DisplayName("removeCoinExchange 메서드 테스트")
    void shouldRemoveCoinExchange() {
        // Given
        Exchange exchange = new Exchange(MarketType.UPBIT, "https://upbit.com");
        CoinExchange coinExchange = new CoinExchange(null, exchange);
        coin.addCoinExchanges(coinExchange);

        // When
        coin.removeCoinExchange(coinExchange);

        // Then
        assertThat(coin.getCoinExchanges()).isEmpty();
        assertThat(coinExchange.getCoin()).isNull();
    }

    @Test
    @DisplayName("updateSymbol 메서드 테스트")
    void shouldUpdateSymbol() {
        // Given
        String newSymbol = "ETH";

        // When
        Coin result = coin.updateSymbol(newSymbol);

        // Then
        assertThat(result).isSameAs(coin);
        assertThat(coin.getSymbol()).isEqualTo(newSymbol);
    }

    @Test
    @DisplayName("updateName 메서드 테스트")
    void shouldUpdateName() {
        // Given
        String newName = "이더리움";

        // When
        Coin result = coin.updateName(newName);

        // Then
        assertThat(result).isSameAs(coin);
        assertThat(coin.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("updateContent 메서드 테스트")
    void shouldUpdateContent() {
        // Given
        String newContent = "Ethereum is a cryptocurrency.";

        // When
        Coin result = coin.updateContent(newContent);

        // Then
        assertThat(result).isSameAs(coin);
        assertThat(coin.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("updateEnglishName 메서드 테스트")
    void shouldUpdateEnglishName() {
        // Given
        String newEnglishName = "Ethereum";

        // When
        Coin result = coin.updateEnglishName(newEnglishName);

        // Then
        assertThat(result).isSameAs(coin);
        assertThat(coin.getEnglishName()).isEqualTo(newEnglishName);
    }
}