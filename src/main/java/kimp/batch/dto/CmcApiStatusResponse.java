package kimp.batch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CmcApiStatusResponse {
    private String status;
    private LocalDateTime timestamp;

    public CmcApiStatusResponse(String status, LocalDateTime timestamp) {
        this.status = status;
        this.timestamp = timestamp;
    }
}