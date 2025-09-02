package kimp.websocket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class WebSocketUserTracker {
    
    private final ConcurrentHashMap<String, String> sessionRegistry = new ConcurrentHashMap<>();
    private final AtomicInteger userCount = new AtomicInteger(0);
    
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        if (sessionId != null && !sessionRegistry.containsKey(sessionId)) {
            sessionRegistry.put(sessionId, sessionId);
            int currentCount = userCount.incrementAndGet();
            log.info("WebSocket 사용자 연결: {} (총 사용자 수: {})", sessionId, currentCount);
        }
    }
    
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        if (sessionId != null && sessionRegistry.remove(sessionId) != null) {
            int currentCount = userCount.decrementAndGet();
            log.info("WebSocket 사용자 연결 해제: {} (총 사용자 수: {})", sessionId, currentCount);
        }
    }
    
    public int getUserCount() {
        return userCount.get();
    }
    
    public int getActiveSessionCount() {
        return sessionRegistry.size();
    }
}