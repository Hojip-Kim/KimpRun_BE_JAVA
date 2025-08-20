package unit.kimp.exchange.service.impl;

import kimp.common.dto.PageRequestDto;
import kimp.common.method.DtoConverter;
import kimp.exception.KimprunException;
import kimp.exchange.dao.ExchangeDao;
import kimp.exchange.entity.Exchange;
import kimp.exchange.service.impl.ExchangeNoticePacadeService;
import kimp.market.Enum.MarketType;
import kimp.notice.dao.NoticeDao;
import kimp.notice.dto.notice.ExchangeNoticeDto;
import kimp.notice.dto.notice.NoticeDto;
import kimp.notice.dto.notice.NoticeParsedData;
import kimp.notice.entity.Notice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("ExchangeNoticePacadeService 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class ExchangeNoticePacadeServiceTest {

    @Mock
    private NoticeDao noticeDao;

    @Mock
    private ExchangeDao exchangeDao;

    @Mock
    private DtoConverter dtoConverter;

    @InjectMocks
    private ExchangeNoticePacadeService exchangeNoticePacadeService;

    private Exchange exchange;
    private Notice notice;
    private NoticeDto noticeDto;
    private NoticeParsedData noticeParsedData;
    private List<NoticeParsedData> noticeParsedDataList;
    private final MarketType marketType = MarketType.UPBIT;
    private final String title = "Test Title";
    private final String link = "https://test.com/notice/1";
    private final LocalDateTime date = LocalDateTime.of(2023, 1, 1, 0, 0);

    @BeforeEach
    void setUp() throws Exception {
        // Set up Exchange
        exchange = new Exchange(marketType, "https://upbit.com");

        // Use reflection to set the id field
        Field idField = Exchange.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(exchange, 1L);

        // Set up Notice
        notice = new Notice(title, link, date);
        notice.setExchange(exchange);

        // Set up NoticeDto
        noticeDto = new NoticeDto(1L, marketType, title, link, date);

        // Set up NoticeParsedData
        noticeParsedData = new NoticeParsedData(title, link, date);
        noticeParsedDataList = Arrays.asList(noticeParsedData);
    }

    @Test
    @DisplayName("createNoticesBulk 메서드 테스트 - 새로운 공지사항이 있을 때")
    void shouldCreateNoticesBulkWhenNewNoticeExists() {
        // Given
        when(exchangeDao.getExchangeByMarketType(marketType)).thenReturn(exchange);
        when(noticeDao.getNoticeByLink(link)).thenReturn(null);
        when(noticeDao.createBulkNotice(anyList())).thenReturn(true);

        // When
        boolean result = exchangeNoticePacadeService.createNoticesBulk(marketType, noticeParsedDataList);

        // Then
        assertThat(result).isTrue();
        verify(exchangeDao, times(1)).getExchangeByMarketType(marketType);
        verify(noticeDao, times(1)).getNoticeByLink(link);
        verify(noticeDao, times(1)).createBulkNotice(anyList());
    }

    @Test
    @DisplayName("createNoticesBulk 메서드 테스트 - 이미 존재하는 공지사항만 있을 때")
    void shouldNotCreateNoticesBulkWhenOnlyExistingNoticeExists() {
        // Given
        when(exchangeDao.getExchangeByMarketType(marketType)).thenReturn(exchange);
        when(noticeDao.getNoticeByLink(link)).thenReturn(notice);

        // When
        boolean result = exchangeNoticePacadeService.createNoticesBulk(marketType, noticeParsedDataList);

        // Then
        assertThat(result).isTrue();
        verify(exchangeDao, times(1)).getExchangeByMarketType(marketType);
        verify(noticeDao, times(1)).getNoticeByLink(link);
        verify(noticeDao, never()).createBulkNotice(anyList());
    }

    @Test
    @DisplayName("createNoticesBulk 메서드 테스트 - 새로운 공지사항과 이미 존재하는 공지사항이 모두 있을 때")
    void shouldCreateNoticesBulkWhenMixedNoticesExist() {
        // Given
        NoticeParsedData newNoticeParsedData = new NoticeParsedData("New Title", "https://test.com/notice/2", date);
        List<NoticeParsedData> mixedNoticeParsedDataList = Arrays.asList(noticeParsedData, newNoticeParsedData);

        when(exchangeDao.getExchangeByMarketType(marketType)).thenReturn(exchange);
        when(noticeDao.getNoticeByLink(link)).thenReturn(notice);
        when(noticeDao.getNoticeByLink("https://test.com/notice/2")).thenReturn(null);
        when(noticeDao.createBulkNotice(anyList())).thenReturn(true);

        // When
        boolean result = exchangeNoticePacadeService.createNoticesBulk(marketType, mixedNoticeParsedDataList);

        // Then
        assertThat(result).isTrue();
        verify(exchangeDao, times(1)).getExchangeByMarketType(marketType);
        verify(noticeDao, times(1)).getNoticeByLink(link);
        verify(noticeDao, times(1)).getNoticeByLink("https://test.com/notice/2");
        verify(noticeDao, times(1)).createBulkNotice(anyList());
    }

    @Test
    @DisplayName("createNotice 메서드 테스트")
    void shouldCreateNotice() {
        // Given
        Notice noticeWithoutExchange = new Notice(title, link, date);

        Notice savedNotice = new Notice(title, link, date);
        savedNotice.setExchange(exchange);

        when(exchangeDao.getExchangeByMarketType(marketType)).thenReturn(exchange);
        when(noticeDao.createNotice(any(Notice.class))).thenReturn(noticeWithoutExchange);
        when(dtoConverter.convertNoticeToDto(any(Notice.class))).thenReturn(noticeDto);

        ExchangeNoticeDto<NoticeDto> exchangeNoticeDto = new ExchangeNoticeDto<>("https://upbit.com", marketType);
        exchangeNoticeDto.setData(noticeDto);
        when(dtoConverter.wrappingDtoToExchangeNoticeDto(noticeDto)).thenReturn(exchangeNoticeDto);

        // When
        ExchangeNoticeDto<NoticeDto> result = exchangeNoticePacadeService.createNotice(marketType, title, link, date);

        // Then
        assertThat(result).isEqualTo(exchangeNoticeDto);
        verify(exchangeDao, times(1)).getExchangeByMarketType(marketType);
        verify(noticeDao, times(1)).createNotice(any(Notice.class));
        verify(dtoConverter, times(1)).convertNoticeToDto(any(Notice.class));
        verify(dtoConverter, times(1)).wrappingDtoToExchangeNoticeDto(noticeDto);
    }

    @Test
    @DisplayName("getNoticeByExchange 메서드 테스트")
    void shouldGetNoticeByExchange() {
        // Given
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10);
        Pageable pageable = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize());
        List<Notice> notices = Arrays.asList(notice);
        Page<Notice> noticePage = new PageImpl<>(notices, pageable, notices.size());
        Page<NoticeDto> noticeDtoPage = new PageImpl<>(Arrays.asList(noticeDto), pageable, 1);

        ExchangeNoticeDto<Page<NoticeDto>> exchangeNoticeDto = new ExchangeNoticeDto<>("https://upbit.com", marketType);
        exchangeNoticeDto.setData(noticeDtoPage);

        when(exchangeDao.getExchangeByMarketType(marketType)).thenReturn(exchange);
        when(noticeDao.findByExchangeIdOrderByRegistedAtAsc(exchange.getId(), pageable)).thenReturn(noticePage);
        when(dtoConverter.convertNoticePageToDtoPage(noticePage)).thenReturn(noticeDtoPage);
        when(dtoConverter.wrappingDtosToExchangeNoticeDto(marketType, noticeDtoPage)).thenReturn(exchangeNoticeDto);

        // When
        ExchangeNoticeDto<Page<NoticeDto>> result = exchangeNoticePacadeService.getNoticeByExchange(marketType, pageRequestDto);

        // Then
        assertThat(result).isEqualTo(exchangeNoticeDto);
        verify(exchangeDao, times(1)).getExchangeByMarketType(marketType);
        verify(noticeDao, times(1)).findByExchangeIdOrderByRegistedAtAsc(exchange.getId(), pageable);
        verify(dtoConverter, times(1)).convertNoticePageToDtoPage(noticePage);
        verify(dtoConverter, times(1)).wrappingDtosToExchangeNoticeDto(marketType, noticeDtoPage);
    }

    @Test
    @DisplayName("getNoticeByExchange 메서드 테스트 - 공지사항이 없을 때 예외 발생")
    void shouldThrowExceptionWhenGetNoticeByExchangeAndNoNoticeExists() {
        // Given
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10);
        Pageable pageable = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize());
        Page<Notice> emptyNoticePage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(exchangeDao.getExchangeByMarketType(marketType)).thenReturn(exchange);
        when(noticeDao.findByExchangeIdOrderByRegistedAtAsc(exchange.getId(), pageable)).thenReturn(emptyNoticePage);

        // When & Then
        assertThatThrownBy(() -> exchangeNoticePacadeService.getNoticeByExchange(marketType, pageRequestDto))
                .isInstanceOf(KimprunException.class)
                .hasFieldOrPropertyWithValue("message", "Not have data");

        verify(exchangeDao, times(1)).getExchangeByMarketType(marketType);
        verify(noticeDao, times(1)).findByExchangeIdOrderByRegistedAtAsc(exchange.getId(), pageable);
        verify(dtoConverter, never()).convertNoticePageToDtoPage(any());
        verify(dtoConverter, never()).wrappingDtosToExchangeNoticeDto(any(), any());
    }
}
