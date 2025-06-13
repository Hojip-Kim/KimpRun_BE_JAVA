package kimp.test;

import jakarta.annotation.PostConstruct;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.service.CategoryService;
import kimp.exchange.component.ExchangeScrap;
import kimp.exchange.component.impl.exchange.ExchangeScrapAbstract;
import kimp.exchange.dto.binance.BinanceNoticeDto;
import kimp.exchange.dto.bithumb.BithumbNoticeDto;
import kimp.exchange.dto.coinone.CoinoneNoticeDto;
import kimp.exchange.dto.notice.NoticeParsedData;
import kimp.exchange.dto.upbit.UpbitNoticeDto;
import kimp.exchange.service.ExchangeService;
import kimp.exchange.service.NoticeService;
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
    private void init() throws IOException {

        List<NoticeParsedData> upbitNoticeParsedDataList = upbitScrapComponent.parseNoticeData();
        upbitScrapComponent.setNoticeToRedis(upbitNoticeParsedDataList);
        upbitScrapComponent.setNewParsedData(upbitNoticeParsedDataList);
        exchangeNoticePacadeService.createNoticesBulk(upbitScrapComponent.getMarketType(), upbitNoticeParsedDataList);

//        List<NoticeParsedData> binanceNoticeParsedDataList = binanceScrapComponent.parseNoticeData();
//        if(!binanceNoticeParsedDataList.isEmpty()) {
//            binanceScrapComponent.setNoticeToRedis(binanceNoticeParsedDataList);
//            binanceScrapComponent.setNewParsedData(binanceNoticeParsedDataList);
//            exchangeNoticePacadeService.createNoticesBulk(binanceScrapComponent.getMarketType(), binanceNoticeParsedDataList);
//        }

        List<NoticeParsedData> coinoneNoticeParsedDataList = coinoneScrapComponent.parseNoticeData();
        coinoneScrapComponent.setNoticeToRedis(coinoneNoticeParsedDataList);
        coinoneScrapComponent.setNewParsedData(coinoneNoticeParsedDataList);
        exchangeNoticePacadeService.createNoticesBulk(coinoneScrapComponent.getMarketType(), coinoneNoticeParsedDataList);

        List<NoticeParsedData> bithumbNoticeParsedDataList = bithumbScrapComponent.parseNoticeData();
        bithumbScrapComponent.setNoticeToRedis(bithumbNoticeParsedDataList);
        bithumbScrapComponent.setNewParsedData(bithumbNoticeParsedDataList);
        exchangeNoticePacadeService.createNoticesBulk(bithumbScrapComponent.getMarketType(), bithumbNoticeParsedDataList);

        log.info("스크랩서비스 초기 데이터 세팅 완료");
    }

    @PostConstruct
    private void categoryServiceInit(){
        String[] initCategory = {"전체","코인","보안","기타"};

        for(String category : initCategory){
            this.categoryService.createCategory(new CreateCategoryRequestDto(category));
        }

    }
}
