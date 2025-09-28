package kimp.batch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CmcBatchSyncResponse {
    private String message;
    private String mode;
    private LocalDateTime timestamp;
    private boolean distributedLockApplied;
    private boolean asyncExecution;

    public CmcBatchSyncResponse(String message, String mode, LocalDateTime timestamp, boolean distributedLockApplied, boolean asyncExecution) {
        this.message = message;
        this.mode = mode;
        this.timestamp = timestamp;
        this.distributedLockApplied = distributedLockApplied;
        this.asyncExecution = asyncExecution;
    }
}