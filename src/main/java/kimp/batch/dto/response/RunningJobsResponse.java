package kimp.batch.dto.response;

import kimp.batch.dto.internal.JobExecutionInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunningJobsResponse {
    private int runningJobsCount;
    private List<JobExecutionInfo> runningJobs;
}