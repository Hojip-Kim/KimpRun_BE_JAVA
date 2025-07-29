package unit.kimp.market.components.impl.market;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.Enum.MarketType;
import kimp.market.components.MarketListProvider;
import kimp.market.components.impl.market.Bithumb;
import kimp.market.dto.coin.common.ChangeCoinDto;
import kimp.market.dto.coin.common.ServiceCoinWrapperDto;
import kimp.market.dto.coin.common.crypto.BithumbCryptoDto;
import kimp.market.dto.coin.common.market.BithumbDto;
import kimp.market.dto.market.common.MarketList;
import kimp.market.dto.market.response.BithumbTicker;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.service.CoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Bithumb 컴포넌트 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class BithumbTest {

    @Mock
    private MarketListProvider bithumbMarketListProvider;

    @Mock
    private RestClient restClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CoinService coinService;

    @InjectMocks
    private Bithumb bithumb;

    private List<String> marketList;
    private MarketList<BithumbCryptoDto> bithumbMarketList;
    private BithumbTicker[] tickers;
    private MarketDataList<BithumbDto> bithumbMarketDataList;

    @BeforeEach
    void setUp() throws Exception {
        // Set up test data
        marketList = Arrays.asList("KRW-BTC", "KRW-ETH", "KRW-XRP");

        // Set up bithumbMarketList
        List<BithumbCryptoDto> cryptoDtoList = new ArrayList<>();
        for (String market : marketList) {
            String replacedMarket = market.replace("KRW-", "");
            cryptoDtoList.add(new BithumbCryptoDto(replacedMarket, "KRW"));
        }
        bithumbMarketList = new MarketList<>(cryptoDtoList);

        // Set up tickers using Mockito
        tickers = new BithumbTicker[3];
        for (int i = 0; i < 3; i++) {
            tickers[i] = mock(BithumbTicker.class);
            when(tickers[i].getMarket()).thenReturn(marketList.get(i));
            when(tickers[i].getAccTradeVolume24h()).thenReturn(BigDecimal.valueOf(1000.0));
            when(tickers[i].getChangeRate()).thenReturn(BigDecimal.valueOf(0.05));
            when(tickers[i].getHighest52WeekPrice()).thenReturn(BigDecimal.valueOf(50000.0));
            when(tickers[i].getLowest52WeekPrice()).thenReturn(BigDecimal.valueOf(30000.0));
            when(tickers[i].getOpeningPrice()).thenReturn(BigDecimal.valueOf(40000.0));
            when(tickers[i].getTradePrice()).thenReturn(BigDecimal.valueOf(42000.0));
            when(tickers[i].getChange()).thenReturn("RISE");
            when(tickers[i].getAccTradePrice24h()).thenReturn(BigDecimal.valueOf(42000000.0));
        }

        // Set up bithumbMarketDataList
        List<BithumbDto> marketDataList = new ArrayList<>();
        for (BithumbTicker ticker : tickers) {
            BithumbDto bithumbDto = new BithumbDto(
                ticker.getMarket().replace("KRW-", ""),
                ticker.getAccTradeVolume24h(),
                ticker.getChangeRate(),
                ticker.getHighest52WeekPrice(),
                ticker.getLowest52WeekPrice(),
                ticker.getOpeningPrice(),
                ticker.getTradePrice(),
                ticker.getChange(),
                ticker.getAccTradePrice24h()
            );
            marketDataList.add(bithumbDto);
        }
        bithumbMarketDataList = new MarketDataList<>(marketDataList);

        // Set bithumbTickerUrl using reflection
        Field tickerUrlField = Bithumb.class.getDeclaredField("bithumbTickerUrl");
        tickerUrlField.setAccessible(true);
        tickerUrlField.set(bithumb, "http://dummy-ticker-url.com/api");
    }

    @Test
    @DisplayName("initFirst 메서드 테스트")
    void shouldInitializeFirst() throws IOException {
        // Given
        when(bithumbMarketListProvider.getMarketListWithTicker()).thenReturn(marketList);

        // When
        bithumb.initFirst();

        // Then
        verify(bithumbMarketListProvider, times(1)).getMarketListWithTicker();
        assertThat(bithumb.getMarketList()).isNotNull();
    }

    @Test
    @DisplayName("getMarketList 메서드 테스트")
    void shouldReturnMarketList() throws IOException {
        // Given
        when(bithumbMarketListProvider.getMarketListWithTicker()).thenReturn(marketList);
        bithumb.setBithumbMarketList();

        // When
        MarketList result = bithumb.getMarketList();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPairList()).hasSize(3);
        assertThat(result.getPairList()).containsExactly("BTC", "ETH", "XRP");
    }

    @Test
    @DisplayName("getServiceCoins 메서드 테스트")
    void shouldReturnServiceCoins() throws IOException {
        // Given
        when(bithumbMarketListProvider.getMarketListWithTicker()).thenReturn(marketList);
        bithumb.setBithumbMarketList();

        // When
        ServiceCoinWrapperDto result = bithumb.getServiceCoins();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMarketType()).isEqualTo(MarketType.BITHUMB);
        assertThat(result.getServiceCoins()).hasSize(3);
        assertThat(result.getServiceCoins().get(0).getSymbol()).isEqualTo("BTC");
    }

    @Test
    @DisplayName("getMarketType 메서드 테스트")
    void shouldReturnMarketType() {
        // When
        MarketType result = bithumb.getMarketType();

        // Then
        assertThat(result).isEqualTo(MarketType.BITHUMB);
    }

    @Test
    @DisplayName("setMarketDataList 메서드 테스트 - 마켓 리스트가 null일 때 예외 발생")
    void shouldThrowExceptionWhenSetMarketDataListWithNullMarketList() {
        // When & Then
        assertThatThrownBy(() -> bithumb.setMarketDataList())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bithumb Market List is null");
    }

    @Test
    @DisplayName("setBithumbMarketList 메서드 테스트")
    void shouldSetBithumbMarketList() throws IOException {
        // Given
        when(bithumbMarketListProvider.getMarketListWithTicker()).thenReturn(marketList);

        // When
        bithumb.setBithumbMarketList();

        // Then
        verify(bithumbMarketListProvider, times(1)).getMarketListWithTicker();
        assertThat(bithumb.getMarketList()).isNotNull();
        assertThat(bithumb.getMarketList().getPairList()).hasSize(3);
        assertThat(bithumb.getMarketList().getPairList()).containsExactly("BTC", "ETH", "XRP");
    }

    @Test
    @DisplayName("scheduledSetupUpbitMarketData 메서드 테스트 - 변경사항이 있을 때")
    void shouldHandleScheduledSetupUpbitMarketDataWithChanges() throws IOException, NoSuchFieldException, IllegalAccessException {
        // Given
        // Set up initial market list
        when(bithumbMarketListProvider.getMarketListWithTicker()).thenReturn(marketList);
        bithumb.setBithumbMarketList();

        // Set up new market list with changes
        List<String> newMarketList = Arrays.asList("KRW-BTC", "KRW-ETH", "KRW-XRP", "KRW-SOL");
        when(bithumbMarketListProvider.getMarketListWithTicker()).thenReturn(newMarketList);

        // When
        bithumb.scheduledSetupUpbitMarketData();

        // Then
        verify(bithumbMarketListProvider, times(2)).getMarketListWithTicker();
        verify(coinService, times(1)).createWithDeleteCoin(any(ChangeCoinDto.class));
    }

    @Test
    @DisplayName("scheduledSetupUpbitMarketData 메서드 테스트 - 변경사항이 없을 때")
    void shouldHandleScheduledSetupUpbitMarketDataNoChanges() throws IOException, NoSuchFieldException, IllegalAccessException {
        // Given
        Bithumb bithumbSpy = spy(bithumb);
        MarketList<BithumbCryptoDto> marketListObj = new MarketList<>(new ArrayList<>());
        doReturn(marketListObj).when(bithumbSpy).getMarketList();

        // When
        bithumbSpy.scheduledSetupUpbitMarketData();

        // Then
        verify(coinService, never()).createWithDeleteCoin(any(ChangeCoinDto.class));
    }
}
