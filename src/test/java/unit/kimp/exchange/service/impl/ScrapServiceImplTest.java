package unit.kimp.exchange.service.impl;

import kimp.scrap.component.impl.exchange.ExchangeScrapAbstract;
import kimp.scrap.dto.internal.binance.BinanceNoticeDto;
import kimp.scrap.dto.internal.bithumb.BithumbNoticeDto;
import kimp.scrap.dto.internal.coinone.CoinoneNoticeDto;
import kimp.scrap.dto.internal.upbit.UpbitNoticeDto;
import kimp.exchange.service.ExchangeService;
import kimp.exchange.service.impl.ExchangeNoticePacadeService;
import kimp.exchange.service.impl.ScrapServiceImpl;
import kimp.market.Enum.MarketType;
import kimp.market.controller.MarketInfoStompController;
import kimp.notice.dto.response.NoticeParsedData;
import kimp.notice.dto.response.NoticeDto;
import kimp.notice.service.NoticeService;
import kimp.common.lock.DistributedLockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("ScrapService 구현체 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class ScrapServiceImplTest {

    @Mock
    private ExchangeScrapAbstract<UpbitNoticeDto> upbitScrapComponent;

    @Mock
    private ExchangeScrapAbstract<BithumbNoticeDto> bithumbScrapComponent;

    @Mock
    private ExchangeScrapAbstract<CoinoneNoticeDto> coinoneScrapComponent;

    @Mock
    private ExchangeScrapAbstract<BinanceNoticeDto> binanceScrapComponent;

    @Mock
    private ExchangeService exchangeService;

    @Mock
    private MarketInfoStompController marketInfoStompController;

    @Mock
    private ExchangeNoticePacadeService exchangeNoticePacadeService;

    @Mock
    private NoticeService noticeService;
    
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    
    @Mock
    private SetOperations<String, Object> setOperations;
    
    @Mock
    private DistributedLockService distributedLockService;

    private ScrapServiceImpl scrapService;

    private List<NoticeParsedData> upbitNoticeParsedDataList;
    private List<NoticeParsedData> coinoneNoticeParsedDataList;
    private List<NoticeParsedData> upbitNewNoticeList;
    private List<NoticeParsedData> coinoneNewNoticeList;
    private NoticeDto noticeDto;

    @BeforeEach
    void setup() {
        LocalDateTime fixedDate = LocalDateTime.now().minusDays(1); // 어제 날짜로 설정
        
        upbitNoticeParsedDataList = new ArrayList<>();
        upbitNoticeParsedDataList.add(new NoticeParsedData("Upbit Notice 1", "https://upbit.com/notice/1", fixedDate));
        upbitNoticeParsedDataList.add(new NoticeParsedData("Upbit Notice 2", "https://upbit.com/notice/2", fixedDate));

        coinoneNoticeParsedDataList = new ArrayList<>();
        coinoneNoticeParsedDataList.add(new NoticeParsedData("Coinone Notice 1", "https://coinone.co.kr/notice/1", fixedDate));
        coinoneNoticeParsedDataList.add(new NoticeParsedData("Coinone Notice 2", "https://coinone.co.kr/notice/2", fixedDate));

        upbitNewNoticeList = new ArrayList<>();
        upbitNewNoticeList.add(new NoticeParsedData("Upbit New Notice", "https://upbit.com/notice/new", fixedDate));

        coinoneNewNoticeList = new ArrayList<>();
        coinoneNewNoticeList.add(new NoticeParsedData("Coinone New Notice", "https://coinone.co.kr/notice/new", fixedDate));

        noticeDto = new NoticeDto(1L, MarketType.UPBIT, "Upbit New Notice", "https://upbit.com/notice/new", fixedDate);

        // Create ScrapServiceImpl instance with mocked dependencies
        scrapService = new ScrapServiceImpl(
            upbitScrapComponent,
            bithumbScrapComponent,
            coinoneScrapComponent,
            binanceScrapComponent,
            exchangeNoticePacadeService,
            noticeService,
            marketInfoStompController,
            redisTemplate,
            distributedLockService
        );
    }

    @Test
    @DisplayName("스크랩 서비스 테스트 - Upbit에 새로운 공지사항이 있을 때 (분산 락 적용)")
    void shouldScrapUpbitNoticeDataWithDistributedLock() {
        // Given
        String lockToken = "test-lock-token";
        when(distributedLockService.tryLock(anyString(), anyInt())).thenReturn(lockToken);
        when(distributedLockService.releaseLock(anyString(), anyString())).thenReturn(true);
        
        try {
            when(upbitScrapComponent.parseNoticeData()).thenReturn(upbitNoticeParsedDataList);
        } catch (Exception e) {
            // Won't happen with mock
        }
        when(upbitScrapComponent.getMarketType()).thenReturn(MarketType.UPBIT);
        
        // Redis 기반 로직을 위한 Mock 설정
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(anyString())).thenReturn(Set.of()); // Redis 캐시가 비어있음을 시뮬레이션
        when(noticeService.getRecentNoticeLinks(any(MarketType.class), anyInt())).thenReturn(List.of());
        
        when(noticeService.getNoticeByLink(anyString())).thenReturn(noticeDto);
        when(exchangeNoticePacadeService.createNoticesBulk(any(MarketType.class), anyList())).thenReturn(true);

        // When
        scrapService.scrapUpbitNoticeData();

        // Then - 분산 락 검증
        verify(distributedLockService, times(1)).tryLock(eq("notice-scrape:upbit"), eq(300));
        verify(distributedLockService, times(1)).releaseLock(eq("notice-scrape:upbit"), eq(lockToken));
        
        // 스크래핑 로직 검증
        try {
            verify(upbitScrapComponent, times(1)).parseNoticeData();
        } catch (Exception e) {
            // Mock verification, IOException won't be thrown
        }
        verify(exchangeNoticePacadeService, times(1)).createNoticesBulk(eq(MarketType.UPBIT), anyList());
    }
    
    @Test
    @DisplayName("스크랩 서비스 테스트 - 다른 서버에서 락을 보유 중일 때 건너뛰기")
    void shouldSkipScrapingWhenLockIsHeldByAnotherServer() {
        // Given
        when(distributedLockService.tryLock(anyString(), anyInt())).thenReturn(null); // 락 획득 실패
        when(distributedLockService.getLockOwner(anyString())).thenReturn("other-server-instance");

        // When
        scrapService.scrapUpbitNoticeData();

        // Then - 락 획득 시도는 하지만 실제 스크래핑은 하지 않음
        verify(distributedLockService, times(1)).tryLock(eq("notice-scrape:upbit"), eq(300));
        verify(distributedLockService, times(1)).getLockOwner(eq("notice-scrape:upbit"));
        verify(distributedLockService, never()).releaseLock(anyString(), anyString());
        
        // 스크래핑 관련 작업은 수행되지 않음
        try {
            verify(upbitScrapComponent, never()).parseNoticeData();
        } catch (Exception e) {
            // Mock verification, IOException won't be thrown
        }
        verify(exchangeNoticePacadeService, never()).createNoticesBulk(any(), any());
    }
    
    @Test
    @DisplayName("스크랩 서비스 테스트 - Coinone에 새로운 공지사항이 있을 때 (분산 락 적용)")
    void shouldScrapCoinoneNoticeDataWithDistributedLock() {
        // Given
        String lockToken = "test-lock-token";
        when(distributedLockService.tryLock(anyString(), anyInt())).thenReturn(lockToken);
        when(distributedLockService.releaseLock(anyString(), anyString())).thenReturn(true);
        
        try {
            when(coinoneScrapComponent.parseNoticeData()).thenReturn(coinoneNoticeParsedDataList);
        } catch (Exception e) {
            // Won't happen with mock
        }
        when(coinoneScrapComponent.getMarketType()).thenReturn(MarketType.COINONE);
        
        // Redis 기반 로직을 위한 Mock 설정
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(anyString())).thenReturn(Set.of()); // Redis 캐시가 비어있음을 시뮬레이션
        when(noticeService.getRecentNoticeLinks(any(MarketType.class), anyInt())).thenReturn(List.of());
        
        NoticeDto coinoneNoticeDto = new NoticeDto(2L, MarketType.COINONE, "Coinone New Notice", "https://coinone.co.kr/notice/new", LocalDateTime.now());
        when(noticeService.getNoticeByLink(anyString())).thenReturn(coinoneNoticeDto);
        when(exchangeNoticePacadeService.createNoticesBulk(any(MarketType.class), anyList())).thenReturn(true);

        // When
        scrapService.scrapCoinoneNoticeData();

        // Then - 분산 락 검증
        verify(distributedLockService, times(1)).tryLock(eq("notice-scrape:coinone"), eq(300));
        verify(distributedLockService, times(1)).releaseLock(eq("notice-scrape:coinone"), eq(lockToken));
        
        // 스크래핑 로직 검증
        try {
            verify(coinoneScrapComponent, times(1)).parseNoticeData();
        } catch (Exception e) {
            // Mock verification, IOException won't be thrown
        }
        verify(exchangeNoticePacadeService, times(1)).createNoticesBulk(eq(MarketType.COINONE), anyList());
    }

    @Test
    @DisplayName("스크랩 서비스 테스트 - 새로운 공지사항이 없을 때 (Redis 기반)")
    void shouldNotProcessWhenNoNewNoticeExists() {
        // Given
        String lockToken = "test-lock-token";
        when(distributedLockService.tryLock(anyString(), anyInt())).thenReturn(lockToken);
        when(distributedLockService.releaseLock(anyString(), anyString())).thenReturn(true);
        
        try {
            when(upbitScrapComponent.parseNoticeData()).thenReturn(upbitNoticeParsedDataList);
        } catch (Exception e) {
            // Won't happen with mock
        }
        when(upbitScrapComponent.getMarketType()).thenReturn(MarketType.UPBIT);
        
        // Redis에 기존 공지사항이 있어서 새로운 공지사항이 없다고 시뮬레이션
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        Set<Object> existingUrls = Set.of("https://upbit.com/notice/1", "https://upbit.com/notice/2");
        when(setOperations.members(anyString())).thenReturn(existingUrls);

        // When
        scrapService.scrapUpbitNoticeData();

        // Then - 분산 락은 정상적으로 실행
        verify(distributedLockService, times(1)).tryLock(eq("notice-scrape:upbit"), eq(300));
        verify(distributedLockService, times(1)).releaseLock(eq("notice-scrape:upbit"), eq(lockToken));
        
        // 스크래핑은 실행되지만 새로운 공지사항이 없으므로 DB 저장 및 WebSocket 전송 없음
        try {
            verify(upbitScrapComponent, times(1)).parseNoticeData();
        } catch (Exception e) {
            // Mock verification, IOException won't be thrown
        }
        verify(exchangeNoticePacadeService, never()).createNoticesBulk(any(MarketType.class), anyList());
        try {
            verify(marketInfoStompController, never()).sendNewNotice(any(NoticeDto.class));
        } catch (Exception e) {
            // Mock verification, IOException won't be thrown
        }
    }
}