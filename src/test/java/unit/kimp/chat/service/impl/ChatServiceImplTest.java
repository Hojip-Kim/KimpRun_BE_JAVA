package unit.kimp.chat.service.impl;

import kimp.chat.dao.impl.ChatDaoImpl;
import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.entity.Chat;
import kimp.chat.repository.ChatRepository;
import kimp.chat.service.ChatTrackingService;
import kimp.chat.service.serviceImpl.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceImplTest {

    @Mock
    private ChatDaoImpl chatDao;
    
    @Mock
    private ChatRepository chatRepository;
    
    @Mock
    private ChatTrackingService chatTrackingService;

    @InjectMocks
    private ChatServiceImpl chatService;

    private Chat chat1;
    private Chat chat2;
    private Page<Chat> chatList;

    @BeforeEach
    void setUp() throws Exception {
        chat1 = new Chat("user1", "Hello, world!", true);
        setPrivateField(chat1, "userIp", "192.168.1.1");
        setPrivateField(chat1, "cookiePayload", "cookie1");
        setPrivateField(chat1, "userId", 1L);
        setPrivateField(chat1, "registedAt", java.time.LocalDateTime.now().minusMinutes(10));
        
        chat2 = new Chat("user2", "Hi there!", false);
        setPrivateField(chat2, "userIp", "192.168.1.2");
        setPrivateField(chat2, "cookiePayload", "cookie2");
        setPrivateField(chat2, "registedAt", java.time.LocalDateTime.now().minusMinutes(5));
        
        chatList = new PageImpl<>(Arrays.asList(chat1, chat2));
    }
    
    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @DisplayName("채팅 메시지 조회")
    void shouldReturnChatMessages() {
        // Arrange
        when(chatDao.getAllChats(anyInt(), anyInt())).thenReturn(chatList);
        when(chatTrackingService.getNicknamesByMemberIds(any())).thenReturn(Map.of(1L, "user1"));
        when(chatTrackingService.getNicknamesByUuids(any())).thenReturn(Map.of("cookie2", "user2"));

        // Act
        Page<ChatLogResponseDto> result = chatService.getChatMessages(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getNumberOfElements());
        verify(chatDao, times(1)).getAllChats(0, 10);
    }

    @Test
    @DisplayName("채팅 메시지 조회 실패: 메시지 없음")
    void shouldThrowExceptionWhenNoChatMessagesFound() {
        // Arrange
        when(chatDao.getAllChats(anyInt(), anyInt())).thenThrow(new IllegalArgumentException("Not found any chats"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> chatService.getChatMessages(0, 10));
        verify(chatDao, times(1)).getAllChats(0, 10);
    }

    @Test
    @DisplayName("채팅 메시지 페이지 정렬 확인")
    void shouldReturnChatMessagesInAscendingOrder() {
        // Arrange
        when(chatDao.getAllChats(anyInt(), anyInt())).thenReturn(chatList);
        when(chatTrackingService.getNicknamesByMemberIds(any())).thenReturn(Map.of(1L, "user1"));
        when(chatTrackingService.getNicknamesByUuids(any())).thenReturn(Map.of("cookie2", "user2"));

        // Act
        Page<ChatLogResponseDto> result = chatService.getChatMessages(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getNumberOfElements());
        
        // Check if messages are sorted by time (ascending)
        List<ChatLogResponseDto> content = result.getContent();
        assertEquals("user1", content.get(0).getChatId());
        assertEquals("user2", content.get(1).getChatId());
        
        verify(chatDao, times(1)).getAllChats(0, 10);
    }
}