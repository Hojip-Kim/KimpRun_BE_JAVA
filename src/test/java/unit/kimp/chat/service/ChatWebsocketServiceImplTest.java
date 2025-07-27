package unit.kimp.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.chat.dao.ChatDao;
import kimp.chat.service.serviceImpl.ChatWebsocketServiceImpl;
import kimp.exception.KimprunException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatWebsocketServiceImplTest {

    @InjectMocks
    private ChatWebsocketServiceImpl chatWebsocketServiceImpl;

    @Mock
    private ChatDao chatDao;


    @Mock
    private ObjectMapper objectMapper;


    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(chatWebsocketServiceImpl).build();
    }


    @Test
    @DisplayName("세션 입력 시 세션 맵에 이미 키가 있으면 에러 발생")
    void shouldThrowExceptionWhenSessionInputWithExistingKey() {
        // given
        WebSocketSession webSocketSession1 = mock(WebSocketSession.class);

        when(webSocketSession1.getId()).thenReturn("session-id-1");

        chatWebsocketServiceImpl.sessionInput(webSocketSession1);

        // when & then
        assertThrows(KimprunException.class, () -> {
            chatWebsocketServiceImpl.sessionInput(webSocketSession1);
        });
    }

    @Test
    @DisplayName("세션 종료 시 세션 맵에 대상 세션 ID가 없으면 에러 발생")
    void shouldThrowExceptionWhenSessionCloseWithNonExistingId() {
        //given
        WebSocketSession webSocketSession1 = mock(WebSocketSession.class);
        when(webSocketSession1.getId()).thenReturn("session-id-1");
        WebSocketSession webSocketSession2 = mock(WebSocketSession.class);
        when(webSocketSession2.getId()).thenReturn("session-id-2");

        //when
        chatWebsocketServiceImpl.sessionInput(webSocketSession1);
        
        //then
        assertThrows(KimprunException.class, () -> {
            chatWebsocketServiceImpl.sessionClose(webSocketSession2);
        });
    }
}
