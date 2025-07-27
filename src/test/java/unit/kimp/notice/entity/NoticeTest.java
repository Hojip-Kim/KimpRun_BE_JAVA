package unit.kimp.notice.entity;

import kimp.exception.KimprunException;
import kimp.exchange.entity.Exchange;
import kimp.market.Enum.MarketType;
import kimp.notice.entity.Notice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Notice Entity 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class NoticeTest {

    private Notice notice;
    private Exchange exchange;
    private final String title = "Test Title";
    private final String link = "https://test.com/notice/1";
    private final LocalDateTime date = LocalDateTime.of(2023, 1, 1, 0, 0);

    @BeforeEach
    void setup() {
        notice = new Notice(title, link, date);
        exchange = new Exchange(MarketType.UPBIT, "https://upbit.com");
    }

    @Test
    @DisplayName("Notice 생성자 테스트")
    void shouldTestConstructor() {
        assertThat(notice.getTitle()).isEqualTo(title);
        assertThat(notice.getLink()).isEqualTo(link);
        assertThat(notice.getDate()).isEqualTo(date);
        assertThat(notice.getExchange()).isNull();
    }

    @Test
    @DisplayName("setExchange 메서드 테스트")
    void shouldSetExchange() {
        // When
        notice.setExchange(exchange);

        // Then
        assertThat(notice.getExchange()).isEqualTo(exchange);
    }

    @Test
    @DisplayName("setExchange 메서드 중복 설정 예외 테스트")
    void shouldThrowExceptionWhenSetExchangeAlreadySet() {
        // Given
        notice.setExchange(exchange);

        // When & Then
        assertThatThrownBy(() -> notice.setExchange(new Exchange(MarketType.BINANCE, "https://binance.com")))
                .isInstanceOf(KimprunException.class);
    }

    @Test
    @DisplayName("updateTitle 메서드 테스트")
    void shouldUpdateTitle() {
        // Given
        String newTitle = "New Test Title";

        // When
        notice.updateTitle(newTitle);

        // Then
        assertThat(notice.getTitle()).isEqualTo(newTitle);
    }

    @Test
    @DisplayName("updateTitle 메서드 동일 제목 테스트")
    void shouldUpdateTitleSameTitle() {
        // When
        Notice result = notice.updateTitle(title);

        // Then
        assertThat(result).isSameAs(notice);
        assertThat(notice.getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("updateTitle 메서드 빈 제목 예외 테스트")
    void shouldThrowExceptionWhenUpdateTitleEmptyTitle() {
        // When & Then
        assertThatThrownBy(() -> notice.updateTitle(""))
                .isInstanceOf(KimprunException.class);
    }

    @Test
    @DisplayName("updateLink 메서드 테스트")
    void shouldUpdateLink() {
        // Given
        String newLink = "https://test.com/notice/2";

        // When
        notice.updateLink(newLink);

        // Then
        assertThat(notice.getLink()).isEqualTo(newLink);
    }

    @Test
    @DisplayName("updateLink 메서드 동일 링크 테스트")
    void shouldUpdateLinkSameLink() {
        // When
        Notice result = notice.updateLink(link);

        // Then
        assertThat(result).isSameAs(notice);
        assertThat(notice.getLink()).isEqualTo(link);
    }

    @Test
    @DisplayName("updateLink 메서드 빈 링크 예외 테스트")
    void shouldThrowExceptionWhenUpdateLinkEmptyLink() {
        // When & Then
        assertThatThrownBy(() -> notice.updateLink(""))
                .isInstanceOf(KimprunException.class);
    }

    @Test
    @DisplayName("updateDate 메서드 테스트")
    void shouldUpdateDate() {
        // Given
        LocalDateTime newDate = LocalDateTime.of(2023, 2, 1, 0, 0);

        // When
        notice.updateDate(newDate);

        // Then
        assertThat(notice.getDate()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("updateDate 메서드 null 날짜 예외 테스트")
    void shouldThrowExceptionWhenUpdateDateNullDate() {
        // When & Then
        assertThatThrownBy(() -> notice.updateDate(null))
                .isInstanceOf(KimprunException.class);
    }

    @Test
    @DisplayName("Notice 기본 생성자 테스트")
    void shouldTestDefaultConstructor() {
        // When
        Notice emptyNotice = new Notice();

        // Then
        assertThat(emptyNotice.getId()).isNull();
        assertThat(emptyNotice.getTitle()).isNull();
        assertThat(emptyNotice.getLink()).isNull();
        assertThat(emptyNotice.getDate()).isNull();
        assertThat(emptyNotice.getExchange()).isNull();
    }
}
