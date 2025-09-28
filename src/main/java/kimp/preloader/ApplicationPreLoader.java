package kimp.preloader;

import jakarta.annotation.PostConstruct;
import kimp.scrap.component.ExchangeScrap;
import kimp.scrap.component.impl.exchange.ExchangeScrapAbstract;
import kimp.scrap.dto.binance.BinanceNoticeDto;
import kimp.scrap.dto.bithumb.BithumbNoticeDto;
import kimp.scrap.dto.coinone.CoinoneNoticeDto;
import kimp.notice.dto.notice.NoticeParsedData;
import kimp.scrap.dto.upbit.UpbitNoticeDto;
import kimp.exchange.service.ExchangeService;
import kimp.notice.service.NoticeService;
import kimp.exchange.service.ScrapService;
import kimp.exchange.service.impl.ExchangeNoticePacadeService;
import kimp.market.Enum.MarketType;
import kimp.market.dto.coin.common.ServiceCoinDto;
import kimp.market.service.serviceImpl.CoinExchangePacadeService;
import kimp.user.service.AdminService;
import kimp.cmc.service.CmcEntityPreloaderService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 애플리케이션 시작 시 필요한 초기 데이터를 사전 로딩하는 컴포넌트
 * 
 * 실행 순서:
 * 1. CMC 엔티티 사전 로딩
 * 2. 코인-거래소 매핑 초기화
 * 3. 거래소 공지사항 초기 데이터 로딩
 */
@Component
@Slf4j
public class ApplicationPreLoader {

    private final NoticeService noticeService;
    private final ScrapService scrapService;
    private final ExchangeNoticePacadeService exchangeNoticePacadeService;
    private final ExchangeService exchangeService;
    private final CoinExchangePacadeService coinExchangePacadeService;
    private final AdminService adminService;
    private final CmcEntityPreloaderService cmcEntityPreloaderService;
    private final Environment env;

    private final ExchangeScrap<UpbitNoticeDto> upbitScrapComponent;
    private final ExchangeScrap<BithumbNoticeDto> bithumbScrapComponent;
    private final ExchangeScrap<CoinoneNoticeDto> coinoneScrapComponent;
    private final ExchangeScrap<BinanceNoticeDto> binanceScrapComponent;

    public ApplicationPreLoader(NoticeService noticeService, 
                               ScrapService scrapService, 
                               ExchangeNoticePacadeService exchangeNoticePacadeService, 
                               ExchangeService exchangeService, 
                               CoinExchangePacadeService coinExchangePacadeService, 
                               AdminService adminService, 
                               CmcEntityPreloaderService cmcEntityPreloaderService, 
                               Environment env, 
                               ExchangeScrapAbstract<UpbitNoticeDto> upbitScrap, 
                               ExchangeScrapAbstract<BithumbNoticeDto> bithumbScrap, 
                               ExchangeScrapAbstract<CoinoneNoticeDto> coinoneScrap, 
                               ExchangeScrapAbstract<BinanceNoticeDto> binanceScrap) {
        this.noticeService = noticeService;
        this.scrapService = scrapService;
        this.exchangeNoticePacadeService = exchangeNoticePacadeService;
        this.exchangeService = exchangeService;
        this.coinExchangePacadeService = coinExchangePacadeService;
        this.adminService = adminService;
        this.cmcEntityPreloaderService = cmcEntityPreloaderService;
        this.env = env;

        this.upbitScrapComponent = upbitScrap;
        this.bithumbScrapComponent = bithumbScrap;
        this.coinoneScrapComponent = coinoneScrap;
        this.binanceScrapComponent = binanceScrap;
    }

    /**
     * 1단계: CMC 엔티티 사전 로딩
     * 
     * 개발 환경에서는 성능상 비활성화
     */
    @PostConstruct
    @Order(1)
    private void preloadCmcEntities() {
        // 개발 환경에서는 CMC 사전 로딩 건너뛰기 (성능 최적화)
        if (Arrays.asList(env.getActiveProfiles()).contains("dev")) {
            log.info("=== 개발 환경: CMC 엔티티 사전 로딩 건너뜀 (성능 최적화) ===");
            return;
        }
        
        try {
            cmcEntityPreloaderService.preloadAllCmcEntities();
            log.info("=== CMC 엔티티 사전 로딩 완료 ===");
        } catch (Exception e) {
            log.warn("CMC 엔티티 사전 로딩 실패 (데이터가 없거나 초기 실행일 수 있음): {}", e.getMessage());
        }
    }

    /**
     * 2단계: 코인-거래소 매핑 초기화
     * 
     * 각 거래소별 코인 목록을 데이터베이스에 동기화
     */
    @PostConstruct
    @Order(2)
    private void initializeCoinExchangeMapping() {
        log.info("=== 코인-거래소 매핑 초기화 시작 ===");
        
        MarketType[] marketTypes = MarketType.values();

        for (MarketType marketType : marketTypes) {
            try {
                List<MarketType> marketTypeList = new ArrayList<>();
                marketTypeList.add(marketType);
                
                List<ServiceCoinDto> serviceCoinDtoList = this.coinExchangePacadeService.getCoinsByExchange(marketType);
                
                if (serviceCoinDtoList != null && !serviceCoinDtoList.isEmpty()) {
                    this.coinExchangePacadeService.createCoinBulk(marketTypeList, serviceCoinDtoList);
                    log.info("거래소 {} 코인 매핑 완료: {} 개", marketType, serviceCoinDtoList.size());
                } else {
                    log.warn("거래소 {} 코인 목록이 비어있음", marketType);
                }
            } catch (Exception e) {
                log.error("거래소 {} 코인 매핑 초기화 실패: {}", marketType, e.getMessage(), e);
            }
        }
        
        log.info("=== 코인-거래소 매핑 초기화 완료 ===");
    }

    /**
     * 3단계: 거래소 공지사항 초기 데이터 로딩
     * 
     * 각 거래소의 최신 공지사항을 스크래핑하여 초기 데이터로 설정
     * Python 서비스 연결이 필요한 경우 예외 처리하여 애플리케이션 시작 방해하지 않음
     */
    @PostConstruct
    @Order(3)
    private void initializeExchangeNotices() {
        log.info("=== 거래소 공지사항 초기 데이터 로딩 시작 ===");
        
        // 각 거래소별 공지사항 초기화 (Python 서비스 의존성 예외 처리)
        initializeNoticesForExchange("Upbit", this::initializeUpbitNotices);
        initializeNoticesForExchange("Coinone", this::initializeCoinoneNotices);
        initializeNoticesForExchange("Binance", this::initializeBinanceNotices);
        initializeNoticesForExchange("Bithumb", this::initializeBithumbNotices);

        log.info("=== 거래소 공지사항 초기 데이터 로딩 완료 ===");
    }

    /**
     * 거래소별 공지사항 초기화 템플릿 메소드
     */
    private void initializeNoticesForExchange(String exchangeName, Runnable initializationTask) {
        try {
            initializationTask.run();
        } catch (Exception e) {
            log.warn("{} 공지사항 초기화 실패 (외부 서비스 연결 문제 가능): {}", exchangeName, e.getMessage());
        }
    }

    /**
     * Upbit 공지사항 초기화
     */
    private void initializeUpbitNotices() {
        try {
            log.debug("Upbit 공지사항 초기화 시작");
            List<NoticeParsedData> upbitNoticeParsedDataList = upbitScrapComponent.parseNoticeData();
            
            if (!upbitNoticeParsedDataList.isEmpty()) {
                upbitScrapComponent.setNewParsedData(upbitNoticeParsedDataList);
                exchangeNoticePacadeService.createNoticesBulkOptimized(upbitScrapComponent.getMarketType(), upbitNoticeParsedDataList);
                log.info("Upbit 공지사항 {} 개 초기화 완료", upbitNoticeParsedDataList.size());
            } else {
                log.warn("Upbit 공지사항 데이터가 비어있음");
            }
        } catch (IOException e) {
            throw new RuntimeException("Upbit 공지사항 초기화 실패", e);
        }
    }

    /**
     * Coinone 공지사항 초기화
     */
    private void initializeCoinoneNotices() {
        try {
            log.debug("Coinone 공지사항 초기화 시작");
            List<NoticeParsedData> coinoneNoticeParsedDataList = coinoneScrapComponent.parseNoticeData();
            
            if (!coinoneNoticeParsedDataList.isEmpty()) {
                coinoneScrapComponent.setNewParsedData(coinoneNoticeParsedDataList);
                exchangeNoticePacadeService.createNoticesBulkOptimized(coinoneScrapComponent.getMarketType(), coinoneNoticeParsedDataList);
                log.info("Coinone 공지사항 {} 개 초기화 완료", coinoneNoticeParsedDataList.size());
            } else {
                log.warn("Coinone 공지사항 데이터가 비어있음");
            }
        } catch (IOException e) {
            throw new RuntimeException("Coinone 공지사항 초기화 실패", e);
        }
    }

    /**
     * Binance 공지사항 초기화
     */
    private void initializeBinanceNotices() {
        try {
            log.debug("Binance 공지사항 초기화 시작");
            List<NoticeParsedData> binanceNoticeParsedDataList = binanceScrapComponent.parseNoticeData();
            
            if (!binanceNoticeParsedDataList.isEmpty()) {
                binanceScrapComponent.setNewParsedData(binanceNoticeParsedDataList);
                exchangeNoticePacadeService.createNoticesBulkOptimized(binanceScrapComponent.getMarketType(), binanceNoticeParsedDataList);
                log.info("Binance 공지사항 {} 개 초기화 완료", binanceNoticeParsedDataList.size());
            } else {
                log.warn("Binance 공지사항 데이터가 비어있음");
            }
        } catch (IOException e) {
            throw new RuntimeException("Binance 공지사항 초기화 실패", e);
        }
    }

    /**
     * Bithumb 공지사항 초기화
     */
    private void initializeBithumbNotices() {
        try {
            log.debug("Bithumb 공지사항 초기화 시작");
            List<NoticeParsedData> bithumbNoticeParsedDataList = bithumbScrapComponent.parseNoticeData();
            
            if (!bithumbNoticeParsedDataList.isEmpty()) {
                bithumbScrapComponent.setNewParsedData(bithumbNoticeParsedDataList);
                exchangeNoticePacadeService.createNoticesBulkOptimized(bithumbScrapComponent.getMarketType(), bithumbNoticeParsedDataList);
                log.info("Bithumb 공지사항 {} 개 초기화 완료", bithumbNoticeParsedDataList.size());
            } else {
                log.warn("Bithumb 공지사항 데이터가 비어있음");
            }
        } catch (IOException e) {
            throw new RuntimeException("Bithumb 공지사항 초기화 실패", e);
        }
    }
}