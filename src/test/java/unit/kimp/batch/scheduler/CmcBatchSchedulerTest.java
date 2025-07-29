package unit.kimp.batch.scheduler;

import kimp.batch.scheduler.CmcBatchScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CmcBatchSchedulerTest {

    @InjectMocks
    private CmcBatchScheduler cmcBatchScheduler;

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job cmcDataSyncJob;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("CMC 데이터 동기화 Job 실행")
    void shouldRunCmcDataSyncJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        // Act
        cmcBatchScheduler.runCmcDataSyncJob();

        // Assert
        verify(jobLauncher, times(1)).run(eq(cmcDataSyncJob), any(JobParameters.class));
    }

    @Test
    @DisplayName("CMC 데이터 동기화 Job 실행 중 예외 발생")
    void shouldLogExceptionWhenJobFails() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        // Arrange
        doThrow(new JobExecutionAlreadyRunningException("Job already running"))
                .when(jobLauncher).run(eq(cmcDataSyncJob), any(JobParameters.class));

        // Act
        cmcBatchScheduler.runCmcDataSyncJob();

        // Assert
        verify(jobLauncher, times(1)).run(eq(cmcDataSyncJob), any(JobParameters.class));
    }
}
