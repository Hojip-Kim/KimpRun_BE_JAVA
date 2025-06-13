package kimp.exchange.service.impl;

import kimp.exchange.dto.notice.NoticeDto;
import kimp.exchange.dto.notice.NoticeParsedData;
import kimp.exchange.component.ExchangeScrap;
import kimp.exchange.component.impl.exchange.ExchangeScrapAbstract;
import kimp.exchange.dto.binance.BinanceNoticeDto;
import kimp.exchange.dto.bithumb.BithumbNoticeDto;
import kimp.exchange.dto.coinone.CoinoneNoticeDto;
import kimp.exchange.dto.upbit.UpbitNoticeDto;
import kimp.exchange.service.ExchangeService;
import kimp.exchange.service.NoticeService;
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
    private final ExchangeService exchangeService;
    private final MarketInfoHandler marketInfoHandler;
    private final ExchangeNoticePacadeService exchangeNoticePacadeService;
    private final NoticeService noticeService;

    public ScrapServiceImpl(ExchangeScrapAbstract<UpbitNoticeDto> upbitScrap, ExchangeScrapAbstract<BithumbNoticeDto> bithumbScrap, ExchangeScrapAbstract<CoinoneNoticeDto> coinoneScrap, ExchangeScrapAbstract<BinanceNoticeDto> binanceScrap, ExchangeService exchangeService, MarketInfoHandler marketInfoHandler, ExchangeNoticePacadeService exchangeNoticePacadeService, NoticeService noticeService) {
        this.upbitScrapComponent = upbitScrap;
        this.bithumbScrapComponent = bithumbScrap;
        this.coinoneScrapComponent = coinoneScrap;
        this.binanceScrapComponent = binanceScrap;
        this.exchangeService = exchangeService;
        this.marketInfoHandler = marketInfoHandler;
        this.exchangeNoticePacadeService = exchangeNoticePacadeService;
        this.noticeService = noticeService;
    }

//     거래소 별 최신 공지사항을 뽑아내는 메서드
//     현재 ip block방지로 10초단위로
    @Scheduled(fixedRate = 10000)
    public void scrapNoticeData() throws IOException {
        log.info("스케쥴링 실행");
        List<NoticeParsedData> upbitNoticeParsedDataList = upbitScrapComponent.parseNoticeData();
//        List<NoticeParsedData> binanceNoticeParsedDataList = binanceScrapComponent.parseNoticeData();
//        List<NoticeParsedData> bithumbNoticeParsedDataList = bithumbScrapComponent.parseNoticeData();
        List<NoticeParsedData> coinoneNoticeParsedDataList = coinoneScrapComponent.parseNoticeData();

        //새로운 공지사항데이터
        List<NoticeParsedData> upbitNewNotice = null;
//        List<NoticeParsedData> bithumbNewNotice = null;
        List<NoticeParsedData> coinoneNewNotice = null;
//        List<NoticeParsedData> binanceNewNotice = null;


        // 새로운 공지사항이 생긴다면
        if(upbitScrapComponent.isUpdatedNotice(upbitScrapComponent.getNoticeFromRedis(), upbitNoticeParsedDataList)) {
            upbitNewNotice = upbitScrapComponent.getNewNotice(upbitNoticeParsedDataList);

            upbitScrapComponent.setNoticeToRedis(upbitNoticeParsedDataList);
            upbitScrapComponent.setNewParsedData(upbitNoticeParsedDataList);
            upbitScrapComponent.setNewNotice(upbitNewNotice);

            exchangeNoticePacadeService.createNoticesBulk(upbitScrapComponent.getMarketType(), upbitScrapComponent.getFieldNewNotice());
            // websocket 실시간 데이터 전송
            for(int i = 0; i < upbitNewNotice.size(); i++) {
                String link = upbitNewNotice.get(i).getAlink();
                NoticeDto noticeDto = noticeService.getNoticeByLink(link);
                marketInfoHandler.sendNewNotice(noticeDto);
            }

            log.info("새로운 공지사항 발생!!!!!! 업비트" + upbitNewNotice.get(0).getTitle());
        }
//        if(binanceScrapComponent.isUpdatedNotice(binanceScrapComponent.getNoticeFromRedis(), binanceNoticeParsedDataList)) {
//            binanceNewNotice = binanceScrapComponent.getNewNotice(binanceNoticeParsedDataList);
//
//            binanceScrapComponent.setNoticeToRedis(binanceNoticeParsedDataList);
//            binanceScrapComponent.setNewParsedData(binanceNoticeParsedDataList);
//            binanceScrapComponent.setNewNotice(binanceNewNotice);
//            exchangeNoticePacadeService.createNoticesBulk(binanceScrapComponent.getMarketType(), binanceScrapComponent.getFieldNewNotice());
//            // websocket 실시간 데이터 전송
//            for(int i = 0; i < binanceNewNotice.size(); i++) {
//                String link = binanceNewNotice.get(i).getAlink();
//                NoticeDto noticeDto = noticeService.getNoticeByLink(link);
//                marketInfoHandler.sendNewNotice(noticeDto);
//            }
//
//            log.info("새로운 공지사항 발생!!!!!! 바이낸스" + binanceNewNotice.get(0).getTitle());
//        }

        // 빗썸은 첫번째 api에서 공지사항의 시간/분 정보가 안나오므로 추가 작업이 필요함.
//        if(bithumbScrapComponent.isUpdatedNotice(bithumbScrapComponent.getNoticeFromRedis(), bithumbNoticeParsedDataList)) {
//            bithumbNewNotice = bithumbScrapComponent.getNewNotice(bithumbNoticeParsedDataList);
//
//            bithumbScrapComponent.setNoticeToRedis(bithumbNoticeParsedDataList);
//            bithumbScrapComponent.setNewParsedData(bithumbNoticeParsedDataList);
//            bithumbScrapComponent.setNewNotice(bithumbNewNotice);
//            // websocket 실시간 데이터 전송
//
//            exchangeNoticePacadeService.createNoticesBulk(bithumbScrapComponent.getMarketType(), bithumbScrapComponent.getFieldNewNotice());
//
//            for(int i = 0; i < bithumbNewNotice.size(); i++) {
//                String link = bithumbNewNotice.get(i).getAlink();
//                NoticeDto noticeDto = noticeService.getNoticeByLink(link);
//                marketInfoHandler.sendNewNotice(noticeDto);
//            }
//
//            log.info("새로운 공지사항 발생!!!!!! 빗썸" + bithumbNewNotice.get(0).getTitle());
//        }
        if(coinoneScrapComponent.isUpdatedNotice(coinoneScrapComponent.getNoticeFromRedis(), coinoneNoticeParsedDataList)) {
            coinoneNewNotice = coinoneScrapComponent.getNewNotice(coinoneNoticeParsedDataList);

            coinoneScrapComponent.setNoticeToRedis(coinoneNoticeParsedDataList);
            coinoneScrapComponent.setNewParsedData(coinoneNoticeParsedDataList);
            coinoneScrapComponent.setNewNotice(coinoneNewNotice);
            // websocket 실시간 데이터 전송

            exchangeNoticePacadeService.createNoticesBulk(coinoneScrapComponent.getMarketType(), coinoneScrapComponent.getFieldNewNotice());

            for(int i = 0; i < coinoneNewNotice.size(); i++) {
                String link = coinoneNewNotice.get(i).getAlink();
                NoticeDto noticeDto = noticeService.getNoticeByLink(link);
                marketInfoHandler.sendNewNotice(noticeDto);
            }

            log.info("새로운 공지사항 발생!!!!!! 코인원" + coinoneNewNotice.get(0).getTitle());
        }

    }

}