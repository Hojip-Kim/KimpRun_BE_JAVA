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
public class UnlockResponse {
    private String message;
    private LocalDateTime unlockTime;
    private String lockKey;
    private String executorIp;
}