package unit.kimp.chat.controller;

import kimp.chat.controller.ChatController;
import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.entity.Chat;
import kimp.chat.service.ChatService;
import kimp.common.dto.PageRequestDto;
import kimp.exception.KimprunException;
import kimp.exception.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    @InjectMocks
    private ChatController chatController;

    @Mock
    private ChatService chatService;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }

    @Test
    @DisplayName("채팅 로그 조회 실패: 유효하지 않은 DTO")
    void shouldThrowExceptionWhenGetChatsWithInvalidDto() {
        // given
        PageRequestDto invalidDto = null;

        // when, then
        assertThrows(KimprunException.class, () -> chatController.getChats(invalidDto, request));
    }

    @Test
    @DisplayName("채팅 로그 조회 성공")
    void shouldReturnChatLogsWhenRequestIsValid() {
        // given
        Page<ChatLogResponseDto> mockChatList = new PageImpl<>(new ArrayList<>());
        List<ChatLogResponseDto> mockResponseList = new ArrayList<>();

        when(chatService.getChatMessages(any())).thenReturn(mockChatList);

        // when
        PageRequestDto validDto = new PageRequestDto(0, 10);
        ApiResponse<Page<ChatLogResponseDto>> response = chatController.getChats(validDto, request);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
    }

}
