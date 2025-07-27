package unit.kimp.batch.job;

import kimp.batch.job.CmcCoinBatchJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class CmcCoinBatchJobTest {

    private CmcCoinBatchJob cmcCoinBatchJob;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private Step coinMapSyncStep;
    @Mock
    private Step coinLatestInfoSyncStep;
    @Mock
    private Step coinDetailInfoSyncStep;
    @Mock
    private Step exchangeMapSyncStep;
    @Mock
    private Step exchangeDetailInfoSyncStep;
    @Mock
    private Step coinInfoBulkStep;
    @Mock
    private Step coinMetaStep;
    @Mock
    private Step coinMappingStep;

    @BeforeEach
    void setUp() {
        cmcCoinBatchJob = new CmcCoinBatchJob(jobRepository, coinMapSyncStep, coinLatestInfoSyncStep, coinDetailInfoSyncStep, exchangeMapSyncStep, exchangeDetailInfoSyncStep, coinInfoBulkStep, coinMetaStep, coinMappingStep);
    }

    @Test
    @DisplayName("cmcDataSyncJob 빈이 올바르게 생성되는지 확인")
    void cmcDataSyncJob_ShouldBeCreatedCorrectly() {
        // Act
        Job job = cmcCoinBatchJob.cmcDataSyncJob();

        // Assert
        assertNotNull(job);
    }
}
