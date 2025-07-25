package kimp.test;

import jakarta.annotation.PostConstruct;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.service.CategoryService;
import kimp.exchange.component.ExchangeScrap;
import kimp.exchange.component.impl.exchange.ExchangeScrapAbstract;
import kimp.exchange.dto.binance.BinanceNoticeDto;
import kimp.exchange.dto.bithumb.BithumbNoticeDto;
import kimp.exchange.dto.coinone.CoinoneNoticeDto;
import kimp.notice.dto.notice.NoticeParsedData;
import kimp.exchange.dto.upbit.UpbitNoticeDto;
import kimp.exchange.service.ExchangeService;
import kimp.notice.service.NoticeService;
import kimp.exchange.service.ScrapService;
import kimp.exchange.service.impl.ExchangeNoticePacadeService;
import kimp.market.Enum.MarketType;
import kimp.market.dto.coin.common.ServiceCoinDto;
import kimp.market.service.serviceImpl.CoinExchangePacadeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class testDataInit {

    private final CategoryService categoryService;
    private final NoticeService noticeService;
    private final ScrapService scrapService;
    private final ExchangeNoticePacadeService exchangeNoticePacadeService;
    private final ExchangeService exchangeService;
    private final CoinExchangePacadeService coinExchangePacadeService;


    private final ExchangeScrap<UpbitNoticeDto> upbitScrapComponent;
    private final ExchangeScrap<BithumbNoticeDto> bithumbScrapComponent;
    private final ExchangeScrap<CoinoneNoticeDto> coinoneScrapComponent;
    private final ExchangeScrap<BinanceNoticeDto> binanceScrapComponent;

    public testDataInit(CategoryService categoryServer, NoticeService noticeService, ScrapService scrapService, ExchangeNoticePacadeService exchangeNoticePacadeService, ExchangeService exchangeService, CoinExchangePacadeService coinExchangePacadeService, ExchangeScrapAbstract<UpbitNoticeDto> upbitScrap, ExchangeScrapAbstract<BithumbNoticeDto> bithumbScrap, ExchangeScrapAbstract<CoinoneNoticeDto> coinoneScrap, ExchangeScrapAbstract<BinanceNoticeDto> binanceScrap) {
        this.categoryService = categoryServer;
        this.noticeService = noticeService;
        this.scrapService = scrapService;
        this.exchangeNoticePacadeService = exchangeNoticePacadeService;
        this.exchangeService = exchangeService;
        this.coinExchangePacadeService = coinExchangePacadeService;

        this.upbitScrapComponent = upbitScrap;
        this.bithumbScrapComponent = bithumbScrap;
        this.coinoneScrapComponent = coinoneScrap;
        this.binanceScrapComponent = binanceScrap;
    }

    @PostConstruct
    private void coinExchangePacadeServiceInit(){
        MarketType[] marketTypes = MarketType.values();

        for(MarketType marketType : marketTypes){
            List<MarketType> marketTypeList = new ArrayList<>();
            marketTypeList.add(marketType);
            List<ServiceCoinDto> serviceCoinDtoList = this.coinExchangePacadeService.getCoinsByExchange(marketType);
            if(serviceCoinDtoList != null){
                this.coinExchangePacadeService.createCoinBulk(marketTypeList, serviceCoinDtoList);
            }
        }
    }

    @PostConstruct
    private void init() {
        log.info("공지사항 초기 데이터 세팅 시작");
        
        // Python 서비스 의존성이 있는 초기화는 예외 처리
        try {
            initializeUpbitNotices();
        } catch (Exception e) {
            log.warn("Upbit 공지사항 초기화 실패 (Python 서비스 연결 필요): {}", e.getMessage());
        }

        try {
            initializeCoinoneNotices();
        } catch (Exception e) {
            log.warn("Coinone 공지사항 초기화 실패 (Python 서비스 연결 필요): {}", e.getMessage());
        }

        // 현재 주석 처리된 거래소들도 동일하게 처리

        try {
            initializeBinanceNotices();
        } catch (Exception e) {
            log.warn("Binance 공지사항 초기화 실패 (Python 서비스 연결 필요): {}", e.getMessage());
        }

        try {
            initializeBithumbNotices();
        } catch (Exception e) {
            log.warn("Bithumb 공지사항 초기화 실패 (Python 서비스 연결 필요): {}", e.getMessage());
        }

        log.info("공지사항 초기 데이터 세팅 완료 (Python 서비스 연결 실패 시 스킵됨)");
    }

    /**
     * Upbit 공지사항 초기화
     */
    private void initializeUpbitNotices() throws IOException {
        log.debug("Upbit 공지사항 초기화 시작");
        List<NoticeParsedData> upbitNoticeParsedDataList = upbitScrapComponent.parseNoticeData();
        
        if (!upbitNoticeParsedDataList.isEmpty()) {
            upbitScrapComponent.setNoticeToRedis(upbitNoticeParsedDataList);
            upbitScrapComponent.setNewParsedData(upbitNoticeParsedDataList);
            exchangeNoticePacadeService.createNoticesBulk(upbitScrapComponent.getMarketType(), upbitNoticeParsedDataList);
            log.info("Upbit 공지사항 {} 개 초기화 완료", upbitNoticeParsedDataList.size());
        } else {
            log.warn("Upbit 공지사항 데이터가 비어있음");
        }
    }

    /**
     * Coinone 공지사항 초기화
     */
    private void initializeCoinoneNotices() throws IOException {
        log.debug("Coinone 공지사항 초기화 시작");
        List<NoticeParsedData> coinoneNoticeParsedDataList = coinoneScrapComponent.parseNoticeData();
        
        if (!coinoneNoticeParsedDataList.isEmpty()) {
            coinoneScrapComponent.setNoticeToRedis(coinoneNoticeParsedDataList);
            coinoneScrapComponent.setNewParsedData(coinoneNoticeParsedDataList);
            exchangeNoticePacadeService.createNoticesBulk(coinoneScrapComponent.getMarketType(), coinoneNoticeParsedDataList);
            log.info("Coinone 공지사항 {} 개 초기화 완료", coinoneNoticeParsedDataList.size());
        } else {
            log.warn("Coinone 공지사항 데이터가 비어있음");
        }
    }

    /**
     * Binance 공지사항 초기화 (현재 주석 처리됨)
     */
    private void initializeBinanceNotices() throws IOException {
        log.debug("Binance 공지사항 초기화 시작");
        List<NoticeParsedData> binanceNoticeParsedDataList = binanceScrapComponent.parseNoticeData();
        
        if (!binanceNoticeParsedDataList.isEmpty()) {
            binanceScrapComponent.setNoticeToRedis(binanceNoticeParsedDataList);
            binanceScrapComponent.setNewParsedData(binanceNoticeParsedDataList);
            exchangeNoticePacadeService.createNoticesBulk(binanceScrapComponent.getMarketType(), binanceNoticeParsedDataList);
            log.info("Binance 공지사항 {} 개 초기화 완료", binanceNoticeParsedDataList.size());
        } else {
            log.warn("Binance 공지사항 데이터가 비어있음");
        }
    }

    /**
     * Bithumb 공지사항 초기화 (현재 주석 처리됨)
     */
    private void initializeBithumbNotices() throws IOException {
        log.debug("Bithumb 공지사항 초기화 시작");
        List<NoticeParsedData> bithumbNoticeParsedDataList = bithumbScrapComponent.parseNoticeData();
        
        if (!bithumbNoticeParsedDataList.isEmpty()) {
            bithumbScrapComponent.setNoticeToRedis(bithumbNoticeParsedDataList);
            bithumbScrapComponent.setNewParsedData(bithumbNoticeParsedDataList);
            exchangeNoticePacadeService.createNoticesBulk(bithumbScrapComponent.getMarketType(), bithumbNoticeParsedDataList);
            log.info("Bithumb 공지사항 {} 개 초기화 완료", bithumbNoticeParsedDataList.size());
        } else {
            log.warn("Bithumb 공지사항 데이터가 비어있음");
        }
    }

    @PostConstruct
    private void categoryServiceInit(){
        String[] initCategory = {"전체","코인","보안","기타"};

        for(String category : initCategory){
            this.categoryService.createCategory(new CreateCategoryRequestDto(category));
        }
    }
}
