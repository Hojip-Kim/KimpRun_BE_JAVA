package kimp.cmc.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class CmcApiRateLimiter {
    
    private static final int MAX_REQUESTS_PER_MINUTE = 30;
    private static final long MINUTE_IN_MILLIS = 60 * 1000L;
    
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());
    
    /**
     * API 호출 전에 rate limit 체크 및 대기
     */
    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        long windowStartTime = windowStart.get();
        
        // 1분이 지났으면 윈도우 리셋
        if (currentTime - windowStartTime >= MINUTE_IN_MILLIS) {
            windowStart.set(currentTime);
            requestCount.set(0);
            log.debug("Rate Limit 윈도우 리셋: {}", currentTime);
        }
        
        int currentCount = requestCount.get();
        
        // 한도 초과 시 대기
        if (currentCount >= MAX_REQUESTS_PER_MINUTE) {
            long waitTime = MINUTE_IN_MILLIS - (currentTime - windowStartTime);
            if (waitTime > 0) {
                log.warn("Rate Limit 초과됨. {} ms 기다리는 중", waitTime);
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Rate Limiter 방해됨.", e);
                    return false;
                }
                // 대기 후 윈도우 리셋
                windowStart.set(System.currentTimeMillis());
                requestCount.set(0);
            }
        }
        
        // 요청 카운트 증가
        int newCount = requestCount.incrementAndGet();
        log.debug("Api Request 증가. 카운트: {}/{}", newCount, MAX_REQUESTS_PER_MINUTE);
        return true;
    }
    
    /**
     * 현재 요청 카운트 조회
     */
    public int getCurrentCount() {
        return requestCount.get();
    }
    
    /**
     * 다음 작업까지 남은 시간 조회 (밀리초)
     */
    public long getTimeUntilNextWindow() {
        long currentTime = System.currentTimeMillis();
        long windowStartTime = windowStart.get();
        long elapsed = currentTime - windowStartTime;
        return Math.max(0, MINUTE_IN_MILLIS - elapsed);
    }
}