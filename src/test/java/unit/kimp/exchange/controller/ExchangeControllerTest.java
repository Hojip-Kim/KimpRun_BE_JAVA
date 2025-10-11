package unit.kimp.exchange.controller;

import kimp.exception.response.ApiResponse;
import kimp.exchange.controller.ExchangeController;
import kimp.exchange.dto.exchange.request.ExchangeCreateRequestDto;
import kimp.exchange.dto.exchange.response.ExchangeDto;
import kimp.exchange.service.ExchangeService;
import kimp.market.Enum.MarketType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeControllerTest {

    @Mock
    private ExchangeService exchangeService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ExchangeController exchangeController;

    private ExchangeDto exchangeDto1;
    private ExchangeDto exchangeDto2;
    private List<ExchangeDto> exchangeDtoList;
    private ExchangeCreateRequestDto createRequestDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        exchangeDto1 = new ExchangeDto(1L, MarketType.UPBIT, "https://upbit.com");
        exchangeDto2 = new ExchangeDto(2L, MarketType.BINANCE, "https://binance.com");
        exchangeDtoList = Arrays.asList(exchangeDto1, exchangeDto2);
        createRequestDto = new ExchangeCreateRequestDto(MarketType.COINONE, "https://coinone.co.kr");
    }

    @Test
    @DisplayName("모든 거래소 조회")
    void shouldReturnAllExchanges() {
        // Arrange
        when(exchangeService.getExchanges()).thenReturn(exchangeDtoList);

        // Act
        ApiResponse<List<ExchangeDto>> response = exchangeController.getExchange();

        // Assert
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals(exchangeDtoList, response.getData());
        verify(exchangeService, times(1)).getExchanges();
    }

    @Test
    @DisplayName("ID로 특정 거래소 조회")
    void shouldReturnSpecificExchangeWhenGetWithId() {
        // Arrange
        when(exchangeService.getExchange(any())).thenReturn(exchangeDto1);

        // Act
        ApiResponse<ExchangeDto> response = exchangeController.getExchange(1L);

        // Assert
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(exchangeDto1, response.getData());
        assertEquals(MarketType.UPBIT, response.getData().getExchangeName());
        assertEquals("https://upbit.com", response.getData().getLink());
        verify(exchangeService, times(1)).getExchange(any());
    }

    @Test
    @DisplayName("새로운 거래소 생성")
    void shouldCreateAndReturnNewExchange() {
        // Arrange
        ExchangeDto newExchangeDto = new ExchangeDto(3L, MarketType.COINONE, "https://coinone.co.kr");
        when(exchangeService.createExchange(any())).thenReturn(newExchangeDto);

        // Act
        ApiResponse<ExchangeDto> response = exchangeController.createExchange(userDetails, createRequestDto);

        // Assert
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(newExchangeDto, response.getData());
        assertEquals(MarketType.COINONE, response.getData().getExchangeName());
        assertEquals("https://coinone.co.kr", response.getData().getLink());
        verify(exchangeService, times(1)).createExchange(any());
    }
}