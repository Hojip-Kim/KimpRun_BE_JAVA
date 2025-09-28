package kimp.batch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UnlockResponse {
    private String message;
    private LocalDateTime unlockTime;
    private String lockKey;
    private String executorIp;

    public UnlockResponse(String message, LocalDateTime unlockTime, String lockKey, String executorIp) {
        this.message = message;
        this.unlockTime = unlockTime;
        this.lockKey = lockKey;
        this.executorIp = executorIp;
    }
}