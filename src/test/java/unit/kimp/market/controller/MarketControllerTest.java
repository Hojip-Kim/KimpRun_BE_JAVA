package unit.kimp.market.controller;

import kimp.exception.KimprunException;
import kimp.exception.response.ApiResponse;
import kimp.market.Enum.MarketType;
import kimp.market.controller.MarketController;
import kimp.market.dto.coin.common.market.MarketDto;
import kimp.market.dto.coin.response.CoinMarketDto;
import kimp.market.dto.market.response.CombinedMarketDataList;
import kimp.market.dto.market.response.CombinedMarketList;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.service.MarketService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MarketControllerTest {

    @Mock
    private MarketService marketService;

    @InjectMocks
    private MarketController marketController;

    private CombinedMarketList combinedMarketList;
    private MarketDataList<MarketDto> marketDataList;
    private CombinedMarketDataList combinedMarketDataList;
    private List<CoinMarketDto> firstMarketList;
    private List<CoinMarketDto> secondMarketList;
    private List<MarketDto> marketDtoList;

    @BeforeEach
    void setUp() {
        // Setup test data
        firstMarketList = Arrays.asList(new CoinMarketDto(1L, "BTC"), new CoinMarketDto(2L, "ETH"), new CoinMarketDto(3L, "DOGE"));
        secondMarketList = Arrays.asList(new CoinMarketDto(1L, "BTC"), new CoinMarketDto(2L, "ETH"), new CoinMarketDto(3L, "DOGE"));
        combinedMarketList = new CombinedMarketList(firstMarketList, secondMarketList);

        marketDtoList = Arrays.asList(
            mock(MarketDto.class),
            mock(MarketDto.class)
        );
        marketDataList = new MarketDataList<>(marketDtoList);

        combinedMarketDataList = new CombinedMarketDataList(marketDtoList, marketDtoList);
    }

    @Test
    @DisplayName("통합 마켓 리스트 조회")
    void shouldReturnCombinedMarketList() throws IOException {
        // Arrange
        when(marketService.getMarketListFromDatabase(MarketType.UPBIT, MarketType.BINANCE)).thenReturn(combinedMarketList);

        // Act
        ApiResponse<CombinedMarketList> response = marketController.getMarketList(MarketType.UPBIT, MarketType.BINANCE);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(firstMarketList, response.getData().getFirstMarketList());
        assertEquals(secondMarketList, response.getData().getSecondMarketList());
        verify(marketService, times(1)).getMarketListFromDatabase(MarketType.UPBIT, MarketType.BINANCE);
    }

    @Test
    @DisplayName("통합 마켓 리스트 조회 실패: 파라미터 null")
    void shouldThrowExceptionWhenGetMarketListWithNullParams() {
        // Act & Assert
        assertThrows(KimprunException.class, () -> marketController.getMarketList(null, MarketType.BINANCE));
        assertThrows(KimprunException.class, () -> marketController.getMarketList(MarketType.UPBIT, null));
        assertThrows(KimprunException.class, () -> marketController.getMarketList(null, null));
        verify(marketService, never()).getMarketListFromDatabase(any(), any());
    }

    @Test
    @DisplayName("단일 마켓 데이터 조회")
    void shouldReturnMarketDataList() throws IOException {
        // Arrange
        when(marketService.getMarketDataList(MarketType.UPBIT)).thenReturn(marketDataList);

        // Act
        ApiResponse<MarketDataList> response = marketController.getFirstMarketDatas(MarketType.UPBIT);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(marketDtoList, response.getData().getMarketDataList());
        verify(marketService, times(1)).getMarketDataList(MarketType.UPBIT);
    }

    @Test
    @DisplayName("단일 마켓 데이터 조회 실패: 파라미터 null")
    void shouldThrowExceptionWhenGetFirstMarketDatasWithNullParam() throws IOException {
        // Act & Assert
        assertThrows(KimprunException.class, () -> marketController.getFirstMarketDatas(null));
        verify(marketService, never()).getMarketDataList(any());
    }

    @Test
    @DisplayName("통합 마켓 데이터 조회")
    void shouldReturnCombinedMarketDataList() throws IOException {
        // Arrange
        when(marketService.getCombinedMarketDataList(MarketType.UPBIT, MarketType.BINANCE)).thenReturn(combinedMarketDataList);

        // Act
        ApiResponse<CombinedMarketDataList> response = marketController.getCombinedMarketDatas(MarketType.UPBIT, MarketType.BINANCE);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(marketDtoList, response.getData().getFirstMarketList());
        assertEquals(marketDtoList, response.getData().getSecondMarketList());
        verify(marketService, times(1)).getCombinedMarketDataList(MarketType.UPBIT, MarketType.BINANCE);
    }

    @Test
    @DisplayName("통합 마켓 데이터 조회 실패: 파라미터 null")
    void shouldThrowExceptionWhenGetCombinedMarketDatasWithNullParams() {
        // Act & Assert
        assertThrows(KimprunException.class, () -> marketController.getCombinedMarketDatas(null, MarketType.BINANCE));
        assertThrows(KimprunException.class, () -> marketController.getCombinedMarketDatas(MarketType.UPBIT, null));
        assertThrows(KimprunException.class, () -> marketController.getCombinedMarketDatas(null, null));
        verify(marketService, never()).getCombinedMarketDataList(any(), any());
    }

    @Test
    @DisplayName("테스트용 통합 마켓 데이터 조회")
    void shouldReturnCombinedMarketDataListForTest() throws IOException {
        // Arrange
        when(marketService.getCombinedMarketDataList(MarketType.UPBIT, MarketType.BINANCE)).thenReturn(combinedMarketDataList);

        // Act
        ApiResponse<CombinedMarketDataList> response = marketController.test();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(marketDtoList, response.getData().getFirstMarketList());
        assertEquals(marketDtoList, response.getData().getSecondMarketList());
        verify(marketService, times(1)).getCombinedMarketDataList(MarketType.UPBIT, MarketType.BINANCE);
    }
}
