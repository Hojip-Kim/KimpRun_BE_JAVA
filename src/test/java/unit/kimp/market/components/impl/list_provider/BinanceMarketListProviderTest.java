package unit.kimp.market.components.impl.list_provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.common.MarketCommonMethod;
import kimp.market.components.impl.list_provider.BinanceMarketListProvider;
import kimp.market.dto.market.common.BinanceMarketData;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BinanceMarketListProviderTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MarketCommonMethod marketCommonMethod;

    @InjectMocks
    private BinanceMarketListProvider binanceMarketListProvider;

    private List<String> mockMarketList;
    private String binanceApiUrl = "https://api.binance.com/api/v3/ticker/24hr";

    @BeforeEach
    void setUp() {
        mockMarketList = Arrays.asList("BTCUSDT", "ETHUSDT", "XRPUSDT");
        
        ReflectionTestUtils.setField(binanceMarketListProvider, "binanceApiUrl", binanceApiUrl);
    }

    @Test
    @DisplayName("티커 포함 마켓 리스트 조회 (USDT)")
    void shouldReturnMarketListWithUsdtSuffix() throws IOException {
        // Arrange
        when(marketCommonMethod.getMarketListByURLAndEndWith(
                eq(binanceApiUrl), 
                eq("USDT"), 
                eq("getSymbol"), 
                eq(BinanceMarketData[].class)))
            .thenReturn(mockMarketList);

        // Act
        List<String> result = binanceMarketListProvider.getMarketListWithTicker();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("BTCUSDT", result.get(0));
        assertEquals("ETHUSDT", result.get(1));
        assertEquals("XRPUSDT", result.get(2));
    }

    @Test
    @DisplayName("티커 미포함 마켓 리스트 조회 (USDT)")
    void shouldReturnMarketListWithoutUsdtSuffix() throws IOException {
        // Arrange
        when(marketCommonMethod.getMarketListByURLAndEndWith(
                eq(binanceApiUrl), 
                eq("USDT"), 
                eq("getSymbol"), 
                eq(BinanceMarketData[].class)))
            .thenReturn(mockMarketList);

        // Act
        List<String> result = binanceMarketListProvider.getMarketList();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("BTC", result.get(0));
        assertEquals("ETH", result.get(1));
        assertEquals("XRP", result.get(2));
    }

    @Test
    @DisplayName("빈 마켓 리스트 조회 (USDT)")
    void shouldHandleEmptyMarketList() throws IOException {
        // Arrange
        when(marketCommonMethod.getMarketListByURLAndEndWith(
                eq(binanceApiUrl), 
                eq("USDT"), 
                eq("getSymbol"), 
                eq(BinanceMarketData[].class)))
            .thenReturn(Arrays.asList());

        // Act
        List<String> result = binanceMarketListProvider.getMarketList();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}