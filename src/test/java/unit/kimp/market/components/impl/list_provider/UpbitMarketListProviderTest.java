package unit.kimp.market.components.impl.list_provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.common.MarketCommonMethod;
import kimp.market.components.impl.list_provider.UpbitMarketListProvider;
import kimp.market.dto.market.common.UpbitMarketNameData;
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
public class UpbitMarketListProviderTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MarketCommonMethod marketCommonMethod;

    @InjectMocks
    private UpbitMarketListProvider upbitMarketListProvider;

    private List<String> mockMarketList;
    private String upbitApiUrl = "https://api.upbit.com/v1/market/all";
    private String upbitTickerUrl = "https://api.upbit.com/v1/ticker";

    @BeforeEach
    void setUp() {
        // Set up mock data
        mockMarketList = Arrays.asList("KRW-BTC", "KRW-ETH", "KRW-XRP");
        
        // Set the API URLs using ReflectionTestUtils since they're normally injected via @Value
        ReflectionTestUtils.setField(upbitMarketListProvider, "upbitApiUrl", upbitApiUrl);
        ReflectionTestUtils.setField(upbitMarketListProvider, "upbitTickerUrl", upbitTickerUrl);
    }

    @Test
    @DisplayName("티커 포함 마켓 리스트 조회")
    void shouldReturnMarketListWithTicker() throws IOException {
        // Arrange
        when(marketCommonMethod.getMarketListByURLAndStartWith(
                eq(upbitApiUrl), 
                eq("KRW-"), 
                eq("getMarket"), 
                eq(UpbitMarketNameData[].class)))
            .thenReturn(mockMarketList);

        // Act
        List<String> result = upbitMarketListProvider.getMarketListWithTicker();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("KRW-BTC", result.get(0));
        assertEquals("KRW-ETH", result.get(1));
        assertEquals("KRW-XRP", result.get(2));
    }

    @Test
    @DisplayName("티커 미포함 마켓 리스트 조회")
    void shouldReturnMarketListWithoutTicker() throws IOException {
        // Arrange
        when(marketCommonMethod.getMarketListByURLAndStartWith(
                eq(upbitApiUrl), 
                eq("KRW-"), 
                eq("getMarket"), 
                eq(UpbitMarketNameData[].class)))
            .thenReturn(mockMarketList);

        // Act
        List<String> result = upbitMarketListProvider.getMarketList();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("BTC", result.get(0));
        assertEquals("ETH", result.get(1));
        assertEquals("XRP", result.get(2));
    }

    @Test
    @DisplayName("빈 마켓 리스트 조회")
    void shouldReturnEmptyListWhenNoMarkets() throws IOException {
        // Arrange
        when(marketCommonMethod.getMarketListByURLAndStartWith(
                eq(upbitApiUrl), 
                eq("KRW-"), 
                eq("getMarket"), 
                eq(UpbitMarketNameData[].class)))
            .thenReturn(Arrays.asList());

        // Act
        List<String> result = upbitMarketListProvider.getMarketList();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}