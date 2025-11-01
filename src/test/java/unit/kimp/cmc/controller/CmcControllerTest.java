package unit.kimp.cmc.controller;

import kimp.cmc.controller.CmcController;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CmcControllerTest {

    @InjectMocks
    private CmcController cmcController;

    @Mock
    private CmcCoinManageService cmcCoinManageService;

    private CmcCoinResponseDto mockCmcCoinResponseDto;

    @BeforeEach
    void setUp() {
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
