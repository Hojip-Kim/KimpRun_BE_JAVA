package unit.kimp.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.chat.dao.daoImpl.ChatDao;
import kimp.chat.service.serviceImpl.ChatWebsocketServiceImpl;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @DisplayName("session input을 할 때 session Map에 이미 key가 있으면 에러가 터진다.")
    void sessionInputTest() throws Exception{
        // given
       WebSocketSession webSocketSession1 = mock(WebSocketSession.class);

       when(webSocketSession1.getId()).thenReturn("session-id-1");

       chatWebsocketServiceImpl.sessionInput(webSocketSession1);

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chatWebsocketServiceImpl.sessionInput(webSocketSession1);
        });

        // then
        assertEquals("Already exists in Session Map : ID - session-id-1", exception.getMessage());

    }

    @Test
    @DisplayName("session Close를 할 때 session Map에 대상 session id가 없으면 에러가 터진다.")
    public void sessionCloseTest(){
        //given

        WebSocketSession webSocketSession1 = mock(WebSocketSession.class);
        when(webSocketSession1.getId()).thenReturn("session-id-1");
        WebSocketSession webSocketSession2 = mock(WebSocketSession.class);
        when(webSocketSession2.getId()).thenReturn("session-id-2");

        //when

        chatWebsocketServiceImpl.sessionInput(webSocketSession1);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chatWebsocketServiceImpl.sessionClose(webSocketSession2);
        });

        //then
        assertEquals("Session Map에 " + webSocketSession2.getId() + " ID 가 없습니다.", exception.getMessage());
    }
}
