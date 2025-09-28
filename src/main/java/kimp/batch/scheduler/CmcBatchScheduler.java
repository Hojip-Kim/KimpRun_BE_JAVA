package kimp.batch.scheduler;

import kimp.cmc.dao.CmcBatchDao;
import kimp.common.lock.DistributedLockService;
import kimp.common.ratelimit.DistributedRateLimiter;
import kimp.common.ratelimit.RateLimitResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CmcBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job cmcDataSyncJob;
    private final CmcBatchDao cmcBatchDao;
    private final DistributedLockService distributedLockService;
    private final DistributedRateLimiter distributedRateLimiter;
    
    // 분산 락 설정
    private static final String CMC_BATCH_LOCK_KEY = "cmc-data-sync-daily";
    private static final int LOCK_TTL_SECONDS = 3600; // 1시간 (배치 작업 최대 예상 시간)

    /**
     * 매일 새벽 2시에 CoinMarketCap 데이터 동기화 실행 (Redis 분산 락 적용)
     * CoinMarketCap 데이터가 보통 UTC 기준으로 갱신되므로 한국시간 새벽 2시에 실행
     * 
     * 분산 환경에서 여러 서버 중 하나만 배치 작업을 수행하도록 보장
     */
    @Scheduled(cron = "0 0 2 * * ?", zone = "Asia/Seoul", scheduler = "batchTaskScheduler")
    public void runCmcDataSyncJob() {
        // 분산 락 획득 시도
        String lockToken = distributedLockService.tryLock(CMC_BATCH_LOCK_KEY, LOCK_TTL_SECONDS);
        
        if (lockToken == null) {
            log.info("🔒 CMC 데이터 동기화 건너뜀 - 다른 서버에서 처리 중 또는 Redis 연결 실패");
            return;
        }
        
        try {
            log.info("CMC 데이터 동기화 시작 - 서버가 분산 락을 획득했습니다");
            log.info("실행 시간: {}", LocalDateTime.now());
            
            // 동기화 필요 여부 사전 확인
            boolean coinMapSync = cmcBatchDao.shouldRunCoinMapSync();
            boolean coinInfoSync = cmcBatchDao.shouldRunCoinInfoSync();
            boolean exchangeSync = cmcBatchDao.shouldRunExchangeSync();
            boolean coinRankSync = cmcBatchDao.shouldRunCoinRankSync();
            boolean coinMetaSync = cmcBatchDao.shouldRunCoinMetaSync();
            
            log.info("동기화 필요 여부 - 코인 맵: {}, 코인 상세: {}, 거래소: {}, 코인 랭킹: {}, 코인 메타: {}", 
                    coinMapSync, coinInfoSync, exchangeSync, coinRankSync, coinMetaSync);
            
            if (!coinMapSync && !coinInfoSync && !exchangeSync && !coinRankSync && !coinMetaSync) {
                log.info("모든 데이터가 최신 상태입니다. 배치 작업을 건너뜁니다.");
                return;
            }
            
            // CMC API Rate Limit 사전 확인
            long currentUsage = 0;
            try {
                currentUsage = distributedRateLimiter.getCurrentUsage("cmc-api", 60);
                log.info("현재 CMC API 사용률: {}/30 (1분 윈도우)", currentUsage);
            } catch (Exception e) {
                log.warn("CMC API 사용률 확인 실패 (Redis 연결 문제): {}", e.getMessage());
            }
            
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLocalDateTime("executeTime", LocalDateTime.now())
                    .addString("lockToken", lockToken) // 배치 Job에서 락 연장 가능하도록
                    .toJobParameters();
            
            // 장시간 배치 실행 시 락 연장을 위한 추가 파라미터
            jobParameters = new JobParametersBuilder(jobParameters)
                    .addString("distributedLockKey", CMC_BATCH_LOCK_KEY)
                    .toJobParameters();
            
            jobLauncher.run(cmcDataSyncJob, jobParameters);
            
            log.info("CMC 데이터 동기화 완료 - 분산 락 해제 예정");
            
        } catch (Exception e) {
            log.error("CoinMarketCap 데이터 동기화 중 오류 발생", e);
            
        } finally {
            // 락 해제
            if (distributedLockService.releaseLock(CMC_BATCH_LOCK_KEY, lockToken)) {
                log.debug("CMC 배치 분산 락 해제 성공");
            } else {
                log.warn("CMC 배치 분산 락 해제 실패 - 이미 만료되었을 수 있습니다");
            }
        }
    }

    /**
     * 수동 CMC 데이터 동기화 (관리자 API용)
     * 분산 락을 적용하여 안전한 수동 실행 제공
     * 
     * @throws IllegalStateException 다른 서버에서 배치 실행 중일 때
     */
    public void runManualCmcDataSync() {
        // 정기 배치와 수동 배치가 동시에 실행되지 않도록 같은 락 키 사용
        String lockToken = distributedLockService.tryLock(CMC_BATCH_LOCK_KEY, LOCK_TTL_SECONDS);
        
        if (lockToken == null) {
            throw new IllegalStateException("다른 서버에서 CMC 배치가 실행 중이거나 Redis 연결에 실패했습니다");
        }
        
        try {
            log.info("🔧 수동 CMC 데이터 동기화 시작");
            
            // Rate Limit 확인 (카운터 증가 없이 체크만)
            try {
                RateLimitResult rateLimitResult = 
                    distributedRateLimiter.checkCmcApiLimit();
                
                if (!rateLimitResult.isAllowed()) {
                    throw new IllegalStateException("CMC API Rate Limit 초과. 잠시 후 재시도해주세요. (남은: " 
                        + rateLimitResult.getRemainingRequests() + "/" + rateLimitResult.getLimit() + ")");
                }
            } catch (Exception e) {
                log.error("Rate Limit 확인 실패 (Redis 연결 문제): {}", e.getMessage());
                throw new IllegalStateException("CMC API Rate Limit 확인 실패: " + e.getMessage(), e);
            }
            
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLocalDateTime("executeTime", LocalDateTime.now())
                    .addString("executionType", "MANUAL")
                    .addString("lockToken", lockToken)
                    .addString("distributedLockKey", CMC_BATCH_LOCK_KEY)
                    .toJobParameters();
            
            jobLauncher.run(cmcDataSyncJob, jobParameters);
            
            log.info("수동 CMC 데이터 동기화 완료");
            
        } catch (Exception e) {
            log.error("수동 CMC 데이터 동기화 중 오류 발생", e);
            throw new RuntimeException("CMC 배치 실행 실패: " + e.getMessage(), e);
            
        } finally {
            distributedLockService.releaseLock(CMC_BATCH_LOCK_KEY, lockToken);
        }
    }
    
    /**
     * 현재 CMC API 사용률 조회
     */
    public String getCmcApiUsageStatus() {
        long currentUsage = 0;
        try {
            currentUsage = distributedRateLimiter.getCurrentUsage("cmc-api", 60);
        } catch (Exception e) {
            log.debug("CMC API 사용률 조회 실패: {}", e.getMessage());
        }
        // Redis가 연결되지 않은 경우 null이 반환될 수 있습니다
        String lockOwner = null;
        try {
            lockOwner = distributedLockService.getLockOwner(CMC_BATCH_LOCK_KEY);
        } catch (Exception e) {
            log.debug("Redis 락 소유자 조회 실패: {}", e.getMessage());
        }
        
        return String.format("CMC API 사용률: %d/40 (1분), 배치 실행 중: %s", 
            currentUsage, lockOwner != null ? lockOwner : "없음");
    }
} 