package kimp.websocket.service;

import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * WebSocket 사용자 연결 추적 서비스 인터페이스
 */
public interface WebSocketUserTracker {
    
    /**
     * WebSocket 연결 이벤트 처리
     * 
     * @param event 연결 이벤트
     */
    void handleWebSocketConnectListener(SessionConnectEvent event);
    
    /**
     * WebSocket 연결 해제 이벤트 처리
     * 
     * @param event 연결 해제 이벤트
     */
    void handleWebSocketDisconnectListener(SessionDisconnectEvent event);
    
    /**
     * 현재 연결된 사용자 수 조회
     * 
     * @return 현재 연결된 사용자 수
     */
    int getUserCount();
    
    /**
     * 현재 활성 세션 수 조회
     * 
     * @return 현재 활성 세션 수
     */
    int getActiveSessionCount();
}