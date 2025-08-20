package kimp.exchange.service.impl;

import kimp.notice.dto.notice.NoticeDto;
import kimp.notice.dto.notice.NoticeParsedData;
import kimp.scrap.component.ExchangeScrap;
import kimp.scrap.dto.binance.BinanceNoticeDto;
import kimp.scrap.dto.bithumb.BithumbNoticeDto;
import kimp.scrap.dto.coinone.CoinoneNoticeDto;
import kimp.scrap.dto.upbit.UpbitNoticeDto;
import kimp.notice.service.NoticeService;
import kimp.exchange.service.ScrapService;
import kimp.market.handler.MarketInfoHandler;
import kimp.market.Enum.MarketType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ScrapServiceImpl implements ScrapService {

    private final ExchangeScrap<UpbitNoticeDto> upbitScrapComponent;
    private final ExchangeScrap<BithumbNoticeDto> bithumbScrapComponent;
    private final ExchangeScrap<CoinoneNoticeDto> coinoneScrapComponent;
    private final ExchangeScrap<BinanceNoticeDto> binanceScrapComponent;
    private final ExchangeNoticePacadeService exchangeNoticePacadeService;
    private final NoticeService noticeService;
    private final MarketInfoHandler marketInfoHandler;

    public ScrapServiceImpl(
            ExchangeScrap<UpbitNoticeDto> upbitScrapComponent,
            ExchangeScrap<BithumbNoticeDto> bithumbScrapComponent,
            ExchangeScrap<CoinoneNoticeDto> coinoneScrapComponent,
            ExchangeScrap<BinanceNoticeDto> binanceScrapComponent,
            ExchangeNoticePacadeService exchangeNoticePacadeService, 
            NoticeService noticeService,
            MarketInfoHandler marketInfoHandler) {
        this.upbitScrapComponent = upbitScrapComponent;
        this.bithumbScrapComponent = bithumbScrapComponent;
        this.coinoneScrapComponent = coinoneScrapComponent;
        this.binanceScrapComponent = binanceScrapComponent;
        this.exchangeNoticePacadeService = exchangeNoticePacadeService;
        this.noticeService = noticeService;
        this.marketInfoHandler = marketInfoHandler;
    }

    /**
     * Python 서비스를 통한 거래소별 공지사항 스크래핑
     * Redis 종속성 제거 - DB 기반 날짜 비교로 효율적인 새로운 공지사항 감지
     */
    @Scheduled(fixedRate = 30000) // 30초에서
    public void scrapNoticeData() throws IOException {
        log.info("공지사항 스케줄링 실행 시작");
        
        // 각 거래소별 컴포넌트를 통해 Python 서비스 호출
        processExchangeNotices("UPBIT", upbitScrapComponent);
        processExchangeNotices("BITHUMB", bithumbScrapComponent);
        processExchangeNotices("BINANCE", binanceScrapComponent);
        processExchangeNotices("COINONE", coinoneScrapComponent);
        
        log.info("공지사항 스케줄링 실행 완료");
    }
    
    /**
     * 특정 거래소 컴포넌트를 통한 공지사항 처리
     */
    private <T> void processExchangeNotices(String exchangeName, ExchangeScrap<T> scrapComponent) {
        try {
            log.info("{} 공지사항 처리 시작", exchangeName);
            
            // 1. Python 서비스를 통해 최신 공지사항 파싱
            List<NoticeParsedData> currentNotices = scrapComponent.parseNoticeData();
            
            if (currentNotices == null || currentNotices.isEmpty()) {
                log.warn("{} 거래소에서 공지사항을 가져오지 못했습니다", exchangeName);
                return;
            }
            
            // 1-1. 현재 공지사항들을 최신순으로 정렬 (날짜 기준 내림차순)
            currentNotices = currentNotices.stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .toList();
            
            log.debug("{} 거래소에서 {} 개의 공지사항을 가져왔습니다 (최신순 정렬 완료)", exchangeName, currentNotices.size());
            
            // 2. DB 기반 새로운 공지사항 감지 (Redis 해시 비교 제거)
            List<NoticeParsedData> newNotices = findNewNoticesFromDB(scrapComponent.getMarketType(), currentNotices);
            boolean hasUpdate = !newNotices.isEmpty();
            
            if (hasUpdate) {
                log.info("{} 거래소에 새로운 공지사항 {} 개 발견", exchangeName, newNotices.size());
                
                // 4-1. 새로운 공지사항들도 최신순으로 정렬
                if (newNotices != null && !newNotices.isEmpty()) {
                    newNotices = newNotices.stream()
                        .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                        .toList();
                    
                    log.info("{} 거래소 새로운 공지사항 {} 개 발견 (최신순 정렬)", exchangeName, newNotices.size());
                    
                    // 4-2. 가장 최신 공지사항 정보 로깅
                    NoticeParsedData latestNotice = newNotices.get(0);
                    log.info("🔔 가장 최신 공지사항: {} - {} ({})", 
                            exchangeName, latestNotice.getTitle(), latestNotice.getDate());
                    
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
                        log.info("{} 거래소 새로운 공지사항 {} 개 저장 완료 (오래된 순서부터 저장)", exchangeName, newNotices.size());
                        
                        // 6-1. 저장된 새로운 공지사항들 상세 로깅 (저장 순서대로)
                        for (int i = 0; i < orderedForSaving.size(); i++) {
                            NoticeParsedData notice = orderedForSaving.get(i);
                            boolean isLatest = notice.equals(latestNotice);
                            log.info("  DB 저장 [{}]: {} - {} {}", 
                                    i + 1, notice.getTitle(), notice.getDate(), 
                                    isLatest ? "🔥 (최신 공지사항, 마지막 저장)" : "");
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
     * DB 기반 효율적 새로운 공지사항 감지 로직
     * Redis 대신 DB의 최신 공지사항 날짜를 기준으로 새로운 공지사항 필터링
     */
    private List<NoticeParsedData> findNewNoticesFromDB(MarketType marketType, List<NoticeParsedData> currentNotices) {
        try {
            // 1. DB에서 해당 거래소의 가장 최근 공지사항 날짜 가져오기
            LocalDateTime latestDbDate = noticeService.getLatestNoticeDate(marketType);
            
            if (latestDbDate == null) {
                // DB에 해당 거래소 공지사항이 없는 경우 - 모든 현재 공지사항을 새로운 것으로 간주
                log.info("DB에 {} 거래소 공지사항이 없음 - 모든 현재 공지사항({} 개)을 새로운 것으로 처리", 
                        marketType, currentNotices.size());
                return currentNotices.stream()
                    .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                    .toList();
            }
            
            // 2. 현재 공지사항 중에서 DB 최신 날짜보다 최신인 것들만 필터링
            List<NoticeParsedData> newNotices = currentNotices.stream()
                .filter(notice -> notice.getDate().isAfter(latestDbDate))
                .sorted((a, b) -> b.getDate().compareTo(a.getDate())) // 최신순 정렬
                .toList();
            
            log.info("DB 날짜 기반 비교 결과: DB 최신 날짜 [{}], 전체 {} 개 중 새로운 공지사항 {} 개 발견", 
                    latestDbDate, currentNotices.size(), newNotices.size());
            
            if (!newNotices.isEmpty()) {
                NoticeParsedData latestNew = newNotices.get(0);
                log.info("가장 최신 공지사항: {} - {}", latestNew.getTitle(), latestNew.getDate());
            }
            
            return newNotices;
                
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
                
            log.info("링크 기반 백업 결과: 전체 {} 개 중 새로운 공지사항 {} 개 발견", 
                    currentNotices.size(), newNotices.size());
            
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
     * WebSocket을 통한 실시간 공지사항 전송
     */
    private void sendNewNoticesViaWebSocket(List<NoticeParsedData> newNotices, String exchangeName) {
        log.info("{} WebSocket 전송 시작 - {} 개 공지사항", exchangeName, newNotices.size());
        
        for (int i = 0; i < newNotices.size(); i++) {
            NoticeParsedData noticeData = newNotices.get(i);
            try {
                // 링크를 통해 DB에서 저장된 공지사항 조회
                NoticeDto noticeDto = noticeService.getNoticeByLink(noticeData.getAlink());
                if (noticeDto != null) {
                    marketInfoHandler.sendNewNotice(noticeDto);
                    log.info("✅ WebSocket 전송 완료 [{}]: {} - {} ({})", 
                            i + 1, exchangeName, noticeData.getTitle(), noticeData.getDate());
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