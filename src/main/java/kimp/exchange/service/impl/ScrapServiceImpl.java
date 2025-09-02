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
import kimp.market.controller.MarketInfoStompController;
import kimp.market.Enum.MarketType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final MarketInfoStompController marketInfoStompController;

    public ScrapServiceImpl(
            ExchangeScrap<UpbitNoticeDto> upbitScrapComponent,
            ExchangeScrap<BithumbNoticeDto> bithumbScrapComponent,
            ExchangeScrap<CoinoneNoticeDto> coinoneScrapComponent,
            ExchangeScrap<BinanceNoticeDto> binanceScrapComponent,
            ExchangeNoticePacadeService exchangeNoticePacadeService, 
            NoticeService noticeService,
            MarketInfoStompController marketInfoStompController) {
        this.upbitScrapComponent = upbitScrapComponent;
        this.bithumbScrapComponent = bithumbScrapComponent;
        this.coinoneScrapComponent = coinoneScrapComponent;
        this.binanceScrapComponent = binanceScrapComponent;
        this.exchangeNoticePacadeService = exchangeNoticePacadeService;
        this.noticeService = noticeService;
        this.marketInfoStompController = marketInfoStompController;
    }

    /**
     * Python 서비스를 통한 거래소별 공지사항 스크래핑
     * Redis 종속성 제거 - DB 기반 날짜 비교로 효율적인 새로운 공지사항 감지
     */
    @Scheduled(fixedRate = 30000) // 30초
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
            
            // 1-1. 현재 공지사항들을 최신순으로 정렬하고 최근 30일 데이터만 필터링
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            currentNotices = currentNotices.stream()
                .filter(notice -> notice.getDate().isAfter(thirtyDaysAgo)) // 최근 30일 데이터만
                .sorted((a, b) -> b.getDate().compareTo(a.getDate())) // 최신순 정렬
                .toList();
            
            log.info("{} 거래소에서 {} 개의 공지사항을 가져왔습니다 (최근 30일, 최신순 정렬)", exchangeName, currentNotices.size());
            
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
     * 새로운 DB 기반 공지사항 감지 로직
     * 모든 DB 데이터를 가져와서 link 기반으로 비교하고 date도 체크하여 업데이트 처리
     */
    private List<NoticeParsedData> findNewNoticesFromDB(MarketType marketType, List<NoticeParsedData> currentNotices) {
        try {
            // 1. DB에서 해당 거래소의 모든 공지사항을 가져오기 (기존 최신 20개 방식에서 변경)
            List<NoticeDto> allDbNotices = noticeService.getAllNoticesByMarketType(marketType);
            log.info("DB에서 {} 거래소의 모든 공지사항 {} 개를 가져왔습니다", marketType, allDbNotices.size());
            
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
                        log.info("공지사항 날짜 업데이트 감지: {} - 기존: {}, 새로운: {}", 
                                currentNotice.getTitle(), dbDate, scrapDate);
                        
                        // 날짜 업데이트
                        noticeService.updateNoticeDate(existingNotice.getId(), currentNotice.getDate());
                        newOrUpdatedNotices.add(currentNotice); // 업데이트된 공지사항도 웹소켓 송신 대상
                    }
                } else {
                    // 4. 없으면 아예 새로운 공지사항
                    log.info("새로운 공지사항 발견: {} - URL: {} - 날짜: {}", 
                            currentNotice.getTitle(), currentNotice.getAlink(), currentNotice.getDate());
                    newOrUpdatedNotices.add(currentNotice);
                }
            }
            
            log.info("전체 {} 개 중 새로운/업데이트된 공지사항 {} 개 발견", 
                    currentNotices.size(), newOrUpdatedNotices.size());
            
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
                    marketInfoStompController.sendNewNotice(noticeDto);
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