package unit.kimp.batch.scheduler;

import kimp.batch.scheduler.CmcBatchScheduler;
import kimp.cmc.dao.CmcBatchDao;
import kimp.common.lock.DistributedLockService;
import kimp.common.ratelimit.DistributedRateLimiter;
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
    
    @Mock
    private CmcBatchDao cmcBatchDao;
    
    @Mock
    private DistributedLockService distributedLockService;
    
    @Mock
    private DistributedRateLimiter distributedRateLimiter;

    @BeforeEach
    void setUp() {
        // 각 테스트에서 필요한 mock만 설정
    }

    @Test
    @DisplayName("CMC 데이터 동기화 Job 실행")
    void shouldRunCmcDataSyncJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        // Arrange
        when(cmcBatchDao.shouldRunCoinMapSync()).thenReturn(true);
        when(cmcBatchDao.shouldRunCoinInfoSync()).thenReturn(false);
        when(cmcBatchDao.shouldRunExchangeSync()).thenReturn(false);
        when(cmcBatchDao.shouldRunCoinRankSync()).thenReturn(false);
        when(cmcBatchDao.shouldRunCoinMetaSync()).thenReturn(false);
        when(distributedLockService.tryLock(anyString(), anyInt())).thenReturn("test-lock-token");
        when(distributedLockService.releaseLock(anyString(), anyString())).thenReturn(true);
        when(distributedRateLimiter.getCurrentUsage(anyString(), anyInt())).thenReturn(5L);
        
        // Act
        cmcBatchScheduler.runCmcDataSyncJob();

        // Assert
        verify(jobLauncher, times(1)).run(eq(cmcDataSyncJob), any(JobParameters.class));
    }

    @Test
    @DisplayName("CMC 데이터 동기화 Job 실행 중 예외 발생")
    void shouldLogExceptionWhenJobFails() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        // Arrange
        when(cmcBatchDao.shouldRunCoinMapSync()).thenReturn(true);
        when(cmcBatchDao.shouldRunCoinInfoSync()).thenReturn(false);
        when(cmcBatchDao.shouldRunExchangeSync()).thenReturn(false);
        when(cmcBatchDao.shouldRunCoinRankSync()).thenReturn(false);
        when(cmcBatchDao.shouldRunCoinMetaSync()).thenReturn(false);
        when(distributedLockService.tryLock(anyString(), anyInt())).thenReturn("test-lock-token");
        when(distributedLockService.releaseLock(anyString(), anyString())).thenReturn(true);
        when(distributedRateLimiter.getCurrentUsage(anyString(), anyInt())).thenReturn(5L);
        
        doThrow(new JobExecutionAlreadyRunningException("Job already running"))
                .when(jobLauncher).run(eq(cmcDataSyncJob), any(JobParameters.class));

        // Act
        cmcBatchScheduler.runCmcDataSyncJob();

        // Assert
        verify(jobLauncher, times(1)).run(eq(cmcDataSyncJob), any(JobParameters.class));
    }
    
    @Test
    @DisplayName("동기화가 필요하지 않을 때 Job 실행 안 함")
    void shouldSkipJobWhenNoSyncNeeded() throws Exception {
        // Arrange - 모든 동기화가 필요하지 않도록 설정
        when(cmcBatchDao.shouldRunCoinMapSync()).thenReturn(false);
        when(cmcBatchDao.shouldRunCoinInfoSync()).thenReturn(false);
        when(cmcBatchDao.shouldRunExchangeSync()).thenReturn(false);
        when(cmcBatchDao.shouldRunCoinRankSync()).thenReturn(false);
        when(cmcBatchDao.shouldRunCoinMetaSync()).thenReturn(false);
        
        // 이 테스트에서는 락이 성공적으로 획득되지만 동기화가 필요하지 않음
        when(distributedLockService.tryLock(anyString(), anyInt())).thenReturn("test-lock-token");
        when(distributedLockService.releaseLock(anyString(), anyString())).thenReturn(true);
        // 동기화가 필요하지 않으므로 rateLimiter는 호출되지 않음

        // Act
        cmcBatchScheduler.runCmcDataSyncJob();

        // Assert - Job이 실행되지 않아야 함
        verify(jobLauncher, never()).run(any(Job.class), any(JobParameters.class));
        // 락은 획득하고 해제해야 함
        verify(distributedLockService, times(1)).tryLock(anyString(), anyInt());
        verify(distributedLockService, times(1)).releaseLock(anyString(), anyString());
    }
}
