package unit.kimp.chat.service.impl;

import kimp.chat.dao.ChatDao;
import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.entity.Chat;
import kimp.chat.service.serviceImpl.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceImplTest {

    @Mock
    private ChatDao chatDao;

    @InjectMocks
    private ChatServiceImpl chatService;

    private Chat chat1;
    private Chat chat2;
    private List<Chat> chatList;

    @BeforeEach
    void setUp() {
        // Setup test data
        chat1 = new Chat("user1", "Hello, world!", "true");
        chat2 = new Chat("user2", "Hi there!", "false");
        chatList = Arrays.asList(chat1, chat2);
    }

    @Test
    @DisplayName("채팅 메시지 조회")
    void shouldReturnChatMessages() {
        // Arrange
        when(chatDao.getAllChats(anyInt(), anyInt())).thenReturn(chatList);

        // Act
        List<Chat> result = chatService.getChatMessages(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(chatList, result);
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
    @DisplayName("채팅 로그 DTO 변환")
    void shouldConvertChatLogsToDto() {
        // Act
        List<ChatLogResponseDto> result = chatService.convertChatLogToDto(chatList);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verify first DTO
        assertEquals("user1", result.get(0).getChatID());
        assertEquals("Hello, world!", result.get(0).getContent());
        assertEquals("true", result.get(0).getAuthenticated());
        
        // Verify second DTO
        assertEquals("user2", result.get(1).getChatID());
        assertEquals("Hi there!", result.get(1).getContent());
        assertEquals("false", result.get(1).getAuthenticated());
    }

    @Test
    @DisplayName("채팅 로그 DTO 변환: 입력 없음")
    void shouldReturnEmptyListWhenInputIsEmpty() {
        // Act
        List<ChatLogResponseDto> result = chatService.convertChatLogToDto(new ArrayList<>());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}