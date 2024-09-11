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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    @InjectMocks
    private ChatController chatController;

    @MockBean
    private ChatService chatService;

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
        ChatLogRequestDto invalidDto2 = new ChatLogRequestDto(0, 0);   // size <= 0

        // then
        assertThrows(BadRequestException.class, () -> {
            chatController.getChats(invalidDto1, null);
        });

        assertThrows(BadRequestException.class, () -> {
            chatController.getChats(invalidDto2, null);
        });

    }

    private ChatLogRequestDto chatLogRequest(int page, int size){
        return new ChatLogRequestDto(page, size);
    }

}
