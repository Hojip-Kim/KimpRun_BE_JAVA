package unit.kimp.market.controller;

import kimp.exception.response.ApiResponse;
import kimp.market.controller.MarketInfoController;
import kimp.market.dto.market.response.MarketDollarResponseDto;
import kimp.market.dto.market.response.MarketTetherResponseDto;
import kimp.market.service.MarketInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MarketInfoControllerTest {

    @Mock
    private MarketInfoService marketInfoService;

    @InjectMocks
    private MarketInfoController marketInfoController;

    private double dollarRate;
    private double tetherRate;

    @BeforeEach
    void setUp() {
        // Setup test data
        dollarRate = 1350.75;
        tetherRate = 1345.50;
    }

    @Test
    @DisplayName("getDollar should return dollar rate")
    void getDollar_ShouldReturnDollarRate() {
        // Arrange
        when(marketInfoService.getDollarKRW()).thenReturn(dollarRate);

        // Act
        ApiResponse<MarketDollarResponseDto> response = marketInfoController.getDollar();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(dollarRate, response.getData().getDollar());
        verify(marketInfoService, times(1)).getDollarKRW();
    }

    @Test
    @DisplayName("getTether should return tether rate")
    void getTether_ShouldReturnTetherRate() {
        // Arrange
        when(marketInfoService.getTetherKRW()).thenReturn(tetherRate);

        // Act
        ApiResponse<MarketTetherResponseDto> response = marketInfoController.getTether();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(tetherRate, response.getData().getTether());
        verify(marketInfoService, times(1)).getTetherKRW();
    }
}