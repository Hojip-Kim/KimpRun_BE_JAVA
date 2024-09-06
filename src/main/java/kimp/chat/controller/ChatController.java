package kimp.chat.controller;

import jakarta.servlet.http.HttpServletRequest;
import kimp.chat.service.ChatService;
import kimp.chat.entity.Chat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/chat")

public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/test")
    public Chat createChat(HttpServletRequest httpServletRequest){
        String ipAddress = httpServletRequest.getHeader("X-Forwarded-For");
        if(ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)){
            ipAddress = httpServletRequest.getRemoteAddr();
        }

        System.out.println(ipAddress);

        return chatService.createChat("이름1", "내용1");
    }
}
