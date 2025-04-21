package unit.kimp.unit.kimp.chat.controller;

import kimp.chat.controller.ChatController;
import kimp.chat.service.ChatService;
import kimp.common.dto.PageRequestDto;
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
    @DisplayName("PageRequestDto 생성 시 유효하지 않은 page 또는 size 값으로 IllegalArgumentException 발생")
    void pageRequestDtoInvalidTest() {
        // page < 0인 경우
        assertThrows(IllegalArgumentException.class, () -> {
            new PageRequestDto(-1, 10);
        });

        // size <= 0인 경우
        assertThrows(IllegalArgumentException.class, () -> {
            new PageRequestDto(0, 0);
        });

        // page < 0이고 size <= 0인 경우
        assertThrows(IllegalArgumentException.class, () -> {
            new PageRequestDto(-1, 0);
        });
    }

    @Test
    @DisplayName("Get /allLog: 유효하지 않은 page 또는 size 값으로 BadRequestException 발생")
    void chatLogTransferFailure() throws Exception {
        // given
        PageRequestDto invalidDto1 = null;

        // when, then
        assertThrows(BadRequestException.class, () -> {
            chatController.getChats(invalidDto1, request);
        });

    }

    @Test
    @DisplayName("Get /allLog : page >= 0이거나 size > 0이면 통과")
    void chatLogTransferSuccess() throws Exception {
        // given
        List<PageRequestDto> pageAndSizeList = new ArrayList<>();

        // when
        PageRequestDto validDto1 = new PageRequestDto(0, 10); // page >= 0
        PageRequestDto validDto2 = new PageRequestDto(0, 5);   // size > 1

        // then

        assertDoesNotThrow(()-> {
            chatController.getChats(validDto1, request);
        });

        assertDoesNotThrow(()-> {
            chatController.getChats(validDto2, request);
        });
    }

}
