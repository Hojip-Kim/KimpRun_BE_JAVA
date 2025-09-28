package kimp.batch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RateLimitResetResponse {
    private String message;
    private LocalDateTime resetTime;
    private long currentUsage;
    private int limit;
    private int windowSeconds;

    public RateLimitResetResponse(String message, LocalDateTime resetTime, long currentUsage, int limit, int windowSeconds) {
        this.message = message;
        this.resetTime = resetTime;
        this.currentUsage = currentUsage;
        this.limit = limit;
        this.windowSeconds = windowSeconds;
    }
}