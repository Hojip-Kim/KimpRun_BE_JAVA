package kimp.batch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class JobExecutionStatusResponse {
    private Map<String, StepExecutionInfo> stepExecutions;

    public JobExecutionStatusResponse(Map<String, StepExecutionInfo> stepExecutions) {
        this.stepExecutions = stepExecutions;
    }
}