package unit.kimp.cmc.service;

import kimp.cmc.dao.CmcEntityPreloaderDao;
import kimp.cmc.service.CmcEntityPreloaderService;
import kimp.cmc.service.impl.CmcEntityPreloaderServiceImpl;
import kimp.common.lock.DistributedLockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@DisplayName("CmcEntityPreloaderService 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class CmcEntityPreloaderServiceTest {

    @Mock
    private CmcEntityPreloaderDao cmcEntityPreloaderDao;

    @Mock
    private DistributedLockService distributedLockService;

    private CmcEntityPreloaderService cmcEntityPreloaderService;

    @BeforeEach
    void setup() {
        // 실제 구현체를 사용하여 서비스 객체 생성
        CmcEntityPreloaderServiceImpl serviceImpl = new CmcEntityPreloaderServiceImpl(cmcEntityPreloaderDao);
        // DistributedLockService는 필드 주입이므로 리플렉션 사용
        try {
            java.lang.reflect.Field field = CmcEntityPreloaderServiceImpl.class.getDeclaredField("distributedLockService");
            field.setAccessible(true);
            field.set(serviceImpl, distributedLockService);
        } catch (Exception e) {
            // 테스트 환경에서만 발생할 수 있는 예외 무시
        }
        
        cmcEntityPreloaderService = serviceImpl;
    }

    @Test
    @DisplayName("preloadAllCmcEntities 메서드 테스트 - 분산락 없이 정상 실행")
    void shouldPreloadAllCmcEntitiesWithoutDistributedLock() {
        // Given - 분산락이 없는 경우를 시뮬레이션
        CmcEntityPreloaderServiceImpl serviceWithoutLock = new CmcEntityPreloaderServiceImpl(cmcEntityPreloaderDao);
        
        when(cmcEntityPreloaderDao.findAllCmcCoinsWithAssociations()).thenReturn(Collections.emptyList());
        when(cmcEntityPreloaderDao.findAllCmcExchangesWithAssociations()).thenReturn(Collections.emptyList());
        when(cmcEntityPreloaderDao.findCmcCoinsWithMainnet()).thenReturn(Collections.emptyList());
        when(cmcEntityPreloaderDao.findCmcCoinsWithPlatforms()).thenReturn(Collections.emptyList());

        // When
        serviceWithoutLock.preloadAllCmcEntities();

        // Then - DAO 메서드들이 호출되었는지 확인
        verify(cmcEntityPreloaderDao, times(1)).findAllCmcCoinsWithAssociations();
        verify(cmcEntityPreloaderDao, times(1)).findAllCmcExchangesWithAssociations();
        verify(cmcEntityPreloaderDao, times(1)).findCmcCoinsWithMainnet();
        verify(cmcEntityPreloaderDao, times(1)).findCmcCoinsWithPlatforms();
    }

    @Test
    @DisplayName("preloadAllCmcEntities 메서드 테스트 - 분산락으로 정상 실행")
    void shouldPreloadAllCmcEntitiesWithDistributedLock() {
        // Given
        when(distributedLockService.tryLock(anyString(), anyInt())).thenReturn("test-lock-token");
        when(distributedLockService.releaseLock(anyString(), anyString())).thenReturn(true);
        when(cmcEntityPreloaderDao.findAllCmcCoinsWithAssociations()).thenReturn(Collections.emptyList());
        when(cmcEntityPreloaderDao.findAllCmcExchangesWithAssociations()).thenReturn(Collections.emptyList());
        when(cmcEntityPreloaderDao.findCmcCoinsWithMainnet()).thenReturn(Collections.emptyList());
        when(cmcEntityPreloaderDao.findCmcCoinsWithPlatforms()).thenReturn(Collections.emptyList());

        // When
        cmcEntityPreloaderService.preloadAllCmcEntities();

        // Then
        verify(distributedLockService, times(1)).tryLock(anyString(), anyInt());
        verify(distributedLockService, times(1)).releaseLock(anyString(), anyString());
        verify(cmcEntityPreloaderDao, times(1)).findAllCmcCoinsWithAssociations();
        verify(cmcEntityPreloaderDao, times(1)).findAllCmcExchangesWithAssociations();
    }

    @Test
    @DisplayName("preloadAllCmcEntities 메서드 테스트 - 분산락 획득 실패시 건너뜀")
    void shouldSkipPreloadingWhenDistributedLockFails() {
        // Given
        when(distributedLockService.tryLock(anyString(), anyInt())).thenReturn(null); // 락 획득 실패

        // When
        cmcEntityPreloaderService.preloadAllCmcEntities();

        // Then
        verify(distributedLockService, times(1)).tryLock(anyString(), anyInt());
        verify(distributedLockService, never()).releaseLock(anyString(), anyString());
        verify(cmcEntityPreloaderDao, never()).findAllCmcCoinsWithAssociations(); // 실행되지 않음
    }

    @Test
    @DisplayName("preloadCmcEntitiesForCoins 메서드 테스트 - 정상 호출 확인")
    void shouldPreloadCmcEntitiesForSpecificCoins() {
        // Given
        List<Long> coinIds = Arrays.asList(1L, 2L, 3L);
        when(cmcEntityPreloaderDao.findCmcCoinsWithAssociationsByIds(coinIds)).thenReturn(Collections.emptyList());

        // When
        cmcEntityPreloaderService.preloadCmcEntitiesForCoins(coinIds);

        // Then
        verify(cmcEntityPreloaderDao, times(1)).findCmcCoinsWithAssociationsByIds(coinIds);
    }

    @Test
    @DisplayName("preloadCmcEntitiesForCoins 메서드 테스트 - 빈 리스트일 때 처리")
    void shouldHandleEmptyCoinsListGracefully() {
        // Given
        List<Long> emptyList = Collections.emptyList();

        // When
        cmcEntityPreloaderService.preloadCmcEntitiesForCoins(emptyList);

        // Then - DAO가 호출되지 않아야 함 (early return)
        verify(cmcEntityPreloaderDao, never()).findCmcCoinsWithAssociationsByIds(anyList());
    }

    @Test
    @DisplayName("preloadCmcEntitiesForCoins 메서드 테스트 - null일 때 처리")
    void shouldHandleNullCoinsListGracefully() {
        // Given & When
        cmcEntityPreloaderService.preloadCmcEntitiesForCoins(null);

        // Then - DAO가 호출되지 않아야 함 (early return)
        verify(cmcEntityPreloaderDao, never()).findCmcCoinsWithAssociationsByIds(anyList());
    }

    @Test
    @DisplayName("preloadCmcEntitiesForExchanges 메서드 테스트 - 정상 호출 확인")
    void shouldPreloadCmcEntitiesForSpecificExchanges() {
        // Given
        List<Long> exchangeIds = Arrays.asList(1L, 2L);
        when(cmcEntityPreloaderDao.findCmcExchangesWithAssociationsByIds(exchangeIds)).thenReturn(Collections.emptyList());

        // When
        cmcEntityPreloaderService.preloadCmcEntitiesForExchanges(exchangeIds);

        // Then
        verify(cmcEntityPreloaderDao, times(1)).findCmcExchangesWithAssociationsByIds(exchangeIds);
    }

    @Test
    @DisplayName("preloadCmcEntitiesForExchanges 메서드 테스트 - 빈 리스트일 때 처리")
    void shouldHandleEmptyExchangesListGracefully() {
        // Given
        List<Long> emptyList = Collections.emptyList();

        // When
        cmcEntityPreloaderService.preloadCmcEntitiesForExchanges(emptyList);

        // Then - DAO가 호출되지 않아야 함 (early return)
        verify(cmcEntityPreloaderDao, never()).findCmcExchangesWithAssociationsByIds(anyList());
    }

    @Test
    @DisplayName("preloadCmcEntitiesForExchanges 메서드 테스트 - null일 때 처리")
    void shouldHandleNullExchangesListGracefully() {
        // Given & When
        cmcEntityPreloaderService.preloadCmcEntitiesForExchanges(null);

        // Then - DAO가 호출되지 않아야 함 (early return)
        verify(cmcEntityPreloaderDao, never()).findCmcExchangesWithAssociationsByIds(anyList());
    }
}