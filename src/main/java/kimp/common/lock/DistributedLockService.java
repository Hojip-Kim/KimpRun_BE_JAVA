package kimp.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

/**
 * Redis 기반 분산 락 서비스
 * 
 * 분산 환경에서 하나의 인스턴스만 특정 작업을 수행하도록 보장
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DistributedLockService {
    
    private final RedisTemplate<String, Object> distributedLockRedisTemplate;
    private final String serverInstanceId;
    
    // Lua 스크립트로 원자적 락 획득
    private static final String ACQUIRE_LOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "    return redis.call('expire', KEYS[1], ARGV[2]) " +
        "else " +
        "    local result = redis.call('set', KEYS[1], ARGV[1], 'PX', ARGV[2], 'NX') " +
        "    if result then return 1 else return 0 end " +
        "end";
    
    // Lua 스크립트로 원자적 락 해제
    private static final String RELEASE_LOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "    return redis.call('del', KEYS[1]) " +
        "else " +
        "    return 0 " +
        "end";
    
    /**
     * 분산 락 획득 시도
     * 
     * @param lockKey 락 키 (예: "batch:cmc-sync", "batch:notice-scraper")
     * @param ttlSeconds 락 유지 시간 (초)
     * @return 락 획득 성공 시 락 토큰, 실패 시 null
     */
    public String tryLock(String lockKey, int ttlSeconds) {
        String lockValue = serverInstanceId + ":" + UUID.randomUUID().toString();
        String fullLockKey = "kimprun:lock:" + lockKey;
        
        try {
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(ACQUIRE_LOCK_SCRIPT);
            script.setResultType(Long.class);
            
            Long result = distributedLockRedisTemplate.execute(
                script, 
                Collections.singletonList(fullLockKey), 
                lockValue, 
                String.valueOf(ttlSeconds * 1000)
            );
            
            if (result != null && result == 1L) {
                return lockValue;
            } else {
                log.debug("분산 락 획득 실패 - 서버: {}, 락키: {} (다른 서버에서 처리 중)", 
                    serverInstanceId, lockKey);
                return null;
            }
            
        } catch (Exception e) {
            log.error("분산 락 획득 중 오류 발생 - 서버: {}, 락키: {}", 
                serverInstanceId, lockKey, e);
            return null;
        }
    }
    
    /**
     * 분산 락 해제
     * 
     * @param lockKey 락 키
     * @param lockValue 락 획득 시 받은 토큰
     * @return 해제 성공 여부
     */
    public boolean releaseLock(String lockKey, String lockValue) {
        if (lockValue == null) {
            return false;
        }
        
        String fullLockKey = "kimprun:lock:" + lockKey;
        
        try {
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(RELEASE_LOCK_SCRIPT);
            script.setResultType(Long.class);
            
            Long result = distributedLockRedisTemplate.execute(
                script, 
                Collections.singletonList(fullLockKey), 
                lockValue
            );
            
            boolean released = result != null && result == 1L;
            if (released) {
            } else {
                log.warn("분산 락 해제 실패 - 서버: {}, 락키: {} (이미 만료되었거나 다른 서버 소유)", 
                    serverInstanceId, lockKey);
            }
            
            return released;
            
        } catch (Exception e) {
            log.error("분산 락 해제 중 오류 발생 - 서버: {}, 락키: {}", 
                serverInstanceId, lockKey, e);
            return false;
        }
    }
    
    /**
     * 락 상태 확인
     * 
     * @param lockKey 락 키
     * @return 현재 락을 보유한 서버 정보, 락이 없으면 null
     */
    public String getLockOwner(String lockKey) {
        String fullLockKey = "kimprun:lock:" + lockKey;
        try {
            Object owner = distributedLockRedisTemplate.opsForValue().get(fullLockKey);
            return owner != null ? owner.toString() : null;
        } catch (Exception e) {
            log.error("락 소유자 확인 중 오류 발생 - 락키: {}", lockKey, e);
            return null;
        }
    }
    
    /**
     * 락 TTL 연장 (장시간 실행되는 배치용)
     * 
     * @param lockKey 락 키
     * @param lockValue 락 토큰
     * @param additionalSeconds 추가 연장 시간 (초)
     * @return 연장 성공 여부
     */
    public boolean extendLock(String lockKey, String lockValue, int additionalSeconds) {
        String fullLockKey = "kimprun:lock:" + lockKey;
        
        String script = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('expire', KEYS[1], ARGV[2]) " +
            "else " +
            "    return 0 " +
            "end";
        
        try {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);
            
            Long result = distributedLockRedisTemplate.execute(
                redisScript, 
                Collections.singletonList(fullLockKey), 
                lockValue, 
                String.valueOf(additionalSeconds)
            );
            
            boolean extended = result != null && result == 1L;
            if (extended) {
                log.debug("분산 락 TTL 연장 완료 - 서버: {}, 락키: {}, 연장: {}초", 
                    serverInstanceId, lockKey, additionalSeconds);
            }
            
            return extended;
            
        } catch (Exception e) {
            log.error("분산 락 TTL 연장 중 오류 발생 - 서버: {}, 락키: {}", 
                serverInstanceId, lockKey, e);
            return false;
        }
    }
}