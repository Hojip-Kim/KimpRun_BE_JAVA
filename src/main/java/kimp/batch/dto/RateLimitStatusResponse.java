package kimp.batch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RateLimitStatusResponse {
    private long currentUsage;
    private int limit;
    private int windowSeconds;
    private LocalDateTime timestamp;

    public RateLimitStatusResponse(long currentUsage, int limit, int windowSeconds, LocalDateTime timestamp) {
        this.currentUsage = currentUsage;
        this.limit = limit;
        this.windowSeconds = windowSeconds;
        this.timestamp = timestamp;
    }
}