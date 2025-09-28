package kimp.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JobHistoryResponse {
    private int totalCount;
    private List<JobExecutionInfo> executions;

    public JobHistoryResponse(int totalCount, List<JobExecutionInfo> executions) {
        this.totalCount = totalCount;
        this.executions = executions;
    }
}