package kimp.exchange.service.impl;

import kimp.common.lock.DistributedLockService;
import kimp.notice.dto.response.NoticeDto;
import kimp.notice.dto.response.NoticeParsedData;
import kimp.scrap.component.ExchangeScrap;
import kimp.scrap.dto.internal.binance.BinanceNoticeDto;
import kimp.scrap.dto.internal.bithumb.BithumbNoticeDto;
import kimp.scrap.dto.internal.coinone.CoinoneNoticeDto;
import kimp.scrap.dto.internal.upbit.UpbitNoticeDto;
import kimp.notice.service.NoticeService;
import kimp.exchange.service.ScrapService;
import kimp.market.controller.MarketInfoStompController;
import kimp.market.Enum.MarketType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScrapServiceImpl implements ScrapService {

    private final ExchangeScrap<UpbitNoticeDto> upbitScrapComponent;
    private final ExchangeScrap<BithumbNoticeDto> bithumbScrapComponent;
    private final ExchangeScrap<CoinoneNoticeDto> coinoneScrapComponent;
    private final ExchangeScrap<BinanceNoticeDto> binanceScrapComponent;
    private final ExchangeNoticePacadeService exchangeNoticePacadeService;
    private final NoticeService noticeService;
    private final MarketInfoStompController marketInfoStompController;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DistributedLockService distributedLockService;
    
    // 분산 락 키 정의
    private static final String UPBIT_SCRAPE_LOCK_KEY = "notice-scrape:upbit";
    private static final String BINANCE_SCRAPE_LOCK_KEY = "notice-scrape:binance";
    private static final String BITHUMB_SCRAPE_LOCK_KEY = "notice-scrape:bithumb";
    private static final String COINONE_SCRAPE_LOCK_KEY = "notice-scrape:coinone";
    private static final int SCRAPE_LOCK_TTL_SECONDS = 300; // 5분 (스크래핑 작업 최대 예상 시간)

    public ScrapServiceImpl(
            ExchangeScrap<UpbitNoticeDto> upbitScrapComponent,
            ExchangeScrap<BithumbNoticeDto> bithumbScrapComponent,
            ExchangeScrap<CoinoneNoticeDto> coinoneScrapComponent,
            ExchangeScrap<BinanceNoticeDto> binanceScrapComponent,
            ExchangeNoticePacadeService exchangeNoticePacadeService, 
            NoticeService noticeService,
            MarketInfoStompController marketInfoStompController,
            RedisTemplate<String, Object> redisTemplate,
            DistributedLockService distributedLockService) {
        this.upbitScrapComponent = upbitScrapComponent;
        this.bithumbScrapComponent = bithumbScrapComponent;
        this.coinoneScrapComponent = coinoneScrapComponent;
        this.binanceScrapComponent = binanceScrapComponent;
        this.exchangeNoticePacadeService = exchangeNoticePacadeService;
        this.noticeService = noticeService;
        this.marketInfoStompController = marketInfoStompController;
        this.redisTemplate = redisTemplate;
        this.distributedLockService = distributedLockService;
    }

    @Scheduled(fixedRate = 10000, scheduler = "upbitNoticeTaskScheduler")
    public void scrapUpbitNoticeData() {
        executeWithDistributedLock(UPBIT_SCRAPE_LOCK_KEY, "UPBIT", () -> {
            try {
                processExchangeNotices("UPBIT", upbitScrapComponent);
            } catch (Exception e) {
                log.error("UPBIT 공지사항 스크래핑 중 오류 발생", e);
                throw new RuntimeException(e);
            }
        });
    }

    @Scheduled(fixedRate = 10000, scheduler = "binanceNoticeTaskScheduler")
    public void scrapBinanceNoticeData() {
        executeWithDistributedLock(BINANCE_SCRAPE_LOCK_KEY, "BINANCE", () -> {
            try {
                processExchangeNotices("BINANCE", binanceScrapComponent);
            } catch (Exception e) {
                log.error("BINANCE 공지사항 스크래핑 중 오류 발생", e);
                throw new RuntimeException(e);
            }
        });
    }

    @Scheduled(fixedRate = 10000, scheduler = "bithumbNoticeTaskScheduler")
    public void scrapBithumbNoticeData() {
        executeWithDistributedLock(BITHUMB_SCRAPE_LOCK_KEY, "BITHUMB", () -> {
            try {
                processExchangeNotices("BITHUMB", bithumbScrapComponent);
            } catch (Exception e) {
                log.error("BITHUMB 공지사항 스크래핑 중 오류 발생", e);
                throw new RuntimeException(e);
            }
        });
    }

    @Scheduled(fixedRate = 10000, scheduler = "coinoneNoticeTaskScheduler")
    public void scrapCoinoneNoticeData() {
        executeWithDistributedLock(COINONE_SCRAPE_LOCK_KEY, "COINONE", () -> {
            try {
                processExchangeNotices("COINONE", coinoneScrapComponent);
            } catch (Exception e) {
                log.error("COINONE 공지사항 스크래핑 중 오류 발생", e);
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * 특정 거래소 컴포넌트를 통한 공지사항 처리
     */
    private <T> void processExchangeNotices(String exchangeName, ExchangeScrap<T> scrapComponent) {
        try {
            // 1. Python 서비스를 통해 최신 공지사항 파싱
            List<NoticeParsedData> currentNotices = scrapComponent.parseNoticeData();
            
            if (currentNotices == null || currentNotices.isEmpty()) {
                log.warn("{} 거래소에서 공지사항을 가져오지 못했습니다", exchangeName);
                return;
            }
            
            // 1-1. 현재 공지사항들을 최신순으로 정렬하고 최근 30일 데이터만 필터링
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            currentNotices = currentNotices.stream()
                .filter(notice -> notice.getDate().isAfter(thirtyDaysAgo)) // 최근 30일 데이터만
                .sorted((a, b) -> b.getDate().compareTo(a.getDate())) // 최신순 정렬
                .toList();

            // 2. Redis 기반 새로운 공지사항 감지
            List<NoticeParsedData> newNotices = findNewNoticesFromRedis(scrapComponent.getMarketType(), currentNotices);
            boolean hasUpdate = !newNotices.isEmpty();
            
            if (hasUpdate) {

                // 4-1. 새로운 공지사항들도 최신순으로 정렬
                if (newNotices != null && !newNotices.isEmpty()) {
                    newNotices = newNotices.stream()
                        .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                        .toList();

                    // 4-2. 가장 최신 공지사항 정보 로깅
                    NoticeParsedData latestNotice = newNotices.get(0);
                    
                    // 5. 메모리 상태 업데이트 (Redis 저장 제거)
                    scrapComponent.setNewParsedData(currentNotices);
                    scrapComponent.setNewNotice(newNotices);
                    
                    // 6. DB에 새로운 공지사항 저장 (오래된 순서부터 저장하여 최신 것이 마지막에 오도록)
                    List<NoticeParsedData> orderedForSaving = newNotices.stream()
                        .sorted((a, b) -> a.getDate().compareTo(b.getDate())) // 오래된 순서로 재정렬
                        .toList();
                    
                    boolean saved = exchangeNoticePacadeService.createNoticesBulk(
                        scrapComponent.getMarketType(), 
                        orderedForSaving // 오래된 순서로 정렬된 공지사항들 (최신 것이 마지막에 저장됨)
                    );
                    
                    if (saved) {

                        // 6-1. Redis 캐시 동기화 - 새로운 공지사항들을 Redis에 추가
                        updateRedisCache(scrapComponent.getMarketType(), newNotices);
                        
                        // 6-2. 저장된 새로운 공지사항들 상세 로깅 (저장 순서대로)
                        for (int i = 0; i < orderedForSaving.size(); i++) {
                            NoticeParsedData notice = orderedForSaving.get(i);
                            boolean isLatest = notice.equals(latestNotice);
                        }
                        
                        // 7. WebSocket으로 실시간 전송 (최신순으로 - 사용자가 최신 것을 먼저 봐야 함)
                        sendNewNoticesViaWebSocket(newNotices, exchangeName);
                        
                    } else {
                        log.warn("{} 거래소 공지사항 저장 실패", exchangeName);
                    }
                } else {
                    log.debug("{} 거래소 새로운 공지사항 추출 결과 없음", exchangeName);
                    // 새로운 공지사항이 없어도 현재 상태는 업데이트
                    scrapComponent.setNewParsedData(currentNotices);
                }
            } else {
                log.debug("{} 거래소에 변경사항 없음", exchangeName);
            }
            
        } catch (Exception e) {
            log.error("{} 거래소 공지사항 처리 중 오류 발생: {}", exchangeName, e.getMessage(), e);
        }
    }
    
    /**
     * Redis 기반 공지사항 감지 로직
     * Redis에서 URL 기반으로 비교하여 새로운 공지사항 감지
     */
    private List<NoticeParsedData> findNewNoticesFromRedis(MarketType marketType, List<NoticeParsedData> currentNotices) {
        try {
            String redisKey = "notices:" + marketType.name().toLowerCase();
            
            // Redis에서 기존 공지사항 URL들을 가져오기
            Set<Object> existingUrls = redisTemplate.opsForSet().members(redisKey);
            Set<String> existingUrlStrings = existingUrls != null ? 
                existingUrls.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toSet()) : 
                new HashSet<>();

            // Redis 캐시가 비어있으면 DB에서 초기화
            if (existingUrlStrings.isEmpty()) {
                log.warn("Redis 캐시가 비어있음. DB에서 초기화를 진행합니다.");
                initializeRedisCache(marketType);
                
                // 다시 Redis에서 가져오기
                existingUrls = redisTemplate.opsForSet().members(redisKey);
                existingUrlStrings = existingUrls != null ? 
                    existingUrls.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .collect(Collectors.toSet()) : 
                    new HashSet<>();
                log.info("Redis 초기화 후 {} 거래소의 공지사항 URL {} 개", marketType, existingUrlStrings.size());
            }
            
            // 새로운 공지사항 찾기 (final 변수 사용)
            final Set<String> finalExistingUrlStrings = existingUrlStrings;
            List<NoticeParsedData> newNotices = currentNotices.stream()
                .filter(notice -> !finalExistingUrlStrings.contains(notice.getAlink()))
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .toList();
            
            return newNotices;
            
        } catch (Exception e) {
            log.error("Redis 기반 새로운 공지사항 감지 실패: {} - {}", marketType, e.getMessage());
            
            // Redis 실패 시 DB 기반 백업 로직 사용
            log.warn("DB 기반 백업 로직으로 전환");
            return findNewNoticesFromDB(marketType, currentNotices);
        }
    }
    
    /**
     * Redis 캐시 초기화 - DB에서 기존 공지사항 URL들을 가져와서 Redis에 저장
     */
    private void initializeRedisCache(MarketType marketType) {
        try {
            String redisKey = "notices:" + marketType.name().toLowerCase();
            
            // DB에서 해당 거래소의 모든 공지사항 URL 가져오기
            List<String> existingUrls = noticeService.getRecentNoticeLinks(marketType, 1000); // 최근 1000개
            
            if (!existingUrls.isEmpty()) {
                // Redis Set에 URL들 저장
                redisTemplate.opsForSet().add(redisKey, existingUrls.toArray());
                
                // 7일 TTL 설정
                redisTemplate.expire(redisKey, 7, TimeUnit.DAYS);
                
                log.info("Redis 캐시 초기화 완료: {} 거래소 - {} 개 URL 저장", marketType, existingUrls.size());
            } else {
                log.warn("DB에서 {} 거래소의 공지사항 URL을 가져오지 못했습니다", marketType);
            }
            
        } catch (Exception e) {
            log.error("Redis 캐시 초기화 실패: {} - {}", marketType, e.getMessage());
        }
    }
    
    /**
     * Redis 캐시 업데이트 - 새로운 공지사항 URL들을 Redis에 추가
     */
    private void updateRedisCache(MarketType marketType, List<NoticeParsedData> newNotices) {
        try {
            if (newNotices == null || newNotices.isEmpty()) {
                return;
            }
            
            String redisKey = "notices:" + marketType.name().toLowerCase();
            
            // 새로운 공지사항들의 URL을 Redis Set에 추가
            String[] newUrls = newNotices.stream()
                .map(NoticeParsedData::getAlink)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
            
            if (newUrls.length > 0) {
                redisTemplate.opsForSet().add(redisKey, (Object[]) newUrls);
                
                // TTL 갱신 (7일)
                redisTemplate.expire(redisKey, 7, TimeUnit.DAYS);
            }
            
        } catch (Exception e) {
            log.error("Redis 캐시 업데이트 실패: {} - {}", marketType, e.getMessage());
        }
    }
    
    /**
     * 기존 DB 기반 공지사항 감지 로직 (Redis 실패 시 백업용)
     */
    private List<NoticeParsedData> findNewNoticesFromDB(MarketType marketType, List<NoticeParsedData> currentNotices) {
        try {
            // 1. DB에서 해당 거래소의 모든 공지사항을 가져오기
            List<NoticeDto> allDbNotices = noticeService.getAllNoticesByMarketType(marketType);

            List<NoticeParsedData> newOrUpdatedNotices = new ArrayList<>();
            
            for (NoticeParsedData currentNotice : currentNotices) {
                // 2. link로 기존 공지사항 찾기
                NoticeDto existingNotice = allDbNotices.stream()
                    .filter(dbNotice -> dbNotice.getUrl().equals(currentNotice.getAlink()))
                    .findFirst()
                    .orElse(null);
                
                if (existingNotice != null) {
                    // 3. 있으면 date와 대조해서 달라진게있으면 업데이트 (분 단위까지만 비교)
                    LocalDateTime dbDate = existingNotice.getCreatedAt().withSecond(0).withNano(0);
                    LocalDateTime scrapDate = currentNotice.getDate().withSecond(0).withNano(0);

                    if (!dbDate.equals(scrapDate)) {
                        // 날짜 업데이트
                        noticeService.updateNoticeDate(existingNotice.getId(), currentNotice.getDate());
                        newOrUpdatedNotices.add(currentNotice); // 업데이트된 공지사항도 웹소켓 송신 대상
                    }
                } else {
                    // 4. 없으면 아예 새로운 공지사항
                    newOrUpdatedNotices.add(currentNotice);
                }
            }
            
            return newOrUpdatedNotices.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate())) // 최신순 정렬
                .toList();
                
        } catch (Exception e) {
            log.error("DB 기반 새로운 공지사항 감지 실패: {} - {}", marketType, e.getMessage());
            
            // 실패 시 링크 기반 백업 로직 사용
            log.warn("링크 기반 백업 로직으로 전환");
            return findNewNoticesByLinks(marketType, currentNotices);
        }
    }
    
    /**
     * 링크 기반 백업 로직 - DB 날짜 비교 실패 시 사용
     */
    private List<NoticeParsedData> findNewNoticesByLinks(MarketType marketType, List<NoticeParsedData> currentNotices) {
        try {
            List<String> existingLinks = noticeService.getRecentNoticeLinks(marketType, 100);
            
            List<NoticeParsedData> newNotices = currentNotices.stream()
                .filter(notice -> !existingLinks.contains(notice.getAlink()))
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .toList();
            
            return newNotices;
        } catch (Exception e) {
            log.error("링크 기반 백업 로직도 실패: {}", e.getMessage());
            // 최종 백업: 모든 현재 공지사항을 새로운 것으로 간주 (안전장치)
            return currentNotices.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .toList();
        }
    }
    
    /**
     * 분산 락을 적용한 스크래핑 작업 실행
     * 분산 환경에서 여러 서버 중 하나만 특정 거래소 스크래핑을 수행하도록 보장
     */
    private void executeWithDistributedLock(String lockKey, String exchangeName, Runnable task) {
        String lockToken = distributedLockService.tryLock(lockKey, SCRAPE_LOCK_TTL_SECONDS);
        
        if (lockToken == null) {
            String currentOwner = distributedLockService.getLockOwner(lockKey);
            return;
        }
        
        try {
            log.debug("{} 공지사항 스크래핑 시작 - 분산 락 획득", exchangeName);
            task.run();
            
        } catch (Exception e) {
            log.error("{} 공지사항 스크래핑 중 오류 발생", exchangeName, e);
            
        } finally {
            // 락 해제
            if (distributedLockService.releaseLock(lockKey, lockToken)) {
                log.debug("🔓 {} 스크래핑 분산 락 해제 완료", exchangeName);
            } else {
                log.warn("⚠️ {} 스크래핑 분산 락 해제 실패 - 이미 만료되었을 수 있습니다", exchangeName);
            }
        }
    }
    
    /**
     * WebSocket을 통한 실시간 공지사항 전송
     */
    private void sendNewNoticesViaWebSocket(List<NoticeParsedData> newNotices, String exchangeName) {
        for (int i = 0; i < newNotices.size(); i++) {
            NoticeParsedData noticeData = newNotices.get(i);
            try {
                // 링크를 통해 DB에서 저장된 공지사항 조회
                NoticeDto noticeDto = noticeService.getNoticeByLink(noticeData.getAlink());
                if (noticeDto != null) {
                    marketInfoStompController.sendNewNotice(noticeDto);
                } else {
                    log.warn("WebSocket 전송 실패 - DB에서 공지사항을 찾을 수 없음: {} - {}",
                            exchangeName, noticeData.getTitle());
                }
            } catch (Exception e) {
                log.warn("WebSocket 전송 실패: {} - {} (오류: {})",
                        exchangeName, noticeData.getTitle(), e.getMessage());
            }
        }
        
        log.info("{} WebSocket 전송 완료", exchangeName);
    }
}