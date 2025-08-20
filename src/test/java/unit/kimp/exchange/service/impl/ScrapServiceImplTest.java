package unit.kimp.exchange.service.impl;

import kimp.scrap.component.impl.exchange.ExchangeScrapAbstract;
import kimp.scrap.dto.binance.BinanceNoticeDto;
import kimp.scrap.dto.bithumb.BithumbNoticeDto;
import kimp.scrap.dto.coinone.CoinoneNoticeDto;
import kimp.scrap.dto.upbit.UpbitNoticeDto;
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
    @DisplayName("스크랩 서비스 테스트 - Upbit에 새로운 공지사항이 있을 때 (DB 기반)")
    void shouldScrapNoticeDataWhenNewUpbitNoticeExists() throws IOException {
        // Given
        doReturn(upbitNoticeParsedDataList).when(upbitScrapComponent).parseNoticeData();
        doReturn(coinoneNoticeParsedDataList).when(coinoneScrapComponent).parseNoticeData();
        
        doReturn(MarketType.UPBIT).when(upbitScrapComponent).getMarketType();
        doReturn(MarketType.COINONE).when(coinoneScrapComponent).getMarketType();
        
        // DB에서 최신 날짜 조회 - Upbit는 새로운 공지사항이 있도록 설정
        LocalDateTime yesterdayDate = LocalDateTime.now().minusDays(1);
        when(noticeService.getLatestNoticeDate(MarketType.UPBIT)).thenReturn(yesterdayDate);
        when(noticeService.getLatestNoticeDate(MarketType.COINONE)).thenReturn(LocalDateTime.now().plusHours(1)); // 새로운 공지사항 없음
        
        when(noticeService.getNoticeByLink(anyString())).thenReturn(noticeDto);
        doNothing().when(marketInfoHandler).sendNewNotice(any(NoticeDto.class));
        when(exchangeNoticePacadeService.createNoticesBulk(any(MarketType.class), anyList())).thenReturn(true);

        // When
        scrapService.scrapNoticeData();

        // Then - DB 기반 로직 검증
        verify(upbitScrapComponent, times(1)).parseNoticeData();
        verify(coinoneScrapComponent, times(1)).parseNoticeData();
        
        // DB 날짜 조회 검증
        verify(noticeService, times(1)).getLatestNoticeDate(MarketType.UPBIT);
        verify(noticeService, times(1)).getLatestNoticeDate(MarketType.COINONE);
        
        // Upbit만 새로운 공지사항 처리
        verify(upbitScrapComponent, times(1)).setNewParsedData(anyList());
        verify(upbitScrapComponent, times(1)).setNewNotice(anyList());
        verify(exchangeNoticePacadeService, times(1)).createNoticesBulk(eq(MarketType.UPBIT), anyList());
        verify(noticeService, atLeastOnce()).getNoticeByLink(anyString());
        verify(marketInfoHandler, atLeastOnce()).sendNewNotice(any(NoticeDto.class));
        
        // Coinone은 새로운 공지사항이 없으므로 처리 없음
        verify(coinoneScrapComponent, never()).setNewNotice(anyList());
    }

    @Test
    @DisplayName("스크랩 서비스 테스트 - Coinone에 새로운 공지사항이 있을 때 (DB 기반)")
    void shouldScrapNoticeDataWhenNewCoinoneNoticeExists() throws IOException {
        // Given
        doReturn(upbitNoticeParsedDataList).when(upbitScrapComponent).parseNoticeData();
        doReturn(coinoneNoticeParsedDataList).when(coinoneScrapComponent).parseNoticeData();
        
        doReturn(MarketType.UPBIT).when(upbitScrapComponent).getMarketType();
        doReturn(MarketType.COINONE).when(coinoneScrapComponent).getMarketType();
        
        // DB에서 최신 날짜 조회 - Coinone만 새로운 공지사항이 있도록 설정
        LocalDateTime yesterdayDate = LocalDateTime.now().minusDays(1);
        when(noticeService.getLatestNoticeDate(MarketType.UPBIT)).thenReturn(LocalDateTime.now().plusHours(1)); // 새로운 공지사항 없음
        when(noticeService.getLatestNoticeDate(MarketType.COINONE)).thenReturn(yesterdayDate); // 새로운 공지사항 있음
        
        NoticeDto coinoneNoticeDto = new NoticeDto(2L, MarketType.COINONE, "Coinone New Notice", "https://coinone.co.kr/notice/new", LocalDateTime.now());
        when(noticeService.getNoticeByLink(anyString())).thenReturn(coinoneNoticeDto);
        doNothing().when(marketInfoHandler).sendNewNotice(any(NoticeDto.class));
        when(exchangeNoticePacadeService.createNoticesBulk(any(MarketType.class), anyList())).thenReturn(true);

        // When
        scrapService.scrapNoticeData();

        // Then - DB 기반 로직 검증
        verify(upbitScrapComponent, times(1)).parseNoticeData();
        verify(coinoneScrapComponent, times(1)).parseNoticeData();
        
        // DB 날짜 조회 검증
        verify(noticeService, times(1)).getLatestNoticeDate(MarketType.UPBIT);
        verify(noticeService, times(1)).getLatestNoticeDate(MarketType.COINONE);
        
        // Coinone만 새로운 공지사항 처리
        verify(coinoneScrapComponent, times(1)).setNewParsedData(anyList());
        verify(coinoneScrapComponent, times(1)).setNewNotice(anyList());
        verify(exchangeNoticePacadeService, times(1)).createNoticesBulk(eq(MarketType.COINONE), anyList());
        verify(noticeService, atLeastOnce()).getNoticeByLink(anyString());
        verify(marketInfoHandler, atLeastOnce()).sendNewNotice(any(NoticeDto.class));
        
        // Upbit은 새로운 공지사항이 없으므로 처리 없음
        verify(upbitScrapComponent, never()).setNewNotice(anyList());
    }

    @Test
    @DisplayName("스크랩 서비스 테스트 - 새로운 공지사항이 없을 때 (DB 기반)")
    void shouldScrapNoticeDataWhenNoNewNoticeExists() throws IOException {
        // Given
        doReturn(upbitNoticeParsedDataList).when(upbitScrapComponent).parseNoticeData();
        doReturn(coinoneNoticeParsedDataList).when(coinoneScrapComponent).parseNoticeData();
        
        doReturn(MarketType.UPBIT).when(upbitScrapComponent).getMarketType();
        doReturn(MarketType.COINONE).when(coinoneScrapComponent).getMarketType();
        
        // DB에서 최신 날짜 조회 - 두 거래소 모두 새로운 공지사항 없도록 설정
        LocalDateTime futureDate = LocalDateTime.now().plusHours(1);
        when(noticeService.getLatestNoticeDate(MarketType.UPBIT)).thenReturn(futureDate);
        when(noticeService.getLatestNoticeDate(MarketType.COINONE)).thenReturn(futureDate);

        // When
        scrapService.scrapNoticeData();

        // Then - DB 기반 로직 검증
        verify(upbitScrapComponent, times(1)).parseNoticeData();
        verify(coinoneScrapComponent, times(1)).parseNoticeData();
        
        // DB 날짜 조회 검증
        verify(noticeService, times(1)).getLatestNoticeDate(MarketType.UPBIT);
        verify(noticeService, times(1)).getLatestNoticeDate(MarketType.COINONE);
        
        // 둘 다 새로운 공지사항이 없으므로 새로운 공지사항 처리 안 함
        verify(upbitScrapComponent, never()).setNewNotice(anyList());
        verify(coinoneScrapComponent, never()).setNewNotice(anyList());
        
        verify(exchangeNoticePacadeService, never()).createNoticesBulk(any(MarketType.class), anyList());
        verify(noticeService, never()).getNoticeByLink(anyString());
        verify(marketInfoHandler, never()).sendNewNotice(any(NoticeDto.class));
    }
}
