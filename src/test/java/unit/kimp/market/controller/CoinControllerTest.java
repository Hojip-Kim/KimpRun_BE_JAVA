package unit.kimp.market.controller;

import kimp.exception.response.ApiResponse;
import kimp.market.controller.CoinController;
import kimp.market.dto.coin.request.*;
import kimp.market.dto.coin.response.CoinResponseDto;
import kimp.market.dto.coin.response.CoinResponseWithMarketTypeDto;
import kimp.market.service.CoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoinControllerTest {

    @Mock
    private CoinService coinService;

    @InjectMocks
    private CoinController coinController;

    private CoinResponseDto coinResponseDto;
    private List<CoinResponseDto> coinResponseDtoList;
    private CreateCoinDto createCoinDto;
    private UpdateCoinDto updateCoinDto;
    private UpdateContentCoinDto updateContentCoinDto;
    private AdjustExchangeCoinDto adjustExchangeCoinDto;
    private DeleteCoinDto deleteCoinDto;

    @BeforeEach
    void setUp() {
        coinResponseDto = new CoinResponseDto(1L, "BTC", "비트코인", "Bitcoin");
        
        coinResponseDtoList = new ArrayList<>();
        coinResponseDtoList.add(coinResponseDto);
        coinResponseDtoList.add(new CoinResponseDto(2L, "ETH", "이더리움", "Ethereum"));
        
        createCoinDto = mock(CreateCoinDto.class);
        updateCoinDto = mock(UpdateCoinDto.class);
        updateContentCoinDto = mock(UpdateContentCoinDto.class);
        adjustExchangeCoinDto = mock(AdjustExchangeCoinDto.class);
        deleteCoinDto = mock(DeleteCoinDto.class);
    }

    @Test
    @DisplayName("ID로 코인 조회")
    void shouldReturnCoinWhenFound() {
        // Arrange
        when(coinService.getCoinByID(1L)).thenReturn(mock(CoinResponseWithMarketTypeDto.class));

        // Act
        ApiResponse<CoinResponseDto> response = coinController.getCoinById(1L);

        // Assert
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        verify(coinService, times(1)).getCoinByID(1L);
    }

    @Test
    @DisplayName("getCoinById는 null일 때에도 코인 데이터를 반환해야 함.")
    void getCoinById_ShouldReturnSuccessResponse_EvenWhenNull() {
        // Arrange
        when(coinService.getCoinByID(999L)).thenReturn(null);

        // Act
        ApiResponse<CoinResponseDto> response = coinController.getCoinById(999L);

        // Assert
        assertEquals(200, response.getStatus());
        assertNull(response.getData());
        verify(coinService, times(1)).getCoinByID(999L);
    }

    @Test
    @DisplayName("getCoinByExchangeId는 발견 시 코인을 반환해야 함.")
    void getCoinByExchangeId_ShouldReturnCoins_WhenFound() {
        // Arrange
        when(coinService.getCoinsByExchangeId(1L)).thenReturn(coinResponseDtoList);

        // Act
        ApiResponse<List<CoinResponseDto>> response = coinController.getCoinByExchangeId(1L);

        // Assert
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        verify(coinService, times(1)).getCoinsByExchangeId(1L);
    }

    @Test
    @DisplayName("getCoinByExchangeId는 null일 때에도 성공 응답을 반환해야 함.")
    void getCoinByExchangeId_ShouldReturnSuccessResponse_EvenWhenNull() {
        // Arrange
        when(coinService.getCoinsByExchangeId(999L)).thenReturn(null);

        // Act
        ApiResponse<List<CoinResponseDto>> response = coinController.getCoinByExchangeId(999L);

        // Assert
        assertEquals(200, response.getStatus());
        assertNull(response.getData());
        verify(coinService, times(1)).getCoinsByExchangeId(999L);
    }

    @Test
    @DisplayName("createCoin은 성공 시 생성된 코인을 반환해야 함.")
    void createCoin_ShouldReturnCreatedCoin_WhenSuccessful() {
        // Arrange
        when(coinService.createCoin(any(CreateCoinDto.class))).thenReturn(coinResponseDto);

        // Act
        ApiResponse<CoinResponseDto> response = coinController.createCoin(createCoinDto);

        // Assert
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals("BTC", response.getData().getSymbol());
        verify(coinService, times(1)).createCoin(createCoinDto);
    }

    @Test
    @DisplayName("createCoin은 null일 때에도 성공 응답을 반환해야 함.")
    void createCoin_ShouldReturnSuccessResponse_EvenWhenNull() {
        // Arrange
        when(coinService.createCoin(any(CreateCoinDto.class))).thenReturn(null);

        // Act
        ApiResponse<CoinResponseDto> response = coinController.createCoin(createCoinDto);

        // Assert
        assertEquals(200, response.getStatus());
        assertNull(response.getData());
        verify(coinService, times(1)).createCoin(createCoinDto);
    }

    @Test
    @DisplayName("updateAllCoinData는 성공 시 업데이트된 코인을 반환해야 함.")
    void updateAllCoinData_ShouldReturnUpdatedCoin_WhenSuccessful() {
        // Arrange
        when(coinService.updateCoin(any(UpdateCoinDto.class))).thenReturn(coinResponseDto);

        // Act
        ApiResponse<CoinResponseDto> response = coinController.updateAllCoinData(updateCoinDto);

        // Assert
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals("BTC", response.getData().getSymbol());
        verify(coinService, times(1)).updateCoin(updateCoinDto);
    }

    @Test
    @DisplayName("updateAllCoinData는 null인 경우에도 성공 응답을 반환해야 함.")
    void updateAllCoinData_ShouldReturnSuccessResponse_EvenWhenNull() {
        // Arrange
        when(coinService.updateCoin(any(UpdateCoinDto.class))).thenReturn(null);

        // Act
        ApiResponse<CoinResponseDto> response = coinController.updateAllCoinData(updateCoinDto);

        // Assert
        assertEquals(200, response.getStatus());
        assertNull(response.getData());
        verify(coinService, times(1)).updateCoin(updateCoinDto);
    }

    @Test
    @DisplayName("updateCoinContent는 성공 시 업데이트된 코인을 반환해야 함.")
    void updateCoinContent_ShouldReturnUpdatedCoin_WhenSuccessful() {
        // Arrange
        when(coinService.updateContentCoin(any(UpdateContentCoinDto.class))).thenReturn(coinResponseDto);

        // Act
        ApiResponse<CoinResponseDto> response = coinController.updateCoinContent(updateContentCoinDto);

        // Assert
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals("BTC", response.getData().getSymbol());
        verify(coinService, times(1)).updateContentCoin(updateContentCoinDto);
    }

    @Test
    @DisplayName("updateCoinContent는 null인 경우에도 성공 응답을 반환해야 함.")
    void updateCoinContent_ShouldReturnSuccessResponse_EvenWhenNull() {
        // Arrange
        when(coinService.updateContentCoin(any(UpdateContentCoinDto.class))).thenReturn(null);

        // Act
        ApiResponse<CoinResponseDto> response = coinController.updateCoinContent(updateContentCoinDto);

        // Assert
        assertEquals(200, response.getStatus());
        assertNull(response.getData());
        verify(coinService, times(1)).updateContentCoin(updateContentCoinDto);
    }

    @Test
    @DisplayName("addExchangeCoin은 성공하면 업데이트된 코인을 반환해야 함.")
    void addExchangeCoin_ShouldReturnUpdatedCoin_WhenSuccessful() {
        // Arrange
        when(coinService.addExchangeCoin(any(AdjustExchangeCoinDto.class))).thenReturn(coinResponseDto);

        // Act
        ApiResponse<CoinResponseDto> response = coinController.addExchangeCoin(adjustExchangeCoinDto);

        // Assert
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals("BTC", response.getData().getSymbol());
        verify(coinService, times(1)).addExchangeCoin(adjustExchangeCoinDto);
    }

    @Test
    @DisplayName("addExchangeCoin은 null일 때에도 성공 응답을 반환해야 함.")
    void addExchangeCoin_ShouldReturnSuccessResponse_EvenWhenNull() {
        // Arrange
        when(coinService.addExchangeCoin(any(AdjustExchangeCoinDto.class))).thenReturn(null);

        // Act
        ApiResponse<CoinResponseDto> response = coinController.addExchangeCoin(adjustExchangeCoinDto);

        // Assert
        assertEquals(200, response.getStatus());
        assertNull(response.getData());
        verify(coinService, times(1)).addExchangeCoin(adjustExchangeCoinDto);
    }

    @Test
    @DisplayName("delistExchangeCoin은 성공하면 true를 반환해야 함.")
    void delistExchangeCoin_ShouldReturnTrue_WhenSuccessful() {
        // Arrange
        doNothing().when(coinService).deleteExchangeCoin(any(AdjustExchangeCoinDto.class));

        // Act
        ApiResponse<Boolean> response = coinController.delistExchangeCoin(adjustExchangeCoinDto);

        // Assert
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(coinService, times(1)).deleteExchangeCoin(adjustExchangeCoinDto);
    }

    @Test
    @DisplayName("deleteCoin은 성공하면 true를 반환해야 함.")
    void deleteCoin_ShouldReturnTrue_WhenSuccessful() {
        // Arrange
        doNothing().when(coinService).deleteCoin(any(DeleteCoinDto.class));

        // Act
        ApiResponse<Boolean> response = coinController.deleteCoin(deleteCoinDto);

        // Assert
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(coinService, times(1)).deleteCoin(deleteCoinDto);
    }
}