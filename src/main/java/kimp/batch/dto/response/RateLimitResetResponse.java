package kimp.batch.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitResetResponse {
    private String message;
    private LocalDateTime resetTime;
    private long currentUsage;
    private int limit;
    private int windowSeconds;
}