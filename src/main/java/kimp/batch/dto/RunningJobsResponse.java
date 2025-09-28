package kimp.batch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RunningJobsResponse {
    private int runningJobsCount;
    private List<JobExecutionInfo> runningJobs;

    public RunningJobsResponse(int runningJobsCount, List<JobExecutionInfo> runningJobs) {
        this.runningJobsCount = runningJobsCount;
        this.runningJobs = runningJobs;
    }
}