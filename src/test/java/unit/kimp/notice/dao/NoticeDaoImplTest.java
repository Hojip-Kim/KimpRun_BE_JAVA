package unit.kimp.notice.dao;

import kimp.exchange.entity.Exchange;
import kimp.market.Enum.MarketType;
import kimp.notice.dao.impl.NoticeDaoImpl;
import kimp.notice.entity.Notice;
import kimp.notice.repository.NoticeRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("NoticeDao 구현체 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class NoticeDaoImplTest {

    @Mock
    private NoticeRepository noticeRepository;

    @InjectMocks
    private NoticeDaoImpl noticeDao;

    private Notice notice;
    private Exchange exchange;
    private final String title = "Test Title";
    private final String link = "https://test.com/notice/1";
    private final LocalDateTime date = LocalDateTime.of(2023, 1, 1, 0, 0);

    @BeforeEach
    void setup() {
        exchange = new Exchange(MarketType.UPBIT, "https://upbit.com");
        notice = new Notice(title, link, date);
        notice.setExchange(exchange);
    }

    @Test
    @DisplayName("createNotice 메서드 테스트")
    void shouldCreateNotice() {
        // Given
        when(noticeRepository.save(any(Notice.class))).thenReturn(notice);

        // When
        Notice savedNotice = noticeDao.createNotice(notice);

        // Then
        assertThat(savedNotice).isEqualTo(notice);
        verify(noticeRepository, times(1)).save(notice);
    }

    @Test
    @DisplayName("createBulkNotice 메서드 테스트")
    void shouldCreateBulkNotice() {
        // Given
        List<Notice> notices = Arrays.asList(
                notice,
                new Notice("Title 2", "https://test.com/notice/2", LocalDateTime.now())
        );
        when(noticeRepository.saveAll(anyList())).thenReturn(notices);

        // When
        boolean result = noticeDao.createBulkNotice(notices);

        // Then
        assertThat(result).isTrue();
        verify(noticeRepository, times(1)).saveAll(notices);
    }

    @Test
    @DisplayName("getNotice 메서드 테스트")
    void shouldGetNotice() {
        // Given
        when(noticeRepository.findById(anyLong())).thenReturn(Optional.of(notice));

        // When
        Notice foundNotice = noticeDao.getNotice(1L);

        // Then
        assertThat(foundNotice).isEqualTo(notice);
        verify(noticeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getNotice 메서드 존재하지 않는 ID 예외 테스트")
    void shouldThrowExceptionWhenGetNoticeNotFound() {
        // Given
        when(noticeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> noticeDao.getNotice(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found notice id : 1");
        verify(noticeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getNoticeByLink 메서드 테스트")
    void shouldGetNoticeByLink() {
        // Given
        when(noticeRepository.findNoticeByLink(anyString())).thenReturn(notice);

        // When
        Notice foundNotice = noticeDao.getNoticeByLink(link);

        // Then
        assertThat(foundNotice).isEqualTo(notice);
        verify(noticeRepository, times(1)).findNoticeByLink(link);
    }

    @Test
    @DisplayName("deleteNotice 메서드 테스트")
    void shouldDeleteNotice() {
        // Given
        when(noticeRepository.findById(anyLong())).thenReturn(Optional.of(notice));
        doNothing().when(noticeRepository).deleteById(anyLong());

        // When
        noticeDao.deleteNotice(1L);

        // Then
        verify(noticeRepository, times(1)).findById(1L);
        verify(noticeRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("findByExchangeIdOrderByRegistedAtAsc 메서드 테스트")
    void shouldFindByExchangeIdOrderByRegistedAtAsc() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Notice> notices = Arrays.asList(notice);
        Page<Notice> noticePage = new PageImpl<>(notices, pageable, notices.size());
        when(noticeRepository.findByExchangeIdOrderByDateDesc(anyLong(), any(Pageable.class)))
                .thenReturn(noticePage);

        // When
        Page<Notice> result = noticeDao.findByExchangeIdOrderByRegistedAtAsc(1L, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(notice);
        verify(noticeRepository, times(1)).findByExchangeIdOrderByDateDesc(1L, pageable);
    }

    @Test
    @DisplayName("findAllByOrderByRegistedAtAsc 메서드 테스트")
    void shouldFindAllByOrderByRegistedAtAsc() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Notice> notices = Arrays.asList(notice);
        Page<Notice> noticePage = new PageImpl<>(notices, pageable, notices.size());
        when(noticeRepository.findAllByOrderByDateDescWithFetch(any(Pageable.class)))
                .thenReturn(noticePage);

        // When
        Page<Notice> result = noticeDao.findAllByOrderByRegistedAtAsc(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(notice);
        verify(noticeRepository, times(1)).findAllByOrderByDateDescWithFetch(pageable);
    }
}