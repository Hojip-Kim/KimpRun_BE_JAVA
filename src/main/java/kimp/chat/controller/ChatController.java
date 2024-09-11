package kimp.chat.controller;

import jakarta.servlet.http.HttpServletRequest;
import kimp.chat.dto.request.ChatLogRequestDto;
import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.service.ChatService;
import kimp.chat.entity.Chat;
import org.apache.coyote.BadRequestException;
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
    public List<ChatLogResponseDto> getChats(@ModelAttribute ChatLogRequestDto requestDto, HttpServletRequest req ) throws BadRequestException {
        if(requestDto.getPage() < 0 || requestDto.getSize() <= 0) {
            throw new BadRequestException();
        }

        return chatService.getChatMessages(requestDto.getPage(), requestDto.getSize());
    }

    @GetMapping("/test")
    public Chat createChat(HttpServletRequest httpServletRequest){
        String ipAddress = httpServletRequest.getHeader("X-Forwarded-For");
        if(ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)){
            ipAddress = httpServletRequest.getRemoteAddr();
        }

        return chatService.createChat("이름1", "내용1");
    }
}
