package kimp.common.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Redis 기반 분산 Rate Limiter
 * 
 * 여러 서버 인스턴스에서 공통으로 API 호출 제한을 관리
 * Sliding Window Counter 알고리즘 사용
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedRateLimiter {
    
    private final RedisTemplate<String, Object> distributedLockRedisTemplate;
    private final String serverInstanceId;
    
    // Sliding Window Rate Limiting Lua 스크립트
    private static final String RATE_LIMIT_SCRIPT = 
        "local key = KEYS[1] " +
        "local limit = tonumber(ARGV[1]) " +
        "local window = tonumber(ARGV[2]) " +
        "local current_time = tonumber(ARGV[3]) " +
        "local server_id = ARGV[4] " +
        "" +
        "-- 만료된 요청 제거 " +
        "redis.call('zremrangebyscore', key, 0, current_time - window * 1000) " +
        "" +
        "-- 현재 윈도우 내 요청 수 확인 " +
        "local current_requests = redis.call('zcard', key) " +
        "" +
        "if current_requests < limit then " +
        "    -- 요청 허용, 현재 요청 기록 " +
        "    redis.call('zadd', key, current_time, current_time .. ':' .. server_id) " +
        "    redis.call('expire', key, window + 1) " +
        "    return {1, limit - current_requests - 1} " +
        "else " +
        "    -- 요청 거부 " +
        "    return {0, 0} " +
        "end";
    
    /**
     * CoinMarketCap API Rate Limiter
     * 
     * @return {허용여부, 남은요청수}
     */
    public RateLimitResult tryAcquireCmcApiLimit() {
        return tryAcquire("cmc-api", 40, 60); // 1분당 40회
    }
    
    /**
     * 일반적인 Rate Limiting
     * 
     * @param resource 리소스 식별자
     * @param limit 제한 수
     * @param windowSeconds 시간 윈도우 (초)
     * @return 제한 결과
     */
    public RateLimitResult tryAcquire(String resource, int limit, int windowSeconds) {
        String key = "kimprun:ratelimit:" + resource;
        long currentTime = Instant.now().toEpochMilli();
        
        try {
            DefaultRedisScript<Object> script = new DefaultRedisScript<>();
            script.setScriptText(RATE_LIMIT_SCRIPT);
            script.setResultType(Object.class);
            
            Object result = distributedLockRedisTemplate.execute(
                script,
                Collections.singletonList(key),
                String.valueOf(limit),
                String.valueOf(windowSeconds),
                String.valueOf(currentTime),
                serverInstanceId
            );
            
            if (result instanceof java.util.List) {
                List<Long> resultList = (List<Long>) result;
                boolean allowed = resultList.get(0) == 1L;
                long remaining = resultList.get(1);
                
                if (allowed) {
                    log.debug("Rate Limit 통과 - 서버: {}, 리소스: {}, 남은 요청: {}/{}",
                        serverInstanceId, resource, remaining, limit);
                } else {
                    log.warn("Rate Limit 초과 - 서버: {}, 리소스: {}, 제한: {}/{} ({}초 윈도우)",
                        serverInstanceId, resource, limit, limit, windowSeconds);
                }
                
                return new RateLimitResult(allowed, remaining, limit, windowSeconds);
            }
            
            log.error("Rate Limit 스크립트 결과 파싱 실패 - 서버: {}, 리소스: {}", 
                serverInstanceId, resource);
            return new RateLimitResult(false, 0, limit, windowSeconds);
            
        } catch (Exception e) {
            log.error("Rate Limit 확인 중 오류 발생 - 서버: {}, 리소스: {}", 
                serverInstanceId, resource, e);
            // Redis 장애 시 요청 허용 (Fail Open)
            return new RateLimitResult(true, limit - 1, limit, windowSeconds);
        }
    }
    
    /**
     * 현재 사용률 조회
     * 
     * @param resource 리소스 식별자
     * @param windowSeconds 시간 윈도우
     * @return 현재 사용 중인 요청 수
     */
    public long getCurrentUsage(String resource, int windowSeconds) {
        String key = "kimprun:ratelimit:" + resource;
        long currentTime = Instant.now().toEpochMilli();
        
        try {
            // 만료된 요청 정리
            distributedLockRedisTemplate.opsForZSet()
                .removeRangeByScore(key, 0, currentTime - windowSeconds * 1000L);
            
            // 현재 요청 수 반환
            Long count = distributedLockRedisTemplate.opsForZSet().count(key, 
                currentTime - windowSeconds * 1000L, currentTime);
            
            return count != null ? count : 0L;
            
        } catch (Exception e) {
            log.error("Rate Limit 사용률 조회 중 오류 발생 - 리소스: {}", resource, e);
            return 0L;
        }
    }
    
    /**
     * Rate Limit 초기화 (테스트/긴급상황용)
     * 
     * @param resource 리소스 식별자
     */
    public void resetRateLimit(String resource) {
        String key = "kimprun:ratelimit:" + resource;
        try {
            distributedLockRedisTemplate.delete(key);
            log.info("Rate Limit 초기화 완료 - 서버: {}, 리소스: {}", serverInstanceId, resource);
        } catch (Exception e) {
            log.error("Rate Limit 초기화 중 오류 발생 - 리소스: {}", resource, e);
        }
    }
    
    /**
     * Rate Limit 결과
     */
    public static class RateLimitResult {
        private final boolean allowed;
        private final long remainingRequests;
        private final int limit;
        private final int windowSeconds;
        
        public RateLimitResult(boolean allowed, long remainingRequests, int limit, int windowSeconds) {
            this.allowed = allowed;
            this.remainingRequests = remainingRequests;
            this.limit = limit;
            this.windowSeconds = windowSeconds;
        }
        
        public boolean isAllowed() { return allowed; }
        public long getRemainingRequests() { return remainingRequests; }
        public int getLimit() { return limit; }
        public int getWindowSeconds() { return windowSeconds; }
        
        @Override
        public String toString() {
            return String.format("RateLimit{allowed=%s, remaining=%d/%d, window=%ds}", 
                allowed, remainingRequests, limit, windowSeconds);
        }
    }
}