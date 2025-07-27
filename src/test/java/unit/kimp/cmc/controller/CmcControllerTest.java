package unit.kimp.cmc.controller;

import kimp.cmc.component.CoinMarketCapComponent;
import kimp.cmc.controller.CmcController;
import kimp.cmc.dto.common.coin.CmcApiDataDto;
import kimp.cmc.dto.common.coin.CmcCoinInfoDataMapDto;
import kimp.cmc.dto.common.coin.CmcCoinMapDataDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDetailMapDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDto;
import kimp.cmc.service.CmcCoinManageService;
import kimp.exception.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CmcControllerTest {

    @InjectMocks
    private CmcController cmcController;

    @Mock
    private CmcCoinManageService cmcCoinManageService;

    @Mock
    private CoinMarketCapComponent coinMarketCapComponent;

    private List<CmcCoinMapDataDto> mockCoinMapDataList;
    private List<CmcApiDataDto> mockApiDataList;
    private CmcCoinInfoDataMapDto mockCoinInfoDataMapDto;
    private List<CmcExchangeDto> mockExchangeDtoList;
    private CmcExchangeDetailMapDto mockExchangeDetailMapDto;

    @BeforeEach
    void setUp() {
        mockCoinMapDataList = new ArrayList<>();
        mockCoinMapDataList.add(new CmcCoinMapDataDto());

        mockApiDataList = new ArrayList<>();
        mockApiDataList.add(new CmcApiDataDto());

        mockCoinInfoDataMapDto = new CmcCoinInfoDataMapDto();

        mockExchangeDtoList = new ArrayList<>();
        mockExchangeDtoList.add(new CmcExchangeDto());

        mockExchangeDetailMapDto = new CmcExchangeDetailMapDto();
    }

    @Test
    @DisplayName("코인 맵 데이터 조회 테스트")
    void shouldReturnCoinMapData() {
        // Arrange
        when(coinMarketCapComponent.getCoinMapFromCMC(anyInt(), anyInt())).thenReturn(mockCoinMapDataList);

        // Act
        ApiResponse<List<CmcCoinMapDataDto>> response = cmcController.getCoinMapFromCMCTest();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals(mockCoinMapDataList, response.getData());
    }

    @Test
    @DisplayName("최신 코인 정보 조회 테스트")
    void shouldReturnLatestCoinInfo() {
        // Arrange
        when(coinMarketCapComponent.getLatestCoinInfoFromCMC(anyInt(), anyInt())).thenReturn(mockApiDataList);

        // Act
        ApiResponse<List<CmcApiDataDto>> response = cmcController.getLatestCoinInfoFromCMCTest();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals(mockApiDataList, response.getData());
    }

    @Test
    @DisplayName("CMC 코인 정보 조회 테스트")
    void shouldReturnCmcCoinInfos() {
        // Arrange
        when(coinMarketCapComponent.getCmcCoinInfos(anyList())).thenReturn(mockCoinInfoDataMapDto);

        // Act
        ApiResponse<CmcCoinInfoDataMapDto> response = cmcController.getCmcCoinInfosTest();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals(mockCoinInfoDataMapDto, response.getData());
    }

    @Test
    @DisplayName("거래소 맵 조회 테스트")
    void shouldReturnExchangeMap() {
        // Arrange
        when(coinMarketCapComponent.getExchangeMap(anyInt(), anyInt())).thenReturn(mockExchangeDtoList);

        // Act
        ApiResponse<List<CmcExchangeDto>> response = cmcController.getExchangeMapTest();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals(mockExchangeDtoList, response.getData());
    }

    @Test
    @DisplayName("거래소 정보 조회 테스트")
    void shouldReturnExchangeInfo() {
        // Arrange
        when(coinMarketCapComponent.getExchangeInfo(anyList())).thenReturn(mockExchangeDetailMapDto);

        // Act
        ApiResponse<CmcExchangeDetailMapDto> response = cmcController.getExchangeInfo();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals(mockExchangeDetailMapDto, response.getData());
    }
}
