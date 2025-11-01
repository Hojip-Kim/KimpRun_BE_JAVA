package kimp.exchange.service.impl;

import kimp.common.redis.constant.RedisChannelType;
import kimp.common.redis.constant.RedisKeyType;
import kimp.common.redis.pubsub.publisher.RedisMessagePublisher;
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
import kimp.telegram.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;
import java.util.*;
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
    private final RedisMessagePublisher redisMessagePublisher;
    private final TelegramService telegramService;

    public ScrapServiceImpl(
            ExchangeScrap<UpbitNoticeDto> upbitScrapComponent,
            ExchangeScrap<BithumbNoticeDto> bithumbScrapComponent,
            ExchangeScrap<CoinoneNoticeDto> coinoneScrapComponent,
            ExchangeScrap<BinanceNoticeDto> binanceScrapComponent,
            ExchangeNoticePacadeService exchangeNoticePacadeService,
            NoticeService noticeService,
            MarketInfoStompController marketInfoStompController,
            @Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate,
            RedisMessagePublisher redisMessagePublisher,
            TelegramService telegramService) {
        this.upbitScrapComponent = upbitScrapComponent;
        this.bithumbScrapComponent = bithumbScrapComponent;
        this.coinoneScrapComponent = coinoneScrapComponent;
        this.binanceScrapComponent = binanceScrapComponent;
        this.exchangeNoticePacadeService = exchangeNoticePacadeService;
        this.noticeService = noticeService;
        this.marketInfoStompController = marketInfoStompController;
        this.redisTemplate = redisTemplate;
        this.redisMessagePublisher = redisMessagePublisher;
        this.telegramService = telegramService;
    }

    @Scheduled(fixedRate = 10000, scheduler = "upbitNoticeTaskScheduler")
    public void scrapUpbitNoticeData() {
        try {
            processExchangeNotices("UPBIT", upbitScrapComponent);
        } catch (Exception e) {
            log.error("UPBIT 공지사항 스크래핑 중 오류 발생", e);
        }
    }

    @Scheduled(fixedRate = 10000, scheduler = "binanceNoticeTaskScheduler")
    public void scrapBinanceNoticeData() {
        try {
            processExchangeNotices("BINANCE", binanceScrapComponent);
        } catch (Exception e) {
            log.error("BINANCE 공지사항 스크래핑 중 오류 발생", e);
        }
    }

    @Scheduled(fixedRate = 10000, scheduler = "bithumbNoticeTaskScheduler")
    public void scrapBithumbNoticeData() {
        try {
            processExchangeNotices("BITHUMB", bithumbScrapComponent);
        } catch (Exception e) {
            log.error("BITHUMB 공지사항 스크래핑 중 오류 발생", e);
        }
    }

    @Scheduled(fixedRate = 10000, scheduler = "coinoneNoticeTaskScheduler")
    public void scrapCoinoneNoticeData() {
        try {
            processExchangeNotices("COINONE", coinoneScrapComponent);
        } catch (Exception e) {
            log.error("COINONE 공지사항 스크래핑 중 오류 발생", e);
        }
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
            
            // 1-1. 현재 공지사항들을 최신순으로 정렬하고 최근 8일 데이터만 필터링
            // Flask 서버가 7일치 데이터를 제공하지만, 시간대 차이와 경계선 데이터를 고려하여 8일로 여유분 확보
            LocalDateTime eightDaysAgo = LocalDateTime.now().minusDays(8);
            currentNotices = currentNotices.stream()
                .filter(notice -> notice.getDate().isAfter(eightDaysAgo)) // 최근 8일 데이터만
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
                    
                    boolean saved = exchangeNoticePacadeService.createNoticesBulkOptimized(
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

                        // 7. Redis Pub/Sub으로 공지사항 발행 (분산 서버 간 이벤트 전파)
                        publishNewNotices(scrapComponent.getMarketType(), newNotices);

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
            String redisKey = RedisKeyType.NOTICES.getKey(marketType);

            // Redis Sorted set에서 기존 공지사항 URL들을 가져오기 (전체 범위)
            Set<Object> existingUrls = redisTemplate.opsForZSet().range(redisKey, 0, -1);
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
                existingUrls = redisTemplate.opsForZSet().range(redisKey, 0, -1);
                existingUrlStrings = existingUrls != null ?
                    existingUrls.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .collect(Collectors.toSet()) :
                    new HashSet<>();
                log.info("Redis 초기화 후 {} 거래소의 공지사항 URL {} 개", marketType, existingUrlStrings.size());
            }

            // 새로운 공지사항 찾기
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
     * Redis 캐시 초기화 - DB에서 기존 공지사항 URL과 날짜를 가져와서 Redis에 저장
     */
    private void initializeRedisCache(MarketType marketType) {
        try {
            String redisKey = RedisKeyType.NOTICES.getKey(marketType);

            // DB에서 최근 8일간의 공지사항 가져오기 (7일 + 1일 여유분)
            LocalDateTime eightDaysAgo = LocalDateTime.now().minusDays(8);
            List<NoticeDto> recentNotices = noticeService.getNoticesAfterDate(marketType, eightDaysAgo);

            if (!recentNotices.isEmpty()) {
                // Sorted Set에 URL들 저장 (각 공지사항의 날짜를 score로 사용)
                for (NoticeDto notice : recentNotices) {
                    if (notice.getUrl() != null && notice.getCreatedAt() != null) {
                        double score = notice.getCreatedAt().toEpochSecond(java.time.ZoneOffset.UTC);
                        redisTemplate.opsForZSet().add(redisKey, notice.getUrl(), score);
                    }
                }

                log.info("Redis 캐시 초기화 완료: {} 거래소 - {} 개 URL 저장 (최근 8일)", marketType, recentNotices.size());
            } else {
                log.warn("DB에서 {} 거래소의 최근 8일 공지사항을 가져오지 못했습니다", marketType);
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

            String redisKey = RedisKeyType.NOTICES.getKey(marketType);

            // Sorted set(ZSet)에 추가
            for (NoticeParsedData notice : newNotices) {
                if (notice.getAlink() != null && notice.getDate() != null) {
                    double score = notice.getDate().toEpochSecond(java.time.ZoneOffset.UTC);
                    redisTemplate.opsForZSet().add(redisKey, notice.getAlink(), score);
                }
            }

            // 7일 이전 데이터 자동 삭제
            long cutoff = LocalDateTime.now()
                .minusDays(7)
                .toEpochSecond(java.time.ZoneOffset.UTC);

            Long removed = redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, cutoff);

            if (removed != null && removed > 0) {
                log.info("{} 캐시 정리: {}개 삭제 (7일 이전)", marketType, removed);
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
     * WebSocket 및 텔레그램을 통한 실시간 공지사항 전송
     */
    private void sendNewNoticesViaWebSocket(List<NoticeParsedData> newNotices, String exchangeName) {
        for (NoticeParsedData noticeData : newNotices) {
            try {
                // 링크를 통해 DB에서 저장된 공지사항 조회
                NoticeDto noticeDto = noticeService.getNoticeByLink(noticeData.getAlink());
                if (noticeDto != null) {
                    // WebSocket 전송
                    marketInfoStompController.sendNewNotice(noticeDto);

                    // 텔레그램 전송
                    telegramService.sendNoticeMessage(noticeDto);
                } else {
                    log.warn("공지사항 전송 실패 - DB에서 공지사항을 찾을 수 없음: {} - {}",
                            exchangeName, noticeData.getTitle());
                }
            } catch (Exception e) {
                log.warn("공지사항 전송 실패: {} - {} (오류: {})",
                        exchangeName, noticeData.getTitle(), e.getMessage());
            }
        }
    }

    /**
     * Redis Pub/Sub으로 새 공지사항 발행 및 텔레그램 전송
     * 분산 서버 간 WebSocket 이벤트 전파 + 최초 발견 서버만 텔레그램 전송
     */
    private void publishNewNotices(MarketType marketType, List<NoticeParsedData> newNotices) {
        try {
            String channel = RedisChannelType.NOTICE.getChannel(marketType);

            // DB에서 저장된 공지사항 정보 가져오기 (최신 1개만)
            NoticeParsedData latestNotice = newNotices.stream()
                .max(Comparator.comparing(NoticeParsedData::getDate))
                .orElse(null);

            if (latestNotice == null || latestNotice.getAlink() == null) {
                log.warn("Redis Pub/Sub 발행 실패: 유효한 공지사항 없음");
                return;
            }

            // DB에서 조회하여 NoticeDto 생성
            NoticeDto noticeDto = noticeService.getNoticeByLink(latestNotice.getAlink());

            if (noticeDto != null) {
                // 1. Redis Pub/Sub 발행 (모든 서버에 WebSocket 전송 트리거)
                redisMessagePublisher.publish(channel, noticeDto);
                log.info("Redis Pub/Sub 발행 완료: channel={}, noticeId={}, title={}",
                    channel, noticeDto.getId(), noticeDto.getTitle());

                // 2. 텔레그램 전송 (최초 발견한 이 서버에서만 실행)
                try {
                    telegramService.sendNoticeMessage(noticeDto);
                } catch (Exception e) {
                    log.error("텔레그램 전송 실패: {} - {} (오류: {})",
                        marketType, noticeDto.getTitle(), e.getMessage());
                }
            } else {
                log.warn("Redis Pub/Sub 발행 실패: DB에서 공지사항 조회 실패 - {}", latestNotice.getAlink());
            }

        } catch (Exception e) {
            log.error("Redis Pub/Sub 발행 실패: {} - {}", marketType, e.getMessage(), e);
        }
    }
}