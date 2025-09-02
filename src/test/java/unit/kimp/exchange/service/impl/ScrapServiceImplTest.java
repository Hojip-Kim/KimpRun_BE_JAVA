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
import kimp.market.controller.MarketInfoStompController;
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
    private MarketInfoStompController marketInfoStompController;

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
        LocalDateTime fixedDate = LocalDateTime.now().minusDays(1); // 어제 날짜로 설정
        
        upbitNoticeParsedDataList = new ArrayList<>();
        upbitNoticeParsedDataList.add(new NoticeParsedData("Upbit Notice 1", "https://upbit.com/notice/1", fixedDate));
        upbitNoticeParsedDataList.add(new NoticeParsedData("Upbit Notice 2", "https://upbit.com/notice/2", fixedDate));

        coinoneNoticeParsedDataList = new ArrayList<>();
        coinoneNoticeParsedDataList.add(new NoticeParsedData("Coinone Notice 1", "https://coinone.co.kr/notice/1", fixedDate));
        coinoneNoticeParsedDataList.add(new NoticeParsedData("Coinone Notice 2", "https://coinone.co.kr/notice/2", fixedDate));

        upbitNewNoticeList = new ArrayList<>();
        upbitNewNoticeList.add(new NoticeParsedData("Upbit New Notice", "https://upbit.com/notice/new", fixedDate));

        coinoneNewNoticeList = new ArrayList<>();
        coinoneNewNoticeList.add(new NoticeParsedData("Coinone New Notice", "https://coinone.co.kr/notice/new", fixedDate));

        noticeDto = new NoticeDto(1L, MarketType.UPBIT, "Upbit New Notice", "https://upbit.com/notice/new", fixedDate);

        // Create ScrapServiceImpl instance with mocked dependencies
        scrapService = new ScrapServiceImpl(
            upbitScrapComponent,
            bithumbScrapComponent,
            coinoneScrapComponent,
            binanceScrapComponent,
            exchangeNoticePacadeService,
            noticeService,
            marketInfoStompController
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
        
        // DB에서 모든 공지사항 조회 - 새로운 로직에 맞춰 설정
        LocalDateTime fixedDate = LocalDateTime.now().minusDays(1);
        List<NoticeDto> upbitDbNotices = List.of(); // 빈 리스트 = 모든 스크래핑 데이터가 새로운 것
        NoticeDto coinoneExistingNotice1 = new NoticeDto(2L, MarketType.COINONE, "Coinone Notice 1", "https://coinone.co.kr/notice/1", fixedDate);
        NoticeDto coinoneExistingNotice2 = new NoticeDto(3L, MarketType.COINONE, "Coinone Notice 2", "https://coinone.co.kr/notice/2", fixedDate);
        List<NoticeDto> coinoneDbNotices = List.of(coinoneExistingNotice1, coinoneExistingNotice2); // 모든 URL이 일치하는 기존 공지사항
        when(noticeService.getAllNoticesByMarketType(MarketType.UPBIT)).thenReturn(upbitDbNotices);
        when(noticeService.getAllNoticesByMarketType(MarketType.COINONE)).thenReturn(coinoneDbNotices);
        
        when(noticeService.getNoticeByLink(anyString())).thenReturn(noticeDto);
        doNothing().when(marketInfoStompController).sendNewNotice(any(NoticeDto.class));
        when(exchangeNoticePacadeService.createNoticesBulk(any(MarketType.class), anyList())).thenReturn(true);

        // When
        scrapService.scrapNoticeData();

        // Then - DB 기반 로직 검증
        verify(upbitScrapComponent, times(1)).parseNoticeData();
        verify(coinoneScrapComponent, times(1)).parseNoticeData();
        
        // DB 전체 공지사항 조회 검증 (새로운 로직)
        verify(noticeService, times(1)).getAllNoticesByMarketType(MarketType.UPBIT);
        verify(noticeService, times(1)).getAllNoticesByMarketType(MarketType.COINONE);
        
        // Upbit만 새로운 공지사항 처리
        verify(upbitScrapComponent, times(1)).setNewParsedData(anyList());
        verify(upbitScrapComponent, times(1)).setNewNotice(anyList());
        verify(exchangeNoticePacadeService, times(1)).createNoticesBulk(eq(MarketType.UPBIT), anyList());
        verify(noticeService, atLeastOnce()).getNoticeByLink(anyString());
        verify(marketInfoStompController, atLeastOnce()).sendNewNotice(any(NoticeDto.class));
        
        // Coinone은 URL이 일치하므로 새로운 공지사항이 없음 - 변경사항 없음으로 처리
        verify(coinoneScrapComponent, never()).setNewParsedData(anyList());
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
        
        // DB에서 모든 공지사항 조회 - Coinone만 새로운 공지사항이 있도록 설정
        LocalDateTime fixedDate = LocalDateTime.now().minusDays(1);
        NoticeDto upbitExistingNotice1 = new NoticeDto(1L, MarketType.UPBIT, "Upbit Notice 1", "https://upbit.com/notice/1", fixedDate);
        NoticeDto upbitExistingNotice2 = new NoticeDto(2L, MarketType.UPBIT, "Upbit Notice 2", "https://upbit.com/notice/2", fixedDate);  
        List<NoticeDto> upbitDbNotices = List.of(upbitExistingNotice1, upbitExistingNotice2); // UPBIT는 모든 URL이 일치하는 기존 공지사항
        List<NoticeDto> coinoneDbNotices = List.of(); // COINONE은 빈 리스트 = 모든 스크래핑 데이터가 새로운 것
        when(noticeService.getAllNoticesByMarketType(MarketType.UPBIT)).thenReturn(upbitDbNotices);
        when(noticeService.getAllNoticesByMarketType(MarketType.COINONE)).thenReturn(coinoneDbNotices);
        
        NoticeDto coinoneNoticeDto = new NoticeDto(2L, MarketType.COINONE, "Coinone New Notice", "https://coinone.co.kr/notice/new", LocalDateTime.now());
        when(noticeService.getNoticeByLink(anyString())).thenReturn(coinoneNoticeDto);
        doNothing().when(marketInfoStompController).sendNewNotice(any(NoticeDto.class));
        when(exchangeNoticePacadeService.createNoticesBulk(any(MarketType.class), anyList())).thenReturn(true);

        // When
        scrapService.scrapNoticeData();

        // Then - DB 기반 로직 검증
        verify(upbitScrapComponent, times(1)).parseNoticeData();
        verify(coinoneScrapComponent, times(1)).parseNoticeData();
        
        // DB 전체 공지사항 조회 검증 (새로운 로직)
        verify(noticeService, times(1)).getAllNoticesByMarketType(MarketType.UPBIT);
        verify(noticeService, times(1)).getAllNoticesByMarketType(MarketType.COINONE);
        
        // Coinone만 새로운 공지사항 처리
        verify(coinoneScrapComponent, times(1)).setNewParsedData(anyList());
        verify(coinoneScrapComponent, times(1)).setNewNotice(anyList());
        verify(exchangeNoticePacadeService, times(1)).createNoticesBulk(eq(MarketType.COINONE), anyList());
        verify(noticeService, atLeastOnce()).getNoticeByLink(anyString());
        verify(marketInfoStompController, atLeastOnce()).sendNewNotice(any(NoticeDto.class));
        
        // Upbit은 URL이 일치하므로 새로운 공지사항이 없음 - 변경사항 없음으로 처리
        verify(upbitScrapComponent, never()).setNewParsedData(anyList());
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
        
        // DB에서 모든 공지사항 조회 - 두 거래소 모두 기존 공지사항이 있어서 새로운 공지사항 없도록 설정
        LocalDateTime fixedDate = LocalDateTime.now().minusDays(1);
        NoticeDto upbitExistingNotice1 = new NoticeDto(1L, MarketType.UPBIT, "Upbit Notice 1", "https://upbit.com/notice/1", fixedDate);
        NoticeDto upbitExistingNotice2 = new NoticeDto(2L, MarketType.UPBIT, "Upbit Notice 2", "https://upbit.com/notice/2", fixedDate);
        NoticeDto coinoneExistingNotice1 = new NoticeDto(3L, MarketType.COINONE, "Coinone Notice 1", "https://coinone.co.kr/notice/1", fixedDate);
        NoticeDto coinoneExistingNotice2 = new NoticeDto(4L, MarketType.COINONE, "Coinone Notice 2", "https://coinone.co.kr/notice/2", fixedDate);
        List<NoticeDto> upbitDbNotices = List.of(upbitExistingNotice1, upbitExistingNotice2); // 모든 URL이 일치하는 기존 공지사항
        List<NoticeDto> coinoneDbNotices = List.of(coinoneExistingNotice1, coinoneExistingNotice2); // 모든 URL이 일치하는 기존 공지사항
        when(noticeService.getAllNoticesByMarketType(MarketType.UPBIT)).thenReturn(upbitDbNotices);
        when(noticeService.getAllNoticesByMarketType(MarketType.COINONE)).thenReturn(coinoneDbNotices);

        // When
        scrapService.scrapNoticeData();

        // Then - DB 기반 로직 검증
        verify(upbitScrapComponent, times(1)).parseNoticeData();
        verify(coinoneScrapComponent, times(1)).parseNoticeData();
        
        // DB 전체 공지사항 조회 검증 (새로운 로직)
        verify(noticeService, times(1)).getAllNoticesByMarketType(MarketType.UPBIT);
        verify(noticeService, times(1)).getAllNoticesByMarketType(MarketType.COINONE);
        
        // 둘 다 URL이 일치하므로 새로운 공지사항 없음 - 변경사항 없음으로 처리
        verify(upbitScrapComponent, never()).setNewParsedData(anyList());
        verify(coinoneScrapComponent, never()).setNewParsedData(anyList());
        verify(upbitScrapComponent, never()).setNewNotice(anyList());
        verify(coinoneScrapComponent, never()).setNewNotice(anyList());
        
        verify(exchangeNoticePacadeService, never()).createNoticesBulk(any(MarketType.class), anyList());
        verify(noticeService, never()).getNoticeByLink(anyString());
        verify(marketInfoStompController, never()).sendNewNotice(any(NoticeDto.class));
    }
}
