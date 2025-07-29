package unit.kimp.market.components.impl.list_provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.common.MarketCommonMethod;
import kimp.market.components.impl.list_provider.CoinoneMarketListProvider;
import kimp.market.dto.market.common.CoinoneMarketInfo;
import kimp.market.dto.market.common.CoinoneMarketNameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoinoneMarketListProviderTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MarketCommonMethod marketCommonMethod;

    @InjectMocks
    private CoinoneMarketListProvider coinoneMarketListProvider;

    private CoinoneMarketNameData successResponse;
    private CoinoneMarketNameData errorResponse;
    private String coinoneApiUrl = "https://api.coinone.co.kr/public/v2/markets/KRW";

    @BeforeEach
    void setUp() {
        // Set the API URL using ReflectionTestUtils since it's normally injected via @Value
        ReflectionTestUtils.setField(coinoneMarketListProvider, "coinoneApiUrl", coinoneApiUrl);
        
        // Create mock market info objects
        List<CoinoneMarketInfo> markets = new ArrayList<>();
        
        // Create KRW markets
        CoinoneMarketInfo btcMarket = new CoinoneMarketInfo();
        ReflectionTestUtils.setField(btcMarket, "quoteCurrency", "KRW");
        ReflectionTestUtils.setField(btcMarket, "targetCurrency", "BTC");
        ReflectionTestUtils.setField(btcMarket, "priceUnit", BigDecimal.ONE);
        
        CoinoneMarketInfo ethMarket = new CoinoneMarketInfo();
        ReflectionTestUtils.setField(ethMarket, "quoteCurrency", "KRW");
        ReflectionTestUtils.setField(ethMarket, "targetCurrency", "ETH");
        ReflectionTestUtils.setField(ethMarket, "priceUnit", BigDecimal.ONE);
        
        // Create a non-KRW market
        CoinoneMarketInfo btcUsdtMarket = new CoinoneMarketInfo();
        ReflectionTestUtils.setField(btcUsdtMarket, "quoteCurrency", "USDT");
        ReflectionTestUtils.setField(btcUsdtMarket, "targetCurrency", "BTC");
        ReflectionTestUtils.setField(btcUsdtMarket, "priceUnit", BigDecimal.ONE);
        
        markets.add(btcMarket);
        markets.add(ethMarket);
        markets.add(btcUsdtMarket);
        
        // Create success response
        successResponse = new CoinoneMarketNameData();
        ReflectionTestUtils.setField(successResponse, "result", "success");
        ReflectionTestUtils.setField(successResponse, "errorCode", "0");
        ReflectionTestUtils.setField(successResponse, "serverTime", 1234567890L);
        ReflectionTestUtils.setField(successResponse, "markets", markets);
        
        // Create error response
        errorResponse = new CoinoneMarketNameData();
        ReflectionTestUtils.setField(errorResponse, "result", "error");
        ReflectionTestUtils.setField(errorResponse, "errorCode", "1001");
        ReflectionTestUtils.setField(errorResponse, "serverTime", 1234567890L);
        ReflectionTestUtils.setField(errorResponse, "markets", null);
    }

    @Test
    @DisplayName("티커 포함 마켓 리스트 조회")
    void shouldReturnMarketListWithTicker() {
        // Arrange
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(coinoneApiUrl)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CoinoneMarketNameData.class)).thenReturn(successResponse);

        // Act
        List<String> result = coinoneMarketListProvider.getMarketListWithTicker();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("KRW-BTC", result.get(0));
        assertEquals("KRW-ETH", result.get(1));
    }

    @Test
    @DisplayName("티커 미포함 마켓 리스트 조회")
    void shouldReturnMarketListWithoutTicker() throws IOException {
        // Arrange
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(coinoneApiUrl)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CoinoneMarketNameData.class)).thenReturn(successResponse);

        // Act
        List<String> result = coinoneMarketListProvider.getMarketList();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("BTC", result.get(0));
        assertEquals("ETH", result.get(1));
    }

    @Test
    @DisplayName("티커 포함 마켓 리스트 조회 실패: API 에러")
    void shouldReturnNullWhenApiReturnsError() {
        // Arrange
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(coinoneApiUrl)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CoinoneMarketNameData.class)).thenReturn(errorResponse);

        // Act
        List<String> result = coinoneMarketListProvider.getMarketListWithTicker();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("티커 미포함 마켓 리스트 조회 실패: API 에러")
    void shouldThrowExceptionWhenGetMarketListWithApiError() {
        // Arrange
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(coinoneApiUrl)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CoinoneMarketNameData.class)).thenReturn(errorResponse);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            coinoneMarketListProvider.getMarketList();
        });
    }
}