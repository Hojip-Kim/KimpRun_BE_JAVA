package unit.kimp.cmc.controller;

import kimp.cmc.component.CoinMarketCapComponent;
import kimp.cmc.controller.CmcController;
import kimp.cmc.dto.internal.coin.CmcApiDataDto;
import kimp.cmc.dto.internal.coin.CmcCoinInfoDataMapDto;
import kimp.cmc.dto.internal.coin.CmcCoinMapDataDto;
import kimp.cmc.dto.internal.exchange.CmcExchangeDetailMapDto;
import kimp.cmc.dto.internal.exchange.CmcExchangeDto;
import kimp.cmc.dto.response.CmcCoinResponseDto;
import kimp.cmc.service.CmcCoinManageService;
import kimp.cmc.vo.GetCoinDataByCoinIdVo;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private CmcCoinResponseDto mockCmcCoinResponseDto;

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
        
        mockCmcCoinResponseDto = new CmcCoinResponseDto(
            "BTC", "Bitcoin", "logo.png", "21000000", "19000000", "21000000","2373668200507",
            List.of("explorer1.com", "explorer2.com"), List.of("Ethereum (ETH)", "BSC (BNB)"), 
            1, java.time.LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("코인 데이터 조회 테스트")
    void shouldReturnCoinData() {
        // Arrange
        Long coinId = 1L;
        GetCoinDataByCoinIdVo vo = new GetCoinDataByCoinIdVo(coinId);
        when(cmcCoinManageService.findCmcCoinDataByCoinId(any(GetCoinDataByCoinIdVo.class))).thenReturn(mockCmcCoinResponseDto);

        // Act
        ApiResponse<CmcCoinResponseDto> response = cmcController.getCoinDataByCoinId(coinId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals(mockCmcCoinResponseDto, response.getData());
    }

}
