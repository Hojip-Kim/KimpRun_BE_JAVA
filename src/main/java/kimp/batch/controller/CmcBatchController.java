package kimp.batch.controller;

import kimp.batch.scheduler.CmcBatchScheduler;
import kimp.exception.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/batch/cmc")
@RequiredArgsConstructor
public class CmcBatchController {

    private final JobLauncher jobLauncher;
    private final Job cmcDataSyncJob;
    private final JobExplorer jobExplorer;
    private final CmcBatchScheduler cmcBatchScheduler;

    /**
     * CoinMarketCap 데이터 전체 동기화 실행 (분산 락 적용)
     * 
     * Redis 분산 락과 Rate Limiter를 적용하여 안전한 배치 실행 제공
     */
    @PostMapping("/sync")
    public ApiResponse<Map<String, Object>> runCmcDataSync(
            @RequestParam(defaultValue = "manual") String mode) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("=== 분산 락 기반 CMC 데이터 동기화 실행 요청 ===");
            log.info("실행 모드: {}", mode);
            log.info("요청 시간: {}", LocalDateTime.now());
            
            // 분산 락을 활용한 안전한 배치 실행
            cmcBatchScheduler.runManualCmcDataSync();
            
            response.put("success", true);
            response.put("message", "CMC 데이터 동기화가 성공적으로 시작되었습니다");
            response.put("mode", mode);
            response.put("timestamp", LocalDateTime.now());
            response.put("distributedLockApplied", true);
            
            return ApiResponse.success(response);
            
        } catch (IllegalStateException e) {
            log.warn("분산 락 충돌: {}", e.getMessage());
            response.put("success", false);
            response.put("error", "DISTRIBUTED_LOCK_CONFLICT");
            response.put("message", e.getMessage());
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
    public ApiResponse<Map<String, Object>> getCmcApiStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String usageStatus = cmcBatchScheduler.getCmcApiUsageStatus();
            
            response.put("success", true);
            response.put("status", usageStatus);
            response.put("timestamp", LocalDateTime.now());
            
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
    public ApiResponse<Map<String, Object>> getJobExecutionStatus(
            @PathVariable Long jobExecutionId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
            
            if (jobExecution == null) {
                return ApiResponse.error(404, "JOB_EXECUTION_NOT_FOUND", "해당 Job Execution을 찾을 수 없습니다: " + jobExecutionId);
            }
            
            // Step 실행 정보
            Map<String, Object> stepExecutions = new HashMap<>();
            for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
                Map<String, Object> stepInfo = new HashMap<>();
                stepInfo.put("status", stepExecution.getStatus().name());
                stepInfo.put("readCount", stepExecution.getReadCount());
                stepInfo.put("writeCount", stepExecution.getWriteCount());
                stepInfo.put("commitCount", stepExecution.getCommitCount());
                stepInfo.put("rollbackCount", stepExecution.getRollbackCount());
                stepInfo.put("filterCount", stepExecution.getFilterCount());
                stepInfo.put("startTime", stepExecution.getStartTime());
                stepInfo.put("endTime", stepExecution.getEndTime());
                stepInfo.put("exitCode", stepExecution.getExitStatus().getExitCode());
                
                stepExecutions.put(stepExecution.getStepName(), stepInfo);
            }
            response.put("stepExecutions", stepExecutions);
            
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
    public ApiResponse<Map<String, Object>> getJobExecutionHistory(
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<JobInstance> jobInstances = jobExplorer.findJobInstancesByJobName("cmcDataSyncJob", 0, limit);
            
            List<Map<String, Object>> executionHistory = jobInstances.stream()
                .flatMap(jobInstance -> jobExplorer.getJobExecutions(jobInstance).stream())
                .sorted((e1, e2) -> e2.getStartTime().compareTo(e1.getStartTime())) // 최신순 정렬
                .limit(limit)
                .map(jobExecution -> {
                    Map<String, Object> executionInfo = new HashMap<>();
                    executionInfo.put("jobExecutionId", jobExecution.getId());
                    executionInfo.put("jobInstanceId", jobExecution.getJobInstance().getId());
                    executionInfo.put("status", jobExecution.getStatus().name());
                    executionInfo.put("startTime", jobExecution.getStartTime());
                    executionInfo.put("endTime", jobExecution.getEndTime());
                    executionInfo.put("exitCode", jobExecution.getExitStatus().getExitCode());
                    
                    // Job Parameters 정보
                    Map<String, Object> parameters = new HashMap<>();
                    jobExecution.getJobParameters().getParameters().forEach((key, value) -> {
                        parameters.put(key, value.getValue());
                    });
                    executionInfo.put("parameters", parameters);
                    
                    return executionInfo;
                })
                .toList();
            
            response.put("success", true);
            response.put("totalCount", executionHistory.size());
            response.put("executions", executionHistory);
            
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
    public ApiResponse<Map<String, Object>> getRunningJobs() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> jobNames = jobExplorer.getJobNames();
            List<Map<String, Object>> runningJobs = jobNames.stream()
                .filter(jobName -> jobName.equals("cmcDataSyncJob"))
                .flatMap(jobName -> jobExplorer.findRunningJobExecutions(jobName).stream())
                .map(jobExecution -> {
                    Map<String, Object> jobInfo = new HashMap<>();
                    jobInfo.put("jobExecutionId", jobExecution.getId());
                    jobInfo.put("jobName", jobExecution.getJobInstance().getJobName());
                    jobInfo.put("status", jobExecution.getStatus().name());
                    jobInfo.put("startTime", jobExecution.getStartTime());
                    
                    // 현재 실행 중인 Step 정보
                    List<String> runningSteps = jobExecution.getStepExecutions().stream()
                        .filter(step -> step.getStatus().isRunning())
                        .map(StepExecution::getStepName)
                        .toList();
                    jobInfo.put("runningSteps", runningSteps);
                    
                    return jobInfo;
                })
                .toList();
            
            response.put("success", true);
            response.put("runningJobsCount", runningJobs.size());
            response.put("runningJobs", runningJobs);
            
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
    public ApiResponse<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Job Repository 연결 상태 확인
            List<String> jobNames = jobExplorer.getJobNames();
            boolean hasTargetJob = jobNames.contains("cmcDataSyncJob");
            
            response.put("success", true);
            response.put("message", "배치 시스템이 정상 작동 중입니다");
            response.put("jobRepositoryConnected", true);
            response.put("targetJobExists", hasTargetJob);
            response.put("availableJobs", jobNames);
            response.put("timestamp", LocalDateTime.now());
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("배치 시스템 헬스 체크 중 오류 발생", e);
            return ApiResponse.error(500, "INTERNAL_ERROR", "배치 시스템에 문제가 있습니다: " + e.getMessage());
        }
    }
} 