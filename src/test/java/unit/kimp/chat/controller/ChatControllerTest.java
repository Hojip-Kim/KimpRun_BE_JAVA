package unit.kimp.chat.controller;

import kimp.chat.controller.ChatController;
import kimp.chat.dto.request.ChatLogRequestDto;
import kimp.chat.service.ChatService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @DisplayName("Get /allLog : page < 0이거나 size <= 0이면 BadRequestException 발생")
    void chatLogTransferFailure() throws Exception{
        // given
        List<ChatLogRequestDto> pageAndSizeList = new ArrayList<>();


        // when
        ChatLogRequestDto invalidDto1 = new ChatLogRequestDto(-1, 10); // page < 0
        ChatLogRequestDto invalidDto2 = new ChatLogRequestDto(10, 0);   // size <= 0
        ChatLogRequestDto invalidDto3 = new ChatLogRequestDto(-1, 0);   // page < 0 && size <= 0

        // then
        assertThrows(BadRequestException.class, () -> {
            chatController.getChats(invalidDto1, request);
        });

        assertThrows(BadRequestException.class, () -> {
            chatController.getChats(invalidDto2, request);
        });

        assertThrows(BadRequestException.class, () -> {
            chatController.getChats(invalidDto3, request);
        });

    }

    @Test
    @DisplayName("Get /allLog : page >= 0이거나 size > 0이면 통과")
    void chatLogTransferSuccess() throws Exception {
        // given
        List<ChatLogRequestDto> pageAndSizeList = new ArrayList<>();

        // when
        ChatLogRequestDto validDto1 = new ChatLogRequestDto(0, 10); // page >= 0
        ChatLogRequestDto validDto2 = new ChatLogRequestDto(0, 5);   // size > 1

        // then

        assertDoesNotThrow(()-> {
            chatController.getChats(validDto1, request);
        });

        assertDoesNotThrow(()-> {
            chatController.getChats(validDto2, request);
        });
    }

}
