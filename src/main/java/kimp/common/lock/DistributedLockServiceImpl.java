package kimp.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 분산 환경에서 하나의 인스턴스만 특정 작업을 수행하도록 보장
 * Redisson의 RLock을 사용.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DistributedLockServiceImpl implements DistributedLockService {
    
    private final RedissonClient redissonClient;
    private final String serverInstanceId;
    
    /**
     * 분산 락 획득 시도
     * 
     * @param lockKey 락 키 (예: "batch:cmc-sync", "batch:notice-scraper")
     * @param ttlSeconds 락 유지 시간 (초)
     * @return 락 획득 성공 시 락 토큰, 실패 시 null
     */
    @Override
    public String tryLock(String lockKey, int ttlSeconds) {
        String fullLockKey = "kimprun:lock:" + lockKey;
        
        try {
            RLock lock = redissonClient.getLock(fullLockKey);
            
            // 락 획득 시도 (즉시 반환, 대기하지 않음)
            boolean acquired = lock.tryLock(0, ttlSeconds, TimeUnit.SECONDS);
            
            if (acquired) {
                // Redisson에서는 락 토큰이 내부적으로 관리되므로 식별용 문자열 반환
                String lockToken = serverInstanceId + ":" + System.currentTimeMillis();
                log.debug("분산 락 획득 성공 - 서버: {}, 락키: {}, TTL: {}초", 
                    serverInstanceId, lockKey, ttlSeconds);
                return lockToken;
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
     * @param lockValue 락 획득 시 받은 토큰 (Redisson에서는 사용하지 않음)
     * @return 해제 성공 여부
     */
    @Override
    public boolean releaseLock(String lockKey, String lockValue) {
        if (lockValue == null) {
            return false;
        }
        
        String fullLockKey = "kimprun:lock:" + lockKey;
        
        try {
            RLock lock = redissonClient.getLock(fullLockKey);
            
            // 현재 스레드가 락의 소유자인지 확인
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("분산 락 해제 성공 - 서버: {}, 락키: {}", serverInstanceId, lockKey);
                return true;
            } else {
                log.warn("분산 락 해제 실패 - 서버: {}, 락키: {} (현재 스레드가 소유자가 아님)", 
                    serverInstanceId, lockKey);
                return false;
            }
            
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
    @Override
    public String getLockOwner(String lockKey) {
        String fullLockKey = "kimprun:lock:" + lockKey;
        
        try {
            RLock lock = redissonClient.getLock(fullLockKey);
            
            if (lock.isLocked()) {
                // Redisson에서는 정확한 소유자 정보를 직접 제공하지 않으므로 락이 존재하면 일반적인 정보 반환
                return "locked-by-redisson-instance";
            } else {
                return null;
            }
            
        } catch (Exception e) {
            log.error("락 소유자 확인 중 오류 발생 - 락키: {}", lockKey, e);
            return null;
        }
    }
    
    /**
     * 강제 락 해제
     * 
     * 다른 서버에서 실제로 작업이 진행 중일 수도 있으므로 신중히 사용해야 함
     * 서버 장애나 예상치 못한 종료로 인해 락이 남아있을 때만 사용
     * 
     * @param lockKey 락 키
     * @return 강제 해제 성공 여부
     */
    @Override
    public boolean forceUnlock(String lockKey) {
        String fullLockKey = "kimprun:lock:" + lockKey;
        
        try {
            RLock lock = redissonClient.getLock(fullLockKey);
            
            boolean wasLocked = lock.isLocked();
            if (wasLocked) {
                log.warn("분산 락 강제 해제 시도 - 락키: {}, 해제 요청자: {}", lockKey, serverInstanceId);
            }
            
            // 강제 해제
            boolean success = lock.forceUnlock();
            
            if (success && wasLocked) {
                log.warn("분산 락 강제 해제 완료 - 락키: {}", lockKey);
            } else if (!wasLocked) {
                log.warn("분산 락 강제 해제 시도 - 락키: {} (락이 존재하지 않음)", lockKey);
            }
            
            return success;
            
        } catch (Exception e) {
            log.error("분산 락 강제 해제 중 오류 발생 - 락키: {}", lockKey, e);
            return false;
        }
    }

}