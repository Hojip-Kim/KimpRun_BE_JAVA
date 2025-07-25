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
    @Scheduled(fixedRate = 30000)
    public void scrapNoticeData() throws IOException {
        log.info("Python 서비스를 통한 스케줄링 실행");
        
        // 각 거래소별 컴포넌트를 통해 Python 서비스 호출
        processExchangeNotices("UPBIT", upbitScrapComponent);
        processExchangeNotices("BITHUMB", bithumbScrapComponent);
        processExchangeNotices("BINANCE", binanceScrapComponent);
        processExchangeNotices("COINONE", coinoneScrapComponent);
        
        log.info("Python 서비스를 통한 스케줄링 완료");
    }
    
    /**
     * 특정 거래소 컴포넌트를 통한 공지사항 처리
     */
    private <T> void processExchangeNotices(String exchangeName, ExchangeScrap<T> scrapComponent) {
        try {
            log.info("{} 공지사항 처리 시작", exchangeName);
            
            // Python 서비스를 통해 최신 공지사항 파싱
            List<NoticeParsedData> currentNotices = scrapComponent.parseNoticeData();
            
            if (currentNotices == null || currentNotices.isEmpty()) {
                log.warn("{} 거래소에서 공지사항을 가져오지 못했습니다", exchangeName);
                return;
            }
            
            // Redis에서 이전 해시 가져오기
            String previousHash = scrapComponent.getNoticeFromRedis();
            
            // 새로운 공지사항이 있는지 확인 (기존 해시 비교 로직 사용)
            if (scrapComponent.isUpdatedNotice(previousHash, currentNotices)) {
                log.info("{} 거래소에 새로운 공지사항 발견", exchangeName);
                
                // 새로운 공지사항 찾기 (기존 로직 사용)
                List<NoticeParsedData> newNotices = scrapComponent.getNewNotice(currentNotices);
                
                if (newNotices != null && !newNotices.isEmpty()) {
                    // Redis 업데이트
                    scrapComponent.setNoticeToRedis(currentNotices);
                    scrapComponent.setNewParsedData(currentNotices);
                    scrapComponent.setNewNotice(newNotices);
                    
                    // DB에 새로운 공지사항 저장
                    boolean saved = exchangeNoticePacadeService.createNoticesBulk(
                        scrapComponent.getMarketType(), 
                        scrapComponent.getFieldNewNotice()
                    );
                    
                    if (saved) {
                        log.info("{} 거래소 새로운 공지사항 {} 개 저장 완료", exchangeName, newNotices.size());
                        
                        // WebSocket으로 실시간 전송
                        sendNewNoticesViaWebSocket(newNotices, exchangeName);
                        
                        log.info("새로운 공지사항 발생! {} - {}", exchangeName, newNotices.get(0).getTitle());
                    }
                } else {
                    log.debug("{} 거래소 새로운 공지사항 추출 실패", exchangeName);
                }
            } else {
                log.debug("{} 거래소에 새로운 공지사항이 없습니다", exchangeName);
            }
            
        } catch (IllegalStateException e) {
            // 새로운 공지사항이 없는 경우 (정상적인 상황)
            log.debug("{} 거래소에 새로운 공지사항이 없습니다: {}", exchangeName, e.getMessage());
        } catch (Exception e) {
            log.error("{} 거래소 공지사항 처리 중 오류 발생: {}", exchangeName, e.getMessage(), e);
        }
    }
    
    /**
     * WebSocket을 통한 실시간 공지사항 전송
     */
    private void sendNewNoticesViaWebSocket(List<NoticeParsedData> newNotices, String exchangeName) {
        for (NoticeParsedData noticeData : newNotices) {
            try {
                // 링크를 통해 DB에서 저장된 공지사항 조회
                NoticeDto noticeDto = noticeService.getNoticeByLink(noticeData.getAlink());
                if (noticeDto != null) {
                    marketInfoHandler.sendNewNotice(noticeDto);
                    log.debug("{} 새로운 공지사항 WebSocket 전송 완료: {}", exchangeName, noticeData.getTitle());
                }
            } catch (Exception e) {
                log.warn("{} WebSocket 전송 실패: {} - {}", exchangeName, noticeData.getTitle(), e.getMessage());
            }
        }
    }
}