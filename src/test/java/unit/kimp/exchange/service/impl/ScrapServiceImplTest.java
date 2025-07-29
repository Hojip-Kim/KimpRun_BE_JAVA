package unit.kimp.exchange.service.impl;

import kimp.exchange.component.ExchangeScrap;
import kimp.exchange.component.impl.exchange.ExchangeScrapAbstract;
import kimp.exchange.dto.binance.BinanceNoticeDto;
import kimp.exchange.dto.bithumb.BithumbNoticeDto;
import kimp.exchange.dto.coinone.CoinoneNoticeDto;
import kimp.exchange.dto.upbit.UpbitNoticeDto;
import kimp.exchange.service.ExchangeService;
import kimp.exchange.service.impl.ExchangeNoticePacadeService;
import kimp.exchange.service.impl.ScrapServiceImpl;
import kimp.market.Enum.MarketType;
import kimp.market.handler.MarketInfoHandler;
import kimp.notice.dto.notice.NoticeParsedData;
import kimp.notice.dto.notice.NoticeDto;
import kimp.notice.service.NoticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("ScrapService 구현체 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class ScrapServiceImplTest {

    @Mock
    private ExchangeScrapAbstract<UpbitNoticeDto> upbitScrapComponent;

    @Mock
    private ExchangeScrapAbstract<BithumbNoticeDto> bithumbScrapComponent;

    @Mock
    private ExchangeScrapAbstract<CoinoneNoticeDto> coinoneScrapComponent;

    @Mock
    private ExchangeScrapAbstract<BinanceNoticeDto> binanceScrapComponent;

    @Mock
    private ExchangeService exchangeService;

    @Mock
    private MarketInfoHandler marketInfoHandler;

    @Mock
    private ExchangeNoticePacadeService exchangeNoticePacadeService;

    @Mock
    private NoticeService noticeService;

    private ScrapServiceImpl scrapService;

    private List<NoticeParsedData> upbitNoticeParsedDataList;
    private List<NoticeParsedData> coinoneNoticeParsedDataList;
    private List<NoticeParsedData> upbitNewNoticeList;
    private List<NoticeParsedData> coinoneNewNoticeList;
    private NoticeDto noticeDto;

    @BeforeEach
    void setup() {
        // Setup test data
        upbitNoticeParsedDataList = new ArrayList<>();
        upbitNoticeParsedDataList.add(new NoticeParsedData("Upbit Notice 1", "https://upbit.com/notice/1", LocalDateTime.now()));
        upbitNoticeParsedDataList.add(new NoticeParsedData("Upbit Notice 2", "https://upbit.com/notice/2", LocalDateTime.now()));

        coinoneNoticeParsedDataList = new ArrayList<>();
        coinoneNoticeParsedDataList.add(new NoticeParsedData("Coinone Notice 1", "https://coinone.co.kr/notice/1", LocalDateTime.now()));
        coinoneNoticeParsedDataList.add(new NoticeParsedData("Coinone Notice 2", "https://coinone.co.kr/notice/2", LocalDateTime.now()));

        upbitNewNoticeList = new ArrayList<>();
        upbitNewNoticeList.add(new NoticeParsedData("Upbit New Notice", "https://upbit.com/notice/new", LocalDateTime.now()));

        coinoneNewNoticeList = new ArrayList<>();
        coinoneNewNoticeList.add(new NoticeParsedData("Coinone New Notice", "https://coinone.co.kr/notice/new", LocalDateTime.now()));

        noticeDto = new NoticeDto(1L, MarketType.UPBIT, "Upbit New Notice", "https://upbit.com/notice/new", LocalDateTime.now());

        // Create ScrapServiceImpl instance with mocked dependencies
        scrapService = new ScrapServiceImpl(
            upbitScrapComponent,
            bithumbScrapComponent,
            coinoneScrapComponent,
            binanceScrapComponent,
            exchangeNoticePacadeService,
            noticeService,
            marketInfoHandler
        );
    }

    @Test
    @DisplayName("스크랩 서비스 테스트 - Upbit에 새로운 공지사항이 있을 때")
    void shouldScrapNoticeDataWhenNewUpbitNoticeExists() throws IOException {
        // Given
        doReturn(upbitNoticeParsedDataList).when(upbitScrapComponent).parseNoticeData();
        doReturn(coinoneNoticeParsedDataList).when(coinoneScrapComponent).parseNoticeData();

        doReturn("old-hash").when(upbitScrapComponent).getNoticeFromRedis();
        doReturn(true).when(upbitScrapComponent).isUpdatedNotice(anyString(), anyList());
        doReturn(upbitNoticeParsedDataList).when(upbitScrapComponent).getNoticeData(); // Mock existing notice data
        doReturn(upbitNewNoticeList).when(upbitScrapComponent).getNewNotice(anyList());
        doReturn(MarketType.UPBIT).when(upbitScrapComponent).getMarketType();

        doReturn("old-hash").when(coinoneScrapComponent).getNoticeFromRedis();
        doReturn(false).when(coinoneScrapComponent).isUpdatedNotice(anyString(), anyList());

        when(noticeService.getNoticeByLink(anyString())).thenReturn(noticeDto);
        doNothing().when(marketInfoHandler).sendNewNotice(any(NoticeDto.class));
        when(exchangeNoticePacadeService.createNoticesBulk(any(MarketType.class), anyList())).thenReturn(true);

        // When
        scrapService.scrapNoticeData();

        // Then
        verify(upbitScrapComponent, times(1)).parseNoticeData();
        verify(coinoneScrapComponent, times(1)).parseNoticeData();
        verify(upbitScrapComponent, times(1)).getNoticeFromRedis();
        verify(upbitScrapComponent, times(1)).isUpdatedNotice(anyString(), anyList());
        verify(upbitScrapComponent, times(1)).getNewNotice(anyList());
        verify(upbitScrapComponent, times(1)).setNoticeToRedis(anyList());
        verify(upbitScrapComponent, times(1)).setNewParsedData(anyList());
        verify(upbitScrapComponent, times(1)).setNewNotice(anyList());
        verify(exchangeNoticePacadeService, times(1)).createNoticesBulk(any(MarketType.class), anyList());
        verify(noticeService, times(1)).getNoticeByLink(anyString());
        verify(marketInfoHandler, times(1)).sendNewNotice(any(NoticeDto.class));

        verify(coinoneScrapComponent, times(1)).getNoticeFromRedis();
        verify(coinoneScrapComponent, times(1)).isUpdatedNotice(anyString(), anyList());
        verify(coinoneScrapComponent, never()).getNewNotice(anyList());
    }

    @Test
    @DisplayName("스크랩 서비스 테스트 - Coinone에 새로운 공지사항이 있을 때")
    void shouldScrapNoticeDataWhenNewCoinoneNoticeExists() throws IOException {
        // Given
        doReturn(upbitNoticeParsedDataList).when(upbitScrapComponent).parseNoticeData();
        doReturn(coinoneNoticeParsedDataList).when(coinoneScrapComponent).parseNoticeData();

        doReturn("old-hash").when(upbitScrapComponent).getNoticeFromRedis();
        doReturn(false).when(upbitScrapComponent).isUpdatedNotice(anyString(), anyList());

        doReturn("old-hash").when(coinoneScrapComponent).getNoticeFromRedis();
        doReturn(true).when(coinoneScrapComponent).isUpdatedNotice(anyString(), anyList());
        doReturn(coinoneNoticeParsedDataList).when(coinoneScrapComponent).getNoticeData(); // Mock existing notice data
        doReturn(coinoneNewNoticeList).when(coinoneScrapComponent).getNewNotice(anyList());
        doReturn(MarketType.COINONE).when(coinoneScrapComponent).getMarketType();

        NoticeDto coinoneNoticeDto = new NoticeDto(2L, MarketType.COINONE, "Coinone New Notice", "https://coinone.co.kr/notice/new", LocalDateTime.now());
        when(noticeService.getNoticeByLink(anyString())).thenReturn(coinoneNoticeDto);
        doNothing().when(marketInfoHandler).sendNewNotice(any(NoticeDto.class));
        when(exchangeNoticePacadeService.createNoticesBulk(any(MarketType.class), anyList())).thenReturn(true);

        // When
        scrapService.scrapNoticeData();

        // Then
        verify(upbitScrapComponent, times(1)).parseNoticeData();
        verify(coinoneScrapComponent, times(1)).parseNoticeData();
        verify(upbitScrapComponent, times(1)).getNoticeFromRedis();
        verify(upbitScrapComponent, times(1)).isUpdatedNotice(anyString(), anyList());
        verify(upbitScrapComponent, never()).getNewNotice(anyList());

        verify(coinoneScrapComponent, times(1)).getNoticeFromRedis();
        verify(coinoneScrapComponent, times(1)).isUpdatedNotice(anyString(), anyList());
        verify(coinoneScrapComponent, times(1)).getNewNotice(anyList());
        verify(coinoneScrapComponent, times(1)).setNoticeToRedis(anyList());
        verify(coinoneScrapComponent, times(1)).setNewParsedData(anyList());
        verify(coinoneScrapComponent, times(1)).setNewNotice(anyList());
        verify(exchangeNoticePacadeService, times(1)).createNoticesBulk(eq(MarketType.COINONE), anyList());
        verify(noticeService, times(1)).getNoticeByLink(anyString());
        verify(marketInfoHandler, times(1)).sendNewNotice(any(NoticeDto.class));
    }

    @Test
    @DisplayName("스크랩 서비스 테스트 - 새로운 공지사항이 없을 때")
    void shouldScrapNoticeDataWhenNoNewNoticeExists() throws IOException {
        // Given
        doReturn(upbitNoticeParsedDataList).when(upbitScrapComponent).parseNoticeData();
        doReturn(coinoneNoticeParsedDataList).when(coinoneScrapComponent).parseNoticeData();

        doReturn("old-hash").when(upbitScrapComponent).getNoticeFromRedis();
        doReturn(false).when(upbitScrapComponent).isUpdatedNotice(anyString(), anyList());

        doReturn("old-hash").when(coinoneScrapComponent).getNoticeFromRedis();
        doReturn(false).when(coinoneScrapComponent).isUpdatedNotice(anyString(), anyList());

        // When
        scrapService.scrapNoticeData();

        // Then
        verify(upbitScrapComponent, times(1)).parseNoticeData();
        verify(coinoneScrapComponent, times(1)).parseNoticeData();
        verify(upbitScrapComponent, times(1)).getNoticeFromRedis();
        verify(upbitScrapComponent, times(1)).isUpdatedNotice(anyString(), anyList());
        verify(upbitScrapComponent, never()).getNewNotice(anyList());
        verify(upbitScrapComponent, never()).setNoticeToRedis(anyList());

        verify(coinoneScrapComponent, times(1)).getNoticeFromRedis();
        verify(coinoneScrapComponent, times(1)).isUpdatedNotice(anyString(), anyList());
        verify(coinoneScrapComponent, never()).getNewNotice(anyList());
        verify(coinoneScrapComponent, never()).setNoticeToRedis(anyList());

        verify(exchangeNoticePacadeService, never()).createNoticesBulk(any(MarketType.class), anyList());
        verify(noticeService, never()).getNoticeByLink(anyString());
        verify(marketInfoHandler, never()).sendNewNotice(any(NoticeDto.class));
    }
}
