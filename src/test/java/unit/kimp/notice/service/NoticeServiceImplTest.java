package unit.kimp.notice.service;

import kimp.common.dto.PageRequestDto;
import kimp.common.method.DtoConverter;
import kimp.common.method.MarketMethod;
import kimp.exchange.entity.Exchange;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.market.Enum.MarketType;
import kimp.notice.dao.NoticeDao;
import kimp.notice.dto.notice.ExchangeNoticeDto;
import kimp.notice.dto.notice.NoticeDto;
import kimp.notice.entity.Notice;
import kimp.notice.service.impl.NoticeServiceImpl;
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
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("NoticeService 구현체 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class NoticeServiceImplTest {

    @Mock
    private NoticeDao noticeDao;

    @Mock
    private MarketMethod marketMethod;

    @Mock
    private DtoConverter dtoConverter;

    @InjectMocks
    private NoticeServiceImpl noticeService;

    private Notice notice;
    private Exchange exchange;
    private NoticeDto noticeDto;
    private final String title = "Test Title";
    private final String link = "https://test.com/notice/1";
    private final LocalDateTime date = LocalDateTime.of(2023, 1, 1, 0, 0);

    @BeforeEach
    void setup() {
        exchange = new Exchange(MarketType.UPBIT, "https://upbit.com");
        notice = new Notice(title, link, date);
        notice.setExchange(exchange);

        // Create a NoticeDto manually for testing
        noticeDto = new NoticeDto(1L, MarketType.UPBIT, title, link, date);
    }

    @Test
    @DisplayName("ID로 공지사항 조회")
    void shouldGetNoticeById() {
        // Given
        when(noticeDao.getNotice(anyLong())).thenReturn(notice);
        when(dtoConverter.convertNoticeToDto(any(Notice.class))).thenReturn(noticeDto);

        // When
        NoticeDto result = noticeService.getNoticeById(1L);

        // Then
        assertThat(result).isEqualTo(noticeDto);
        verify(noticeDao, times(1)).getNotice(1L);
        verify(dtoConverter, times(1)).convertNoticeToDto(notice);
    }

    @Test
    @DisplayName("ID로 공지사항 조회: 공지사항 없을 때 예외 발생")
    void shouldThrowExceptionWhenGetNoticeByIdNotFound() {
        // Given
        when(noticeDao.getNotice(anyLong())).thenThrow(new IllegalArgumentException("not found notice id : 1"));

        // When & Then
        assertThatThrownBy(() -> noticeService.getNoticeById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found notice id : 1");
        verify(noticeDao, times(1)).getNotice(1L);
        verify(dtoConverter, never()).convertNoticeToDto(any(Notice.class));
    }

    @Test
    @DisplayName("링크로 공지사항 조회")
    void shouldGetNoticeByLink() {
        // Given
        when(noticeDao.getNoticeByLink(anyString())).thenReturn(notice);
        when(dtoConverter.convertNoticeToDto(any(Notice.class))).thenReturn(noticeDto);

        // When
        NoticeDto result = noticeService.getNoticeByLink(link);

        // Then
        assertThat(result).isEqualTo(noticeDto);
        verify(noticeDao, times(1)).getNoticeByLink(link);
        verify(dtoConverter, times(1)).convertNoticeToDto(notice);
    }

    @Test
    @DisplayName("링크로 공지사항 조회: 공지사항 없을 때 예외 발생")
    void shouldThrowExceptionWhenGetNoticeByLinkNotFound() {
        // Given
        when(noticeDao.getNoticeByLink(anyString())).thenReturn(null);
        when(dtoConverter.convertNoticeToDto(null)).thenThrow(new IllegalArgumentException("not have notice"));

        // When & Then
        assertThatThrownBy(() -> noticeService.getNoticeByLink(link))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not have notice");
        verify(noticeDao, times(1)).getNoticeByLink(link);
        verify(dtoConverter, times(1)).convertNoticeToDto(null);
    }

    @Test
    @DisplayName("모든 공지사항 조회")
    void shouldGetAllNotices() {
        // Given
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10);
        Pageable pageable = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize());
        List<Notice> notices = Arrays.asList(notice);
        Page<Notice> noticePage = new PageImpl<>(notices, pageable, notices.size());
        Page<NoticeDto> noticeDtoPage = new PageImpl<>(Arrays.asList(noticeDto), pageable, 1);
        ExchangeNoticeDto<Page<NoticeDto>> exchangeNoticeDto = new ExchangeNoticeDto<>("https://upbit.com", MarketType.ALL);
        exchangeNoticeDto.setData(noticeDtoPage);

        when(noticeDao.findAllByOrderByRegistedAtAsc(any(Pageable.class))).thenReturn(noticePage);
        when(dtoConverter.convertNoticePageToDtoPage(any())).thenReturn(noticeDtoPage);
        when(dtoConverter.wrappingDtosToExchangeNoticeDto(any(), any())).thenReturn(exchangeNoticeDto);

        // When
        ExchangeNoticeDto<Page<NoticeDto>> result = noticeService.getAllNotices(new kimp.notice.vo.GetNoticeByExchangeVo(MarketType.ALL, pageRequestDto));

        // Then
        assertThat(result).isEqualTo(exchangeNoticeDto);
        verify(noticeDao, times(1)).findAllByOrderByRegistedAtAsc(any());
        verify(dtoConverter, times(1)).convertNoticePageToDtoPage(noticePage);
        verify(dtoConverter, times(1)).wrappingDtosToExchangeNoticeDto(eq(MarketType.ALL), eq(noticeDtoPage));
    }

    @Test
    @DisplayName("모든 공지사항 조회: 공지사항 없을 때 예외 발생")
    void shouldThrowExceptionWhenGetAllNoticesEmpty() {
        // Given
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10);
        Pageable pageable = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize());
        Page<Notice> emptyNoticePage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(noticeDao.findAllByOrderByRegistedAtAsc(any(Pageable.class))).thenReturn(emptyNoticePage);

        // When & Then
        assertThatThrownBy(() -> noticeService.getAllNotices(new kimp.notice.vo.GetNoticeByExchangeVo(MarketType.ALL, pageRequestDto)))
                .isInstanceOf(KimprunException.class)
                .hasFieldOrPropertyWithValue("exceptionEnum", KimprunExceptionEnum.REQUEST_ACCEPTED)
                .hasFieldOrPropertyWithValue("message", "Not have data");

        verify(noticeDao, times(1)).findAllByOrderByRegistedAtAsc(any());
        verify(dtoConverter, never()).convertNoticePageToDtoPage(any());
        verify(dtoConverter, never()).wrappingDtosToExchangeNoticeDto(any(), any());
    }

    @Test
    @DisplayName("공지사항 생성")
    void shouldCreateNotice() {
        // Given
        when(noticeDao.createNotice(any(Notice.class))).thenReturn(notice);
        when(dtoConverter.convertNoticeToDto(any(Notice.class))).thenReturn(noticeDto);

        // When
        NoticeDto result = noticeService.createNotice(title, link, date);

        // Then
        assertThat(result).isEqualTo(noticeDto);
        verify(noticeDao, times(1)).createNotice(any(Notice.class));
        verify(dtoConverter, times(1)).convertNoticeToDto(notice);
    }

    @Test
    @DisplayName("링크로 공지사항 찾기")
    void shouldFindNoticeByLink() {
        // Given
        when(noticeDao.getNoticeByLink(anyString())).thenReturn(notice);
        when(dtoConverter.convertNoticeToDto(any(Notice.class))).thenReturn(noticeDto);

        // When
        NoticeDto result = noticeService.findNoticeByLink(link);

        // Then
        assertThat(result).isEqualTo(noticeDto);
        verify(noticeDao, times(1)).getNoticeByLink(link);
        verify(dtoConverter, times(1)).convertNoticeToDto(notice);
    }

    @Test
    @DisplayName("공지사항 업데이트")
    void shouldUpdateNotice() {
        // Given
        String newTitle = "New Test Title";
        String newLink = "https://test.com/notice/2";
        Notice spyNotice = spy(notice);
        when(noticeDao.getNotice(anyLong())).thenReturn(spyNotice);
        when(dtoConverter.convertNoticeToDto(any(Notice.class))).thenReturn(noticeDto);

        // When
        NoticeDto result = noticeService.updateNotice(1L, newTitle, newLink);

        // Then
        assertThat(result).isEqualTo(noticeDto);
        verify(noticeDao, times(1)).getNotice(1L);
        verify(spyNotice, times(1)).updateTitle(newTitle);
        verify(spyNotice, times(1)).updateLink(newLink);
        verify(dtoConverter, times(1)).convertNoticeToDto(spyNotice);
    }

    @Test
    @DisplayName("공지사항 업데이트: 공지사항 없을 때 예외 발생")
    void shouldThrowExceptionWhenUpdateNoticeNotFound() {
        // Given
        String newTitle = "New Test Title";
        String newLink = "https://test.com/notice/2";
        when(noticeDao.getNotice(anyLong())).thenThrow(new IllegalArgumentException("not found notice id : 1"));

        // When & Then
        assertThatThrownBy(() -> noticeService.updateNotice(1L, newTitle, newLink))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found notice id : 1");
        verify(noticeDao, times(1)).getNotice(1L);
        verify(dtoConverter, never()).convertNoticeToDto(any(Notice.class));
    }

    @Test
    @DisplayName("공지사항 업데이트: 빈 제목 예외 발생")
    void shouldThrowExceptionWhenUpdateNoticeEmptyTitle() {
        // Given
        String emptyTitle = "";
        String newLink = "https://test.com/notice/2";
        Notice spyNotice = spy(notice);
        when(noticeDao.getNotice(anyLong())).thenReturn(spyNotice);
        doThrow(new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Notice title cannot be empty", HttpStatus.BAD_REQUEST, "Notice.updateTitle")).when(spyNotice).updateTitle(emptyTitle);

        // When & Then
        assertThatThrownBy(() -> noticeService.updateNotice(1L, emptyTitle, newLink))
                .isInstanceOf(KimprunException.class)
                .hasMessageContaining("Notice title cannot be empty");
        verify(noticeDao, times(1)).getNotice(1L);
        verify(spyNotice, times(1)).updateTitle(emptyTitle);
        verify(spyNotice, never()).updateLink(anyString());
        verify(dtoConverter, never()).convertNoticeToDto(any(Notice.class));
    }

    @Test
    @DisplayName("공지사항 날짜 업데이트")
    void shouldUpdateNoticeDate() {
        // Given
        LocalDateTime newDate = LocalDateTime.of(2023, 2, 1, 0, 0);
        Notice spyNotice = spy(notice);
        when(noticeDao.getNotice(anyLong())).thenReturn(spyNotice);
        when(dtoConverter.convertNoticeToDto(any(Notice.class))).thenReturn(noticeDto);

        // When
        NoticeDto result = noticeService.updateNoticeDate(1L, newDate);

        // Then
        assertThat(result).isEqualTo(noticeDto);
        verify(noticeDao, times(1)).getNotice(1L);
        verify(spyNotice, times(1)).updateDate(newDate);
        verify(dtoConverter, times(1)).convertNoticeToDto(spyNotice);
    }

    @Test
    @DisplayName("공지사항 날짜 업데이트: 공지사항 없을 때 예외 발생")
    void shouldThrowExceptionWhenUpdateNoticeDateNotFound() {
        // Given
        LocalDateTime newDate = LocalDateTime.of(2023, 2, 1, 0, 0);
        when(noticeDao.getNotice(anyLong())).thenThrow(new IllegalArgumentException("not found notice id : 1"));

        // When & Then
        assertThatThrownBy(() -> noticeService.updateNoticeDate(1L, newDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found notice id : 1");
        verify(noticeDao, times(1)).getNotice(1L);
        verify(dtoConverter, never()).convertNoticeToDto(any(Notice.class));
    }

    @Test
    @DisplayName("공지사항 날짜 업데이트: null 날짜 예외 발생")
    void shouldThrowExceptionWhenUpdateNoticeDateNullDate() {
        // Given
        Notice spyNotice = spy(notice);
        when(noticeDao.getNotice(anyLong())).thenReturn(spyNotice);
        doThrow(new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Notice date cannot be null", HttpStatus.BAD_REQUEST, "Notice.updateDate")).when(spyNotice).updateDate(null);

        // When & Then
        assertThatThrownBy(() -> noticeService.updateNoticeDate(1L, null))
                .isInstanceOf(KimprunException.class)
                .hasMessageContaining("Notice date cannot be null");
        verify(noticeDao, times(1)).getNotice(1L);
        verify(spyNotice, times(1)).updateDate(null);
        verify(dtoConverter, never()).convertNoticeToDto(any(Notice.class));
    }
}
