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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import jakarta.persistence.EntityManager;

@DisplayName("NoticeDao 구현체 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class NoticeDaoImplTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private EntityManager entityManager;

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
        
        // EntityManager Mock을 수동으로 주입
        ReflectionTestUtils.setField(noticeDao, "entityManager", entityManager);
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

    @Test
    @DisplayName("findExistingNoticeLinks 메서드 테스트 - 기존 링크가 있을 때")
    void shouldFindExistingNoticeLinks() {
        // Given
        List<String> inputLinks = Arrays.asList("https://test.com/1", "https://test.com/2", "https://test.com/3");
        List<String> existingLinks = Arrays.asList("https://test.com/1", "https://test.com/3");
        when(noticeRepository.findExistingNoticeLinks(anyList())).thenReturn(existingLinks);

        // When
        List<String> result = noticeDao.findExistingNoticeLinks(inputLinks);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly("https://test.com/1", "https://test.com/3");
        verify(noticeRepository, times(1)).findExistingNoticeLinks(inputLinks);
    }

    @Test
    @DisplayName("findExistingNoticeLinks 메서드 테스트 - 빈 리스트일 때")
    void shouldReturnEmptyWhenFindExistingNoticeLinksWithEmptyList() {
        // Given
        List<String> emptyList = Collections.emptyList();

        // When
        List<String> result = noticeDao.findExistingNoticeLinks(emptyList);

        // Then
        assertThat(result).isEmpty();
        verify(noticeRepository, never()).findExistingNoticeLinks(any());
    }

    @Test
    @DisplayName("findExistingNoticeLinks 메서드 테스트 - null일 때")
    void shouldReturnEmptyWhenFindExistingNoticeLinksWithNull() {
        // Given & When
        List<String> result = noticeDao.findExistingNoticeLinks(null);

        // Then
        assertThat(result).isEmpty();
        verify(noticeRepository, never()).findExistingNoticeLinks(any());
    }

    @Test
    @DisplayName("createBulkNoticeOptimized 메서드 테스트 - 배치 처리")
    void shouldCreateBulkNoticeOptimized() {
        // Given
        Notice notice1 = new Notice("Title 1", "https://test.com/1", date);
        Notice notice2 = new Notice("Title 2", "https://test.com/2", date);
        Notice notice3 = new Notice("Title 3", "https://test.com/3", date);
        List<Notice> notices = Arrays.asList(notice1, notice2, notice3);

        // When
        boolean result = noticeDao.createBulkNoticeOptimized(notices);

        // Then
        assertThat(result).isTrue();
        // 배치 크기(100)보다 작으므로 persist가 3번 호출되어야 함
        verify(entityManager, times(3)).persist(any(Notice.class));
        // flush와 clear가 마지막에 호출되어야 함
        verify(entityManager, times(1)).flush();
        verify(entityManager, times(1)).clear();
    }

    @Test
    @DisplayName("createBulkNoticeOptimized 메서드 테스트 - 대용량 배치 처리")
    void shouldCreateBulkNoticeOptimizedWithLargeBatch() {
        // Given
        List<Notice> largeNoticeList = new ArrayList<>();
        // 배치 크기(100)를 넘는 250개의 Notice 생성
        for (int i = 0; i < 250; i++) {
            largeNoticeList.add(new Notice("Title " + i, "https://test.com/" + i, date));
        }

        // When
        boolean result = noticeDao.createBulkNoticeOptimized(largeNoticeList);

        // Then
        assertThat(result).isTrue();
        // 250개 Notice에 대해 persist가 호출되어야 함
        verify(entityManager, times(250)).persist(any(Notice.class));
        // 배치 크기마다 flush/clear + 마지막 flush/clear = 총 3번 flush/clear
        verify(entityManager, times(3)).flush();
        verify(entityManager, times(3)).clear();
    }

    @Test
    @DisplayName("createBulkNoticeOptimized 메서드 테스트 - 빈 리스트일 때")
    void shouldCreateBulkNoticeOptimizedWithEmptyList() {
        // Given
        List<Notice> emptyList = Collections.emptyList();

        // When
        boolean result = noticeDao.createBulkNoticeOptimized(emptyList);

        // Then
        assertThat(result).isTrue();
        // 빈 리스트이므로 어떤 EntityManager 메서드도 호출되지 않아야 함
        verify(entityManager, never()).persist(any());
        verify(entityManager, never()).flush();
        verify(entityManager, never()).clear();
    }

    @Test
    @DisplayName("createBulkNoticeOptimized 메서드 테스트 - null일 때")
    void shouldCreateBulkNoticeOptimizedWithNull() {
        // Given & When
        boolean result = noticeDao.createBulkNoticeOptimized(null);

        // Then
        assertThat(result).isTrue();
        // null이므로 어떤 EntityManager 메서드도 호출되지 않아야 함
        verify(entityManager, never()).persist(any());
        verify(entityManager, never()).flush();
        verify(entityManager, never()).clear();
    }
}