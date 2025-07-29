package unit.kimp.market.dao.impl;

import kimp.exchange.entity.Exchange;
import kimp.market.Enum.MarketType;
import kimp.market.dao.impl.CoinExchangeDaoImpl;
import kimp.market.entity.Coin;
import kimp.market.entity.CoinExchange;
import kimp.market.repository.CoinExchangeRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoinExchangeDaoImplTest {

    @Mock
    private CoinExchangeRepository coinExchangeRepository;

    @InjectMocks
    private CoinExchangeDaoImpl coinExchangeDao;

    private Coin testCoin;
    private Exchange testExchange1;
    private Exchange testExchange2;
    private CoinExchange testCoinExchange1;
    private CoinExchange testCoinExchange2;
    private List<CoinExchange> testCoinExchangeList;
    private List<Long> testExchangeIds;

    @BeforeEach
    void setUp() {
        testCoin = new Coin("BTC", "비트코인", "Bitcoin");
        testExchange1 = new Exchange(MarketType.UPBIT, "https://upbit.com");
        ReflectionTestUtils.setField(testExchange1, "id", 1L);
        testExchange2 = new Exchange(MarketType.BINANCE, "https://binance.com");
        ReflectionTestUtils.setField(testExchange2, "id", 2L);

        testCoinExchange1 = new CoinExchange(testCoin, testExchange1);
        testCoinExchange2 = new CoinExchange(testCoin, testExchange2);

        testCoinExchangeList = Arrays.asList(testCoinExchange1, testCoinExchange2);
        testExchangeIds = Arrays.asList(1L, 2L);
    }

    @Test
    @DisplayName("CoinExchange 생성: 저장된 CoinExchange 반환")
    void shouldReturnSavedCoinExchangeWhenCreateCoinExchange() {
        // Arrange
        when(coinExchangeRepository.save(any(CoinExchange.class))).thenReturn(testCoinExchange1);

        // Act
        CoinExchange result = coinExchangeDao.createCoinExchange(testCoinExchange1);

        // Assert
        assertNotNull(result);
        assertEquals(testCoinExchange1, result);
        verify(coinExchangeRepository, times(1)).save(testCoinExchange1);
    }

    @Test
    @DisplayName("코인 ID와 거래소 ID로 CoinExchange 조회: CoinExchange 반환")
    void shouldReturnCoinExchangesWhenFindCoinExchangeWithExchangeByCoinIdAndExchangeIds() {
        // Arrange
        when(coinExchangeRepository.findCoinExchangeWithExchangeByCoinAndExchangeIn(anyLong(), anyList()))
            .thenReturn(testCoinExchangeList);

        // Act
        List<CoinExchange> result = coinExchangeDao.findCoinExchangeWithExchangeByCoinIdAndExchangeIds(1L, testExchangeIds);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testCoinExchangeList, result);
        verify(coinExchangeRepository, times(1)).findCoinExchangeWithExchangeByCoinAndExchangeIn(1L, testExchangeIds);
    }

    @Test
    @DisplayName("CoinExchange 삭제: CoinExchange 삭제")
    void shouldDeleteCoinExchangesWhenDeleteAllByCoinExchanges() {
        // Arrange
        doNothing().when(coinExchangeRepository).deleteAll(anyList());

        // Act
        coinExchangeDao.deleteAllByCoinExchanges(testCoinExchangeList);

        // Assert
        verify(coinExchangeRepository, times(1)).deleteAll(testCoinExchangeList);
    }

    @Test
    @DisplayName("코인 ID로 CoinExchange 조회: CoinExchange 반환")
    void shouldReturnCoinExchangesWhenFindCoinExchangeWithExchangeByCoinId() {
        // Arrange
        when(coinExchangeRepository.findCoinExchangeWithExchangeByCoinId(anyLong()))
            .thenReturn(testCoinExchangeList);

        // Act
        List<CoinExchange> result = coinExchangeDao.findCoinExchangeWithExchangeByCoinId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testCoinExchangeList, result);
        verify(coinExchangeRepository, times(1)).findCoinExchangeWithExchangeByCoinId(1L);
    }

    @Test
    @DisplayName("코인 ID로 CoinExchange 조회: CoinExchange 없을 때 빈 리스트 반환")
    void shouldReturnEmptyListWhenNoCoinExchangesFoundForFindCoinExchangeWithExchangeByCoinId() {
        // Arrange
        when(coinExchangeRepository.findCoinExchangeWithExchangeByCoinId(anyLong()))
            .thenReturn(new ArrayList<>());

        // Act
        List<CoinExchange> result = coinExchangeDao.findCoinExchangeWithExchangeByCoinId(999L);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(coinExchangeRepository, times(1)).findCoinExchangeWithExchangeByCoinId(999L);
    }
}
