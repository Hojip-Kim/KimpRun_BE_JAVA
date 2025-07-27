package unit.kimp.exchange.dao.impl;

import kimp.exception.KimprunException;
import kimp.exchange.dao.impl.ExchangeDaoImpl;
import kimp.exchange.entity.Exchange;
import kimp.exchange.repository.ExchangeRepository;
import kimp.market.Enum.MarketType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeDaoImplTest {

    @Mock
    private ExchangeRepository exchangeRepository;

    @InjectMocks
    private ExchangeDaoImpl exchangeDao;

    private Exchange exchange1;
    private Exchange exchange2;
    private List<Exchange> exchangeList;
    private List<Long> exchangeIds;
    private List<MarketType> marketTypes;

    @BeforeEach
    void setUp() {
        // Setup test data
        exchange1 = new Exchange(MarketType.UPBIT, "https://upbit.com");
        ReflectionTestUtils.setField(exchange1, "id", 1L);
        
        exchange2 = new Exchange(MarketType.BINANCE, "https://binance.com");
        ReflectionTestUtils.setField(exchange2, "id", 2L);
        
        exchangeList = Arrays.asList(exchange1, exchange2);
        exchangeIds = Arrays.asList(1L, 2L);
        marketTypes = Arrays.asList(MarketType.UPBIT, MarketType.BINANCE);
    }

    @Test
    @DisplayName("ID로 거래소 조회: 발견 시 거래소 반환")
    void shouldReturnExchangeWhenFoundById() {
        // Arrange
        when(exchangeRepository.findById(anyLong())).thenReturn(Optional.of(exchange1));

        // Act
        Exchange result = exchangeDao.getExchangeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(MarketType.UPBIT, result.getMarket());
        assertEquals("https://upbit.com", result.getLink());
        verify(exchangeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("ID로 거래소 조회: 거래소 없을 때 예외 발생")
    void shouldThrowExceptionWhenExchangeNotFoundById() {
        // Arrange
        when(exchangeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(KimprunException.class, () -> exchangeDao.getExchangeById(999L));
        verify(exchangeRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("모든 거래소 조회: 모든 거래소 반환")
    void shouldReturnAllExchanges() {
        // Arrange
        when(exchangeRepository.findAll()).thenReturn(exchangeList);

        // Act
        List<Exchange> result = exchangeDao.getExchanges();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(exchangeList, result);
        verify(exchangeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("모든 거래소 조회: 거래소 없을 때 예외 발생")
    void shouldThrowExceptionWhenNoExchangesFound() {
        // Arrange
        when(exchangeRepository.findAll()).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(KimprunException.class, () -> exchangeDao.getExchanges());
        verify(exchangeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("거래소 생성: 이미 존재할 때 기존 거래소 반환")
    void shouldReturnExistingExchangeWhenAlreadyExists() {
        // Arrange
        Exchange newExchange = new Exchange(MarketType.UPBIT, "https://upbit.com");
        when(exchangeRepository.findExchangeByMarket(MarketType.UPBIT)).thenReturn(exchange1);

        // Act
        Exchange result = exchangeDao.createExchange(newExchange);

        // Assert
        assertNotNull(result);
        assertEquals(exchange1, result);
        verify(exchangeRepository, times(1)).findExchangeByMarket(MarketType.UPBIT);
        verify(exchangeRepository, never()).save(any(Exchange.class));
    }

    @Test
    @DisplayName("거래소 생성: 존재하지 않을 때 새 거래소 생성 및 반환")
    void shouldCreateAndReturnNewExchangeWhenDoesNotExist() {
        // Arrange
        Exchange newExchange = new Exchange(MarketType.COINONE, "https://coinone.co.kr");
        when(exchangeRepository.findExchangeByMarket(MarketType.COINONE)).thenReturn(null);
        when(exchangeRepository.save(any(Exchange.class))).thenReturn(newExchange);

        // Act
        Exchange result = exchangeDao.createExchange(newExchange);

        // Assert
        assertNotNull(result);
        assertEquals(newExchange, result);
        verify(exchangeRepository, times(1)).findExchangeByMarket(MarketType.COINONE);
        verify(exchangeRepository, times(1)).save(newExchange);
    }

    @Test
    @DisplayName("MarketType으로 거래소 조회: 발견 시 거래소 반환")
    void shouldReturnExchangeWhenFoundByMarketType() {
        // Arrange
        when(exchangeRepository.findExchangeByMarket(MarketType.UPBIT)).thenReturn(exchange1);

        // Act
        Exchange result = exchangeDao.getExchangeByMarketType(MarketType.UPBIT);

        // Assert
        assertNotNull(result);
        assertEquals(exchange1, result);
        assertEquals(MarketType.UPBIT, result.getMarket());
        verify(exchangeRepository, times(1)).findExchangeByMarket(MarketType.UPBIT);
    }

    @Test
    @DisplayName("MarketType으로 거래소 조회: 거래소 없을 때 예외 발생")
    void shouldThrowExceptionWhenExchangeNotFoundByMarketType() {
        // Arrange
        when(exchangeRepository.findExchangeByMarket(MarketType.COINONE)).thenReturn(null);

        // Act & Assert
        assertThrows(KimprunException.class, () -> exchangeDao.getExchangeByMarketType(MarketType.COINONE));
        verify(exchangeRepository, times(1)).findExchangeByMarket(MarketType.COINONE);
    }

    @Test
    @DisplayName("MarketTypes로 거래소 조회: 발견 시 거래소들 반환")
    void shouldReturnExchangesWhenFoundByMarketTypes() {
        // Arrange
        when(exchangeRepository.findByMarketIn(marketTypes)).thenReturn(exchangeList);

        // Act
        List<Exchange> result = exchangeDao.getExchangeByMarketTypes(marketTypes);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(exchangeList, result);
        verify(exchangeRepository, times(1)).findByMarketIn(marketTypes);
    }

    @Test
    @DisplayName("MarketTypes로 거래소 조회: 거래소 없을 때 예외 발생")
    void shouldThrowExceptionWhenNoExchangesFoundByMarketTypes() {
        // Arrange
        when(exchangeRepository.findByMarketIn(any())).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(KimprunException.class, () -> exchangeDao.getExchangeByMarketTypes(Arrays.asList(MarketType.COINONE)));
        verify(exchangeRepository, times(1)).findByMarketIn(any());
    }

    @Test
    @DisplayName("ID들로 거래소 조회: 발견 시 거래소들 반환")
    void shouldReturnExchangesWhenFoundByIds() {
        // Arrange
        when(exchangeRepository.findByIdIn(exchangeIds)).thenReturn(exchangeList);

        // Act
        List<Exchange> result = exchangeDao.getExchangesByIds(exchangeIds);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(exchangeList, result);
        verify(exchangeRepository, times(1)).findByIdIn(exchangeIds);
    }

    @Test
    @DisplayName("ID들로 거래소 조회: 거래소 없을 때 예외 발생")
    void shouldThrowExceptionWhenNoExchangesFoundByIds() {
        // Arrange
        when(exchangeRepository.findByIdIn(any())).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(KimprunException.class, () -> exchangeDao.getExchangesByIds(Arrays.asList(999L)));
        verify(exchangeRepository, times(1)).findByIdIn(any());
    }

    @Test
    @DisplayName("ID들로 거래소와 코인 거래소 조회: 발견 시 거래소들 반환")
    void shouldReturnExchangesAndCoinExchangesWhenFoundByIds() {
        // Arrange
        when(exchangeRepository.findExchangesWithCoinExchangesAndCoinByIds(anyLong(), any())).thenReturn(exchangeList);

        // Act
        List<Exchange> result = exchangeDao.getExchangesAndCoinExchangesByIds(1L, exchangeIds);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(exchangeList, result);
        verify(exchangeRepository, times(1)).findExchangesWithCoinExchangesAndCoinByIds(1L, exchangeIds);
    }

    @Test
    @DisplayName("ID들로 거래소와 코인 거래소 조회: 거래소 없을 때 빈 리스트 반환")
    void shouldReturnEmptyListWhenNoExchangesFoundForExchangesAndCoinExchangesByIds() {
        // Arrange
        when(exchangeRepository.findExchangesWithCoinExchangesAndCoinByIds(anyLong(), any())).thenReturn(new ArrayList<>());

        // Act
        List<Exchange> result = exchangeDao.getExchangesAndCoinExchangesByIds(1L, exchangeIds);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(exchangeRepository, times(1)).findExchangesWithCoinExchangesAndCoinByIds(1L, exchangeIds);
    }
}