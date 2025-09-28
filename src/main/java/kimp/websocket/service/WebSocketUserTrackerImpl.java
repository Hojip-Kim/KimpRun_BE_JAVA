package kimp.websocket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket 사용자 연결 추적 서비스 구현체
 */
@Component
@Slf4j
public class WebSocketUserTrackerImpl implements WebSocketUserTracker {
    
    private final ConcurrentHashMap<String, String> sessionRegistry = new ConcurrentHashMap<>();
    private final AtomicInteger userCount = new AtomicInteger(0);
    
    /**
     * WebSocket 연결 이벤트 처리
     * @param event 연결 이벤트
     */
    @Override
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        if (sessionId != null && !sessionRegistry.containsKey(sessionId)) {
            sessionRegistry.put(sessionId, sessionId);
            userCount.incrementAndGet();
        }
    }
    
    /**
     * WebSocket 연결 해제 이벤트 처리
     * @param event 연결 해제 이벤트
     */
    @Override
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        if (sessionId != null && sessionRegistry.remove(sessionId) != null) {
            userCount.decrementAndGet();
        }
    }
    
    /**
     * 현재 연결된 사용자 수 조회
     * @return 현재 연결된 사용자 수
     */
    @Override
    public int getUserCount() {
        return userCount.get();
    }
    
    /**
     * 현재 활성 세션 수 조회
     * @return 현재 활성 세션 수
     */
    @Override
    public int getActiveSessionCount() {
        return sessionRegistry.size();
    }
}