package kimp.batch.controller;

import kimp.batch.scheduler.CmcBatchScheduler;
import kimp.exception.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import kimp.batch.dto.internal.JobExecutionInfo;
import kimp.batch.dto.internal.StepExecutionInfo;
import kimp.batch.dto.response.*;

@Slf4j
@RestController
@RequestMapping("/batch/cmc")
@RequiredArgsConstructor
public class CmcBatchController {

    private final JobLauncher jobLauncher;
    private final Job cmcDataSyncJob;
    private final JobExplorer jobExplorer;
    private final CmcBatchScheduler cmcBatchScheduler;
    private final kimp.common.ratelimit.DistributedRateLimiter distributedRateLimiter;
    private final kimp.common.lock.DistributedLockService distributedLockService;

    /**
     * CoinMarketCap 데이터 전체 동기화 실행 (분산 락 적용)
     * 
     * Redis 분산 락과 Rate Limiter를 적용하여 안전한 배치 실행 제공
     */
    @PostMapping("/sync")
    public ApiResponse<CmcBatchSyncResponse> runCmcDataSync(
            @RequestParam(defaultValue = "manual") String mode) {
        
        try {
            log.info("=== 분산 락 기반 CMC 데이터 동기화 실행 요청 ===");
            log.info("실행 모드: {}", mode);
            log.info("요청 시간: {}", LocalDateTime.now());
            
            // 비동기로 배치 실행 (HTTP 커넥션 해제를 위함)
            CompletableFuture.runAsync(() -> {
                try {
                    cmcBatchScheduler.runManualCmcDataSync();
                } catch (Exception e) {
                    log.error("CMC 데이터 동기화 비동기 실행 중 오류 발생", e);
                }
            });
            
            CmcBatchSyncResponse response = CmcBatchSyncResponse.builder()
                .message("CMC 데이터 동기화가 비동기로 시작되었습니다")
                .mode(mode)
                .timestamp(LocalDateTime.now())
                .distributedLockApplied(true)
                .asyncExecution(true)
                .build();
            
            return ApiResponse.success(response);
            
        } catch (IllegalStateException e) {
            log.warn("분산 락 충돌: {}", e.getMessage());
            return ApiResponse.error(409, "DISTRIBUTED_LOCK_CONFLICT", e.getMessage());
            
        } catch (RuntimeException e) {
            log.error("CMC 배치 실행 중 오류 발생", e);
            return ApiResponse.error(500, "BATCH_EXECUTION_ERROR", e.getMessage());
            
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생", e);
            return ApiResponse.error(500, "UNEXPECTED_ERROR", "배치 작업 실행 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * CMC API 사용률 및 분산 락 상태 조회
     */
    @GetMapping("/api-status")
    public ApiResponse<CmcApiStatusResponse> getCmcApiStatus() {
        try {
            String usageStatus = cmcBatchScheduler.getCmcApiUsageStatus();
            
            CmcApiStatusResponse response = CmcApiStatusResponse.builder()
                .status(usageStatus)
                .timestamp(LocalDateTime.now())
                .build();
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("CMC API 상태 조회 중 오류 발생", e);
            return ApiResponse.error(500, "STATUS_CHECK_ERROR", "상태 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 Job Execution 상태 조회
     */
    @GetMapping("/status/{jobExecutionId}")
    public ApiResponse<JobExecutionStatusResponse> getJobExecutionStatus(
            @PathVariable Long jobExecutionId) {
        
        try {
            JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
            
            if (jobExecution == null) {
                return ApiResponse.error(404, "JOB_EXECUTION_NOT_FOUND", "해당 Job Execution을 찾을 수 없습니다: " + jobExecutionId);
            }
            
            // Step 실행 정보
            Map<String, StepExecutionInfo> stepExecutions = new HashMap<>();
            for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
                StepExecutionInfo stepInfo = new StepExecutionInfo(
                    stepExecution.getStatus().name(),
                    stepExecution.getReadCount(),
                    stepExecution.getWriteCount(),
                    stepExecution.getCommitCount(),
                    stepExecution.getRollbackCount(),
                    stepExecution.getFilterCount(),
                    stepExecution.getStartTime(),
                    stepExecution.getEndTime(),
                    stepExecution.getExitStatus().getExitCode()
                );
                
                stepExecutions.put(stepExecution.getStepName(), stepInfo);
            }
            
            JobExecutionStatusResponse response = JobExecutionStatusResponse.builder()
                .stepExecutions(stepExecutions)
                .build();
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("Job Execution 상태 조회 중 오류 발생: {}", jobExecutionId, e);
            return ApiResponse.error(500, "INTERNAL_ERROR", "상태 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * CoinMarketCap Job 실행 이력 조회
     */
    @GetMapping("/history")
    public ApiResponse<JobHistoryResponse> getJobExecutionHistory(
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<JobInstance> jobInstances = jobExplorer.findJobInstancesByJobName("cmcDataSyncJob", 0, limit);
            
            List<JobExecutionInfo> executionHistory = jobInstances.stream()
                .flatMap(jobInstance -> jobExplorer.getJobExecutions(jobInstance).stream())
                .sorted((e1, e2) -> e2.getStartTime().compareTo(e1.getStartTime())) // 최신순 정렬
                .limit(limit)
                .map(jobExecution -> {
                    // Job Parameters 정보
                    Map<String, Object> parameters = new HashMap<>();
                    jobExecution.getJobParameters().getParameters().forEach((key, value) -> {
                        parameters.put(key, value.getValue());
                    });
                    
                    return new JobExecutionInfo(
                        jobExecution.getId(),
                        jobExecution.getJobInstance().getId(),
                        jobExecution.getStatus().name(),
                        jobExecution.getStartTime(),
                        jobExecution.getEndTime(),
                        jobExecution.getExitStatus().getExitCode(),
                        parameters,
                        null // runningSteps는 이력 조회에서는 사용하지 않음
                    );
                })
                .toList();
            
            JobHistoryResponse response = JobHistoryResponse.builder()
                .totalCount(executionHistory.size())
                .executions(executionHistory)
                .build();
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("Job 실행 이력 조회 중 오류 발생", e);
            return ApiResponse.error(500, "INTERNAL_ERROR", "실행 이력 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 현재 실행 중인 Job 조회
     */
    @GetMapping("/running")
    public ApiResponse<RunningJobsResponse> getRunningJobs() {
        try {
            List<String> jobNames = jobExplorer.getJobNames();
            List<JobExecutionInfo> runningJobs = jobNames.stream()
                .filter(jobName -> jobName.equals("cmcDataSyncJob"))
                .flatMap(jobName -> jobExplorer.findRunningJobExecutions(jobName).stream())
                .map(jobExecution -> {
                    // 현재 실행 중인 Step 정보
                    List<String> runningSteps = jobExecution.getStepExecutions().stream()
                        .filter(step -> step.getStatus().isRunning())
                        .map(StepExecution::getStepName)
                        .toList();
                    
                    return new JobExecutionInfo(
                        jobExecution.getId(),
                        jobExecution.getJobInstance().getId(),
                        jobExecution.getStatus().name(),
                        jobExecution.getStartTime(),
                        null, // endTime은 아직 실행 중이므로 null
                        null, // exitCode도 아직 실행 중이므로 null
                        null, // parameters는 이 조회에서는 사용하지 않음
                        runningSteps
                    );
                })
                .toList();
            
            RunningJobsResponse response = RunningJobsResponse.builder()
                .runningJobsCount(runningJobs.size())
                .runningJobs(runningJobs)
                .build();
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("실행 중인 Job 조회 중 오류 발생", e);
            return ApiResponse.error(500, "INTERNAL_ERROR", "실행 중인 Job 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 배치 작업 헬스 체크
     */
    @GetMapping("/health")
    public ApiResponse<BatchHealthResponse> healthCheck() {
        try {
            // Job Repository 연결 상태 확인
            List<String> jobNames = jobExplorer.getJobNames();
            boolean hasTargetJob = jobNames.contains("cmcDataSyncJob");
            
            BatchHealthResponse response = BatchHealthResponse.builder()
                .message("배치 시스템이 정상 작동 중입니다")
                .jobRepositoryConnected(true)
                .targetJobExists(hasTargetJob)
                .availableJobs(jobNames)
                .timestamp(LocalDateTime.now())
                .build();
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("배치 시스템 헬스 체크 중 오류 발생", e);
            return ApiResponse.error(500, "INTERNAL_ERROR", "배치 시스템에 문제가 있습니다: " + e.getMessage());
        }
    }
    
    /**
     * 분산 락 강제 해제
     * 
     * CMC 배치 실행 중 서버 장애나 예상치 못한 종료로 인해 락이 남아있을 때 사용
     * 주의: 다른 서버에서 실제로 배치가 실행 중일 수도 있으므로 신중히 사용
     */
    @PostMapping("/unlock")
    public ApiResponse<UnlockResponse> forceUnlockCmcBatch() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String clientIp = "unknown";
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                clientIp = request.getRemoteAddr();
            }
            
            log.warn("CMC 배치 분산 락 강제 해제 요청 - 실행자 IP: {}", clientIp);
            
            // 분산 락 강제 해제 (스케줄러와 동일한 락 키 사용)
            String lockKey = "cmc-data-sync-daily";
            boolean unlocked = distributedLockService.forceUnlock(lockKey);
            
            String message = unlocked ? 
                "CMC 배치 분산 락이 성공적으로 해제되었습니다" : 
                "분산 락 해제에 실패했습니다 (락이 존재하지 않거나 해제 불가)";
            
            UnlockResponse response = UnlockResponse.builder()
                .message(message)
                .unlockTime(LocalDateTime.now())
                .lockKey(lockKey)
                .executorIp(clientIp)
                .build();
            
            if (unlocked) {
                return ApiResponse.success(response);
            } else {
                log.warn("CMC 배치 분산 락 해제 실패 - 락 키: {}", lockKey);
                return ApiResponse.error(400, "UNLOCK_FAILED", message);
            }
            
        } catch (Exception e) {
            log.error("CMC 배치 분산 락 해제 중 오류 발생", e);
            return ApiResponse.error(500, "UNLOCK_ERROR", 
                "분산 락 해제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 동기화된 Rate Limiter 상태 조회
     */
    @GetMapping("/rate-limit-status")
    public ApiResponse<RateLimitStatusResponse> getRateLimitStatus() {
        try {
            long currentUsage = distributedRateLimiter.getCurrentUsage("cmc-api", 60);
            
            RateLimitStatusResponse response = RateLimitStatusResponse.builder()
                .currentUsage(currentUsage)
                .limit(30)
                .windowSeconds(60)
                .timestamp(LocalDateTime.now())
                .build();
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("Rate Limit 상태 조회 중 오류 발생", e);
            return ApiResponse.error(500, "STATUS_CHECK_ERROR", 
                "Rate Limit 상태 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * CMC API Rate Limit 리셋
     * 
     * Rate Limit 카운터를 초기화하여 API 호출을 다시 가능하게 함
     * 주의: 실제 API 제한과 별개로 내부 카운터만 리셋하므로 신중히 사용
     */
    @PostMapping("/reset-rate-limit")
    public ApiResponse<RateLimitResetResponse> resetCmcRateLimit() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String clientIp = "unknown";
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                clientIp = request.getRemoteAddr();
            }
            
            log.warn("CMC API Rate Limit 리셋 요청 - 실행자 IP: {}", clientIp);
            
            // Redis Rate Limit 리셋
            distributedRateLimiter.resetRateLimit("cmc-api");
            
            // 리셋 후 현재 상태 확인
            long currentUsage = distributedRateLimiter.getCurrentUsage("cmc-api", 60);
            
            RateLimitResetResponse response = RateLimitResetResponse.builder()
                .message("CMC API Rate Limit이 성공적으로 리셋되었습니다")
                .resetTime(LocalDateTime.now())
                .currentUsage(currentUsage)
                .limit(30)
                .windowSeconds(60)
                .build();

            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("CMC Rate Limit 리셋 중 오류 발생", e);
            return ApiResponse.error(500, "RATE_LIMIT_RESET_ERROR", 
                "Rate Limit 리셋 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
} 