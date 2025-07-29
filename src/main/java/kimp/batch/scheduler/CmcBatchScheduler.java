package kimp.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class CmcBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job cmcDataSyncJob;

    /**
     * 매일 새벽 2시에 CoinMarketCap 데이터 동기화 실행
     * CoinMarketCap 데이터가 보통 UTC 기준으로 갱신되므로 한국시간 새벽 2시에 실행
     */
    @Scheduled(cron = "0 0 2 * * ?", zone = "Asia/Seoul")
    public void runCmcDataSyncJob() {
        try {
            log.info("=== 스케줄된 CoinMarketCap 데이터 동기화 시작 ===");
            log.info("실행 시간: {}", LocalDateTime.now());
            
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLocalDateTime("executeTime", LocalDateTime.now())
                    .toJobParameters();
            
            jobLauncher.run(cmcDataSyncJob, jobParameters);
            
            log.info("=== 스케줄된 CoinMarketCap 데이터 동기화 완료 ===");
            
        } catch (Exception e) {
            log.error("CoinMarketCap 데이터 동기화 중 오류 발생", e);
        }
    }


    
} 