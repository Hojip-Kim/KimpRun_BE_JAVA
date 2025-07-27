package kimp.exchange.service.impl;

import kimp.notice.dto.notice.NoticeDto;
import kimp.notice.dto.notice.NoticeParsedData;
import kimp.exchange.component.ExchangeScrap;
import kimp.exchange.dto.binance.BinanceNoticeDto;
import kimp.exchange.dto.bithumb.BithumbNoticeDto;
import kimp.exchange.dto.coinone.CoinoneNoticeDto;
import kimp.exchange.dto.upbit.UpbitNoticeDto;
import kimp.notice.service.NoticeService;
import kimp.exchange.service.ScrapService;
import kimp.market.handler.MarketInfoHandler;
import kimp.market.Enum.MarketType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
     * 각 거래소 컴포넌트에서 Python 서비스를 호출하여 기존 Redis 해시 비교 로직 유지
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
            
            // 2. Redis에서 이전 해시 가져오기
            String previousHash = scrapComponent.getNoticeFromRedis();
            log.debug("{} 이전 Redis 해시: {}", exchangeName, previousHash);
            
            // 3. 해시 비교를 통한 변경사항 확인
            boolean hasUpdate = scrapComponent.isUpdatedNotice(previousHash, currentNotices);
            
            if (hasUpdate) {
                log.info("{} 거래소에 변경사항 발견", exchangeName);
                
                // 4. 새로운 공지사항 찾기 (현재 상태를 먼저 백업)
                List<NoticeParsedData> previousNotices = scrapComponent.getNoticeData();
                log.debug("{} 이전 저장된 공지사항 개수: {}", exchangeName, previousNotices.size());
                
                List<NoticeParsedData> newNotices;
                
                // 초기화 실패 등으로 이전 데이터가 없는 경우 처리
                if (previousNotices.isEmpty()) {
                    log.warn("{} 이전 공지사항 데이터가 비어있음 - DB에서 최신 공지사항과 비교", exchangeName);
                    // DB에서 최신 공지사항 몇 개를 가져와서 비교
                    newNotices = findNewNoticesFromDB(scrapComponent.getMarketType(), currentNotices);
                } else {
                    // 정상적인 경우: 메모리의 이전 데이터와 비교
                    newNotices = scrapComponent.getNewNotice(currentNotices);
                }
                
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
                    
                    // 5. 상태 업데이트 (반드시 새로운 공지사항 처리 후에)
                    scrapComponent.setNoticeToRedis(currentNotices);
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
                    // 해시는 변경되었지만 새로운 공지사항이 없는 경우도 상태 업데이트
                    scrapComponent.setNoticeToRedis(currentNotices);
                    scrapComponent.setNewParsedData(currentNotices);
                }
            } else {
                log.debug("{} 거래소에 변경사항 없음", exchangeName);
            }
            
        } catch (IllegalStateException e) {
            // 새로운 공지사항이 없는 경우 (정상적인 상황)
            log.debug("{} 거래소에 새로운 공지사항이 없습니다: {}", exchangeName, e.getMessage());
        } catch (Exception e) {
            log.error("{} 거래소 공지사항 처리 중 오류 발생: {}", exchangeName, e.getMessage(), e);
        }
    }
    
    /**
     * DB에서 최신 공지사항과 비교하여 새로운 것만 찾기
     * 초기화 실패 시 사용되는 백업 로직
     */
    private List<NoticeParsedData> findNewNoticesFromDB(MarketType marketType, List<NoticeParsedData> currentNotices) {
        try {
            // DB에서 해당 거래소의 최신 공지사항 링크들 가져오기 (최근 100개 정도)
            List<String> existingLinks = noticeService.getRecentNoticeLinks(marketType, 100);
            
            // 현재 공지사항 중에서 DB에 없는 것만 필터링하고 최신순으로 정렬 (분석용)
            List<NoticeParsedData> newNotices = currentNotices.stream()
                .filter(notice -> !existingLinks.contains(notice.getAlink()))
                .sorted((a, b) -> b.getDate().compareTo(a.getDate())) // 최신순 정렬 (분석 및 로깅용)
                .toList();
                
            log.info("DB 비교 결과: 전체 {} 개 중 새로운 공지사항 {} 개 발견", 
                    currentNotices.size(), newNotices.size());
                
            // 반환은 최신순으로 (처리 로직에서 저장 시 다시 정렬함)
            return newNotices;
                
        } catch (Exception e) {
            log.error("DB에서 기존 공지사항 확인 실패: {}", e.getMessage());
            // 실패 시 모든 현재 공지사항을 새로운 것으로 간주하되 최신순으로 정렬 (분석용)
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