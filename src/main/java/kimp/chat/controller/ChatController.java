package kimp.chat.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kimp.chat.dto.request.DeleteAuthChatRequest;
import kimp.chat.dto.vo.DeleteAnonChatMessage;
import kimp.common.dto.PageRequestDto;
import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.service.ChatService;
import kimp.exception.response.ApiResponse;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.security.user.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/allLog")
    public ApiResponse<Page<ChatLogResponseDto>> getChats(@ModelAttribute PageRequestDto requestDto, HttpServletRequest req ) {
        if(requestDto == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "PageRequestDto cannot be null", HttpStatus.BAD_REQUEST, "ChatController.getChats");
        }

        List<String> blockedMembers = null;
        List<String> blockedGuests = null;
        
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("blockedMembers".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
                    String decoded = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    if (decoded.startsWith("[") && decoded.endsWith("]")) {
                        decoded = decoded.substring(1, decoded.length() - 1).replaceAll("\"", "");
                    }
                    blockedMembers = Arrays.asList(decoded.split(","));
                } else if ("blockedGuests".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
                    String decoded = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    if (decoded.startsWith("[") && decoded.endsWith("]")) {
                        decoded = decoded.substring(1, decoded.length() - 1).replaceAll("\"", "");
                    }
                    blockedGuests = Arrays.asList(decoded.split(","));
                }
            }
        }

        Page<ChatLogResponseDto> chatLog;
        if (blockedMembers != null || blockedGuests != null) {
            chatLog = chatService.getChatMessagesWithBlocked(requestDto.getPage(), requestDto.getSize(), blockedMembers, blockedGuests);
        } else {
            chatLog = chatService.getChatMessages(requestDto.getPage(), requestDto.getSize());
        }
        
        return ApiResponse.success(chatLog);
    }


    @DeleteMapping("/anon")
    public ApiResponse<Void> deleteAnonChat(HttpServletRequest req, @RequestBody DeleteAnonChatMessage requestDto) {
        if(requestDto == null || requestDto.getInherenceId() == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION, "DeleteAnonChatMessage cannot be null", HttpStatus.BAD_REQUEST, "ChatController.deleteAnonChat");
        }

        String kimprunToken = null;

        if(req.getCookies() != null) {
            for(Cookie cookie : req.getCookies()) {
                if(cookie.getName().equals("kimprun-token")) {
                    kimprunToken = cookie.getValue();
                    break;
                }
            }
        }
        if(kimprunToken == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION, "KimprunToken cannot be null", HttpStatus.BAD_REQUEST, "ChatController.deleteAnonChat");
        }

        chatService.softDeleteAnonMessage(kimprunToken,requestDto);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/auth")
    public ApiResponse<Void> deleteAuthChat(@AuthenticationPrincipal UserDetails userDetails, @RequestBody DeleteAuthChatRequest requestDto) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        if(requestDto == null || requestDto.getInherenceId() == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION, "DeleteAuthChatMessage cannot be null", HttpStatus.BAD_REQUEST, "ChatController.deleteAuthChat");
        }

        chatService.softDeleteAuthMessage(customUserDetails.getId(), requestDto);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasAuthority('OPERATOR')")
    @DeleteMapping("/admin")
    public ApiResponse<Void> deleteAdminChat(@AuthenticationPrincipal UserDetails userDetails, @RequestBody DeleteAuthChatRequest requestDto) {
        if(requestDto == null || requestDto.getInherenceId() == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION, "DeleteAuthChatMessage cannot be null", HttpStatus.BAD_REQUEST, "ChatController.deleteAuthChat");
        }
        chatService.softDeleteAdminRole(requestDto);
        return ApiResponse.success(null);
    }

}
