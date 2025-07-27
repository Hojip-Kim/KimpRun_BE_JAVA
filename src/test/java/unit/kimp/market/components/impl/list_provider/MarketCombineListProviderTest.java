package unit.kimp.market.components.impl.list_provider;

import kimp.market.components.impl.list_provider.BinanceMarketListProvider;
import kimp.market.components.impl.list_provider.MarketCombineListProvider;
import kimp.market.components.impl.list_provider.UpbitMarketListProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MarketCombineListProviderTest {

    @Mock
    private UpbitMarketListProvider upbitMarketListProvider;

    @Mock
    private BinanceMarketListProvider binanceMarketListProvider;

    @InjectMocks
    private MarketCombineListProvider marketCombineListProvider;

    private List<String> upbitMarketList;
    private List<String> binanceMarketList;

    @BeforeEach
    void setUp() {
        // Set up mock data
        upbitMarketList = Arrays.asList("BTC", "ETH", "XRP", "ADA", "DOT");
        binanceMarketList = Arrays.asList("BTC", "ETH", "XRP", "BNB", "SOL");
    }

    @Test
    @DisplayName("마켓 리스트 조회")
    void shouldReturnEmptyList() throws IOException {
        // Act
        List<String> result = marketCombineListProvider.getMarketList();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("티커 포함 마켓 리스트 조회")
    void shouldReturnEmptyListWithTicker() throws IOException {
        // Act
        List<String> result = marketCombineListProvider.getMarketListWithTicker();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("마켓 결합 리스트 조회")
    void shouldReturnIntersectionOfTwoLists() throws IOException {
        // Act
        List<String> result = marketCombineListProvider.getMarketCombineList(upbitMarketList, binanceMarketList);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("BTC"));
        assertTrue(result.contains("ETH"));
        assertTrue(result.contains("XRP"));
        assertFalse(result.contains("ADA"));
        assertFalse(result.contains("DOT"));
        assertFalse(result.contains("BNB"));
        assertFalse(result.contains("SOL"));
    }

    @Test
    @DisplayName("마켓 결합 리스트 조회: 공통 요소 없음")
    void shouldReturnEmptyListWhenNoCommonElements() throws IOException {
        // Arrange
        List<String> list1 = Arrays.asList("A", "B", "C");
        List<String> list2 = Arrays.asList("D", "E", "F");

        // Act
        List<String> result = marketCombineListProvider.getMarketCombineList(list1, list2);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("업비트 마켓 리스트 조회")
    void shouldReturnUpbitMarketList() throws IOException {
        // Arrange
        when(upbitMarketListProvider.getMarketList()).thenReturn(upbitMarketList);

        // Act
        List<String> result = marketCombineListProvider.getUpbitMarketList();

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(upbitMarketList, result);
    }

    @Test
    @DisplayName("바이낸스 마켓 리스트 조회")
    void shouldReturnBinanceMarketList() throws IOException {
        // Arrange
        when(binanceMarketListProvider.getMarketList()).thenReturn(binanceMarketList);

        // Act
        List<String> result = marketCombineListProvider.getBinanceMarketList();

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(binanceMarketList, result);
    }
}