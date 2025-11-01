package unit.kimp.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.chat.dto.response.ChatMessageResponse;
import kimp.common.redis.constant.RedisChannelType;
import kimp.config.redis.handler.RedisMessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Redis 메시지 브로커 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class RedisMessageBrokerTest {
    
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    
    @Mock
    private ObjectMapper objectMapper;
    
    private RedisMessageHandler redisMessageHandler;
    
    @BeforeEach
    void setUp() {
        redisMessageHandler = new RedisMessageHandler(messagingTemplate, objectMapper);
    }
    
    @Test
    void 메시지_핸들러_초기화_테스트() {
        // given & when & then
        assertNotNull(redisMessageHandler.getInstanceId());
        assertTrue(redisMessageHandler.getInstanceId().startsWith("local-") 
                || redisMessageHandler.getInstanceId().contains("hostname"));
    }
    
    @Test
    void 다른_인스턴스_메시지_처리_테스트() throws Exception {
        // given
        String otherInstanceId = "other-instance-123";
        ChatMessageResponse chatMessage = createTestChatMessage();
        RedisMessageHandler.ChatMessageWrapper wrapper = 
                new RedisMessageHandler.ChatMessageWrapper(otherInstanceId, chatMessage);
        
        String jsonMessage = "{\"instanceId\":\"" + otherInstanceId + "\",\"message\":{\"chatID\":\"test-123\"}}";
        when(objectMapper.readValue(jsonMessage, RedisMessageHandler.ChatMessageWrapper.class))
                .thenReturn(wrapper);
        
        // when
        redisMessageHandler.handleMessage(jsonMessage);
        
        // then
        verify(messagingTemplate).convertAndSend("/topic/chat", chatMessage);
    }
    
    @Test
    void 자체_인스턴스_메시지_무시_테스트() throws Exception {
        // given
        String currentInstanceId = redisMessageHandler.getInstanceId();
        ChatMessageResponse chatMessage = createTestChatMessage();
        RedisMessageHandler.ChatMessageWrapper wrapper = 
                new RedisMessageHandler.ChatMessageWrapper(currentInstanceId, chatMessage);
        
        String jsonMessage = "{\"instanceId\":\"" + currentInstanceId + "\",\"message\":{\"chatID\":\"test-123\"}}";
        when(objectMapper.readValue(jsonMessage, RedisMessageHandler.ChatMessageWrapper.class))
                .thenReturn(wrapper);
        
        // when
        redisMessageHandler.handleMessage(jsonMessage);
        
        // then
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }
    
    @Test
    void 메시지_파싱_실패_처리_테스트() throws Exception {
        // given
        String invalidMessage = "invalid json message";
        when(objectMapper.readValue(invalidMessage, RedisMessageHandler.ChatMessageWrapper.class))
                .thenThrow(new RuntimeException("파싱 실패"));
        
        // when & then - 예외가 발생해도 서비스는 계속되어야 함
        assertDoesNotThrow(() -> redisMessageHandler.handleMessage(invalidMessage));
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }
    
    @Test
    void Redis_발행_테스트() {
        // given
        ChatMessageResponse chatMessage = createTestChatMessage();
        RedisMessageHandler.ChatMessageWrapper wrapper = 
                new RedisMessageHandler.ChatMessageWrapper(redisMessageHandler.getInstanceId(), chatMessage);
        
        // when
        String channel = RedisChannelType.CHAT_MESSAGES.getChannel();
        redisTemplate.convertAndSend(channel, wrapper);

        // then
        verify(redisTemplate).convertAndSend(eq(channel), any());
    }
    
    @Test
    void ChatMessageWrapper_테스트() {
        // given
        String instanceId = "test-instance";
        ChatMessageResponse message = createTestChatMessage();
        
        // when
        RedisMessageHandler.ChatMessageWrapper wrapper = 
                new RedisMessageHandler.ChatMessageWrapper(instanceId, message);
        
        // then
        assertEquals(instanceId, wrapper.getInstanceId());
        assertEquals(message, wrapper.getMessage());
    }
    
    private ChatMessageResponse createTestChatMessage() {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setChatID("test-123");
        response.setContent("테스트 메시지");
        response.setAuthenticated(false);
        response.setIp("127.0.0.1");
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }
}