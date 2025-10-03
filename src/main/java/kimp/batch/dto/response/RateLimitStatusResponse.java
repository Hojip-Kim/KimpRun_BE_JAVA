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
public class RateLimitStatusResponse {
    private long currentUsage;
    private int limit;
    private int windowSeconds;
    private LocalDateTime timestamp;
}