package kimp.config.redis.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.chat.dto.response.ChatMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis Pub/Sub 메시지 핸들러
 * 
 * 다른 서버 인스턴스에서 발행한 채팅 메시지를 수신하여
 * 현재 서버에 연결된 WebSocket 클라이언트들에게 전달
 */
@Component
@Slf4j
public class RedisMessageHandler implements MessageListener {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    
    // 현재 서버 인스턴스 ID (중복 처리 방지용)
    private final String instanceId = System.getenv("HOSTNAME") != null ? 
            System.getenv("HOSTNAME") : "local-" + System.currentTimeMillis();
    
    public RedisMessageHandler(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
        log.info("RedisMessageHandler 초기화 - 인스턴스 ID: {}", instanceId);
    }
    
    /**
     * Redis에서 메시지 수신 시 호출
     * JSON 형태의 메시지를 파싱하여 WebSocket으로 전달
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody());
            log.debug("Redis 메시지 수신: {}", messageBody);
            
            // JSON을 ChatMessageWrapper로 역직렬화
            ChatMessageWrapper wrapper = objectMapper.readValue(messageBody, ChatMessageWrapper.class);
            
            // 자기 자신이 발행한 메시지는 무시 (중복 방지)
            if (instanceId.equals(wrapper.getInstanceId())) {
                log.debug("자체 발행 메시지 무시 - Instance ID: {}", instanceId);
                return;
            }
            
            // WebSocket으로 메시지 브로드캐스트
            messagingTemplate.convertAndSend("/topic/chat", wrapper.getMessage());
            log.info("Redis 메시지 브로드캐스트 완료 - 발신 서버: {}, 채팅ID: {}", 
                    wrapper.getInstanceId(), wrapper.getMessage().getChatID());
            
        } catch (Exception e) {
            log.error("Redis 메시지 처리 실패", e);
        }
    }
    
    /**
     * 메시지 처리 메서드 (MessageListenerAdapter용)
     */
    public void handleMessage(String message) {
        try {
            log.debug("handleMessage 호출 - 메시지: {}", message);
            
            // JSON을 ChatMessageWrapper로 역직렬화
            ChatMessageWrapper wrapper = objectMapper.readValue(message, ChatMessageWrapper.class);
            
            // 자기 자신이 발행한 메시지는 무시
            if (instanceId.equals(wrapper.getInstanceId())) {
                log.debug("자체 발행 메시지 무시 - Instance ID: {}", instanceId);
                return;
            }
            
            // WebSocket으로 메시지 브로드캐스트
            messagingTemplate.convertAndSend("/topic/chat", wrapper.getMessage());
            log.info("Redis 메시지 브로드캐스트 완료 - 발신 서버: {}, 채팅ID: {}", 
                    wrapper.getInstanceId(), wrapper.getMessage().getChatID());
            
        } catch (Exception e) {
            log.error("메시지 처리 실패", e);
        }
    }
    
    /**
     * 현재 인스턴스 ID 반환
     */
    public String getInstanceId() {
        return instanceId;
    }
    
    /**
     * 채팅 메시지 래퍼 클래스
     * 인스턴스 ID와 함께 메시지를 전달하여 중복 처리 방지
     */
    public static class ChatMessageWrapper {
        private String instanceId;
        private ChatMessageResponse message;
        
        // 기본 생성자
        public ChatMessageWrapper() {
        }
        
        public ChatMessageWrapper(String instanceId, ChatMessageResponse message) {
            this.instanceId = instanceId;
            this.message = message;
        }
        
        public String getInstanceId() {
            return instanceId;
        }
        
        public void setInstanceId(String instanceId) {
            this.instanceId = instanceId;
        }
        
        public ChatMessageResponse getMessage() {
            return message;
        }
        
        public void setMessage(ChatMessageResponse message) {
            this.message = message;
        }
    }
}