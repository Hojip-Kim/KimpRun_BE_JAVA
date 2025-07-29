package kimp.chat.controller;

import jakarta.servlet.http.HttpServletRequest;
import kimp.common.dto.PageRequestDto;
import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.service.ChatService;
import kimp.chat.entity.Chat;
import kimp.exception.response.ApiResponse;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/allLog")
    public ApiResponse<List<ChatLogResponseDto>> getChats(@ModelAttribute PageRequestDto requestDto, HttpServletRequest req ) {
        if(requestDto == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "PageRequestDto cannot be null", HttpStatus.BAD_REQUEST, "ChatController.getChats");
        }

        List<Chat> chatLog = chatService.getChatMessages(requestDto.getPage(), requestDto.getSize());
        List<ChatLogResponseDto> result = chatService.convertChatLogToDto(chatLog);
        return ApiResponse.success(result);
    }

}
