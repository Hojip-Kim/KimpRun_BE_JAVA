package unit.kimp.market.dao.impl;

import kimp.market.Enum.MarketType;
import kimp.market.dao.impl.CoinDaoImpl;
import kimp.market.entity.Coin;
import kimp.market.entity.CoinExchange;
import kimp.market.repository.CoinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoinDaoImplTest {

    @Mock
    private CoinRepository coinRepository;

    @InjectMocks
    private CoinDaoImpl coinDao;

    private Coin testCoin;
    private List<Coin> testCoinList;
    private List<CoinExchange> testCoinExchanges;
    private List<MarketType> testMarketTypes;

    @BeforeEach
    void setUp() {
        // Set up test data
        testCoin = new Coin("BTC", "비트코인", "Bitcoin");
        testCoinList = Arrays.asList(
            testCoin,
            new Coin("ETH", "이더리움", "Ethereum")
        );
        testCoinExchanges = new ArrayList<>();
        testMarketTypes = Arrays.asList(MarketType.UPBIT, MarketType.BINANCE);
    }

    @Test
    @DisplayName("ID로 코인 조회: 발견 시 코인 반환")
    void shouldReturnCoinWhenFoundById() {
        // Arrange
        when(coinRepository.findById(anyLong())).thenReturn(Optional.of(testCoin));

        // Act
        Coin result = coinDao.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("BTC", result.getSymbol());
        assertEquals("비트코인", result.getName());
        assertEquals("Bitcoin", result.getEnglishName());
        verify(coinRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("ID로 코인 조회: 코인 없을 때 예외 발생")
    void shouldThrowExceptionWhenCoinNotFoundById() {
        // Arrange
        when(coinRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> coinDao.findById(999L));
        verify(coinRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("거래소 ID로 코인 조회: 발견 시 코인 반환")
    void shouldReturnCoinsWhenFoundByExchangeId() {
        // Arrange
        when(coinRepository.findCoinsByExchange(anyLong())).thenReturn(testCoinList);

        // Act
        List<Coin> result = coinDao.getCoinsByExchangeId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("BTC", result.get(0).getSymbol());
        assertEquals("ETH", result.get(1).getSymbol());
        verify(coinRepository, times(1)).findCoinsByExchange(1L);
    }

    @Test
    @DisplayName("거래소 ID로 코인 조회: 코인 없을 때 예외 발생")
    void shouldThrowExceptionWhenNoCoinsFoundByExchangeId() {
        // Arrange
        when(coinRepository.findCoinsByExchange(anyLong())).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> coinDao.getCoinsByExchangeId(999L));
        verify(coinRepository, times(1)).findCoinsByExchange(999L);
    }

    @Test
    @DisplayName("심볼로 코인과 거래소 조회: 발견 시 코인 반환")
    void shouldReturnCoinsWithExchangesWhenFoundBySymbols() {
        // Arrange
        List<String> symbols = Arrays.asList("BTC", "ETH");
        when(coinRepository.findCoinWithExchangesBySymbols(symbols)).thenReturn(testCoinList);

        // Act
        List<Coin> result = coinDao.findWithExchangesBySymbols(symbols);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("BTC", result.get(0).getSymbol());
        assertEquals("ETH", result.get(1).getSymbol());
        verify(coinRepository, times(1)).findCoinWithExchangesBySymbols(symbols);
    }

    @Test
    @DisplayName("심볼로 코인 조회: 발견 시 코인 반환")
    void shouldReturnCoinWhenFoundBySymbol() {
        // Arrange
        when(coinRepository.findBySymbol("BTC")).thenReturn(testCoin);

        // Act
        Coin result = coinDao.getCoinBySymbol("BTC");

        // Assert
        assertNotNull(result);
        assertEquals("BTC", result.getSymbol());
        assertEquals("비트코인", result.getName());
        assertEquals("Bitcoin", result.getEnglishName());
        verify(coinRepository, times(1)).findBySymbol("BTC");
    }

    @Test
    @DisplayName("심볼로 코인 조회: 코인 없을 때 예외 발생")
    void shouldThrowExceptionWhenCoinNotFoundBySymbol() {
        // Arrange
        when(coinRepository.findBySymbol("UNKNOWN")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> coinDao.getCoinBySymbol("UNKNOWN"));
        verify(coinRepository, times(1)).findBySymbol("UNKNOWN");
    }

    @Test
    @DisplayName("코인 대량 생성: 성공 시 코인 반환")
    void shouldReturnCoinsWhenCreateCoinBulkSuccessful() {
        // Arrange
        when(coinRepository.saveAll(any())).thenReturn(testCoinList);

        // Act
        List<Coin> result = coinDao.createCoinBulk(testCoinList);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("BTC", result.get(0).getSymbol());
        assertEquals("ETH", result.get(1).getSymbol());
        verify(coinRepository, times(1)).saveAll(testCoinList);
    }

    @Test
    @DisplayName("코인 대량 생성: 저장 실패 시 예외 발생")
    void shouldThrowExceptionWhenCreateCoinBulkFails() {
        // Arrange
        when(coinRepository.saveAll(any())).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> coinDao.createCoinBulk(testCoinList));
        verify(coinRepository, times(1)).saveAll(testCoinList);
    }

    @Test
    @DisplayName("코인 생성: 성공 시 코인 반환")
    void shouldReturnCoinWhenCreateCoinSuccessful() {
        // Arrange
        when(coinRepository.save(any(Coin.class))).thenReturn(testCoin);

        // Act
        Coin result = coinDao.createCoin("BTC", "비트코인", "Bitcoin");

        // Assert
        assertNotNull(result);
        assertEquals("BTC", result.getSymbol());
        assertEquals("비트코인", result.getName());
        assertEquals("Bitcoin", result.getEnglishName());
        verify(coinRepository, times(1)).save(any(Coin.class));
    }

    @Test
    @DisplayName("코인 내용 업데이트: 성공 시 업데이트된 코인 반환")
    void shouldReturnUpdatedCoinWhenUpdateContentCoinSuccessful() {
        // Arrange
        String newContent = "New content for Bitcoin";
        when(coinRepository.findById(1L)).thenReturn(Optional.of(testCoin));

        // Act
        Coin result = coinDao.updateContentCoin(1L, newContent);

        // Assert
        assertNotNull(result);
        assertEquals(newContent, result.getContent());
        verify(coinRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("코인 내용 업데이트: 코인 없을 때 예외 발생")
    void shouldThrowExceptionWhenCoinNotFoundForUpdateContentCoin() {
        // Arrange
        when(coinRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> coinDao.updateContentCoin(999L, "New content"));
        verify(coinRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("코인 ID로 마켓 타입 조회: 발견 시 마켓 타입 반환")
    void shouldReturnMarketTypesWhenFoundByCoinId() {
        // Arrange
        when(coinRepository.findMarketTypesByCoinId(1L)).thenReturn(testMarketTypes);

        // Act
        List<MarketType> result = coinDao.findMarketTypesByCoinId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(MarketType.UPBIT, result.get(0));
        assertEquals(MarketType.BINANCE, result.get(1));
        verify(coinRepository, times(1)).findMarketTypesByCoinId(1L);
    }

    @Test
    @DisplayName("코인 ID로 마켓 타입 조회: 마켓 타입 없을 때 예외 발생")
    void shouldThrowExceptionWhenNoMarketTypesFoundByCoinId() {
        // Arrange
        when(coinRepository.findMarketTypesByCoinId(999L)).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> coinDao.findMarketTypesByCoinId(999L));
        verify(coinRepository, times(1)).findMarketTypesByCoinId(999L);
    }
}