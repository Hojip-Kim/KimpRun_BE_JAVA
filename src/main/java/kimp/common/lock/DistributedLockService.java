package kimp.common.lock;

/**
 * 분산 환경에서 하나의 인스턴스만 특정 작업을 수행하도록 보장하는 락 서비스
 */
public interface DistributedLockService {
    
    /**
     * 분산 락 획득 시도
     * 
     * @param lockKey 락 키 (예: "batch:cmc-sync", "batch:notice-scraper")
     * @param ttlSeconds 락 유지 시간 (초)
     * @return 락 획득 성공 시 락 토큰, 실패 시 null
     */
    String tryLock(String lockKey, int ttlSeconds);
    
    /**
     * 분산 락 해제
     * 
     * @param lockKey 락 키
     * @param lockValue 락 획득 시 받은 토큰
     * @return 해제 성공 여부
     */
    boolean releaseLock(String lockKey, String lockValue);
    
    /**
     * 락 상태 확인
     * 
     * @param lockKey 락 키
     * @return 현재 락을 보유한 서버 정보, 락이 없으면 null
     */
    String getLockOwner(String lockKey);
    
    /**
     * 분산 락 강제 해제
     * 
     * 다른 서버에서 실제로 작업이 진행 중일 수도 있으므로 신중히 사용해야 함
     * 서버 장애나 예상치 못한 종료로 인해 락이 남아있을 때만 사용
     * 
     * @param lockKey 락 키
     * @return 강제 해제 성공 여부
     */
    boolean forceUnlock(String lockKey);
    
}