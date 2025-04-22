package kimp.exchange.dto.notice;

import kimp.market.Enum.MarketType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class NoticeDto {

    private long id;
    private MarketType exchangeType;
    private String title;
    private String url;
    private LocalDateTime createdAt;

    public NoticeDto(long id, MarketType exchangeType, String title, String url, LocalDateTime createdAt) {
        this.id = id;
        this.exchangeType = exchangeType;
        this.title = title;
        this.url = url;
        this.createdAt = createdAt;
    }
}
