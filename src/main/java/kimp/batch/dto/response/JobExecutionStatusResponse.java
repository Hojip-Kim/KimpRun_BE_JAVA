package kimp.batch.dto.response;

import kimp.batch.dto.internal.StepExecutionInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExecutionStatusResponse {
    private Map<String, StepExecutionInfo> stepExecutions;
}