package kimp.batch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BatchHealthResponse {
    private String message;
    private boolean jobRepositoryConnected;
    private boolean targetJobExists;
    private List<String> availableJobs;
    private LocalDateTime timestamp;

    public BatchHealthResponse(String message, boolean jobRepositoryConnected, boolean targetJobExists, List<String> availableJobs, LocalDateTime timestamp) {
        this.message = message;
        this.jobRepositoryConnected = jobRepositoryConnected;
        this.targetJobExists = targetJobExists;
        this.availableJobs = availableJobs;
        this.timestamp = timestamp;
    }
}