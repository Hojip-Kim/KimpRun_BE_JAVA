package kimp.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kimp.auth.dto.AuthResponseDto;
import kimp.auth.dto.LoginMemberResponseDto;
import kimp.auth.dto.UnLoginMemberResponseDto;
import kimp.auth.service.AuthService;
import kimp.exception.response.ApiResponse;
import kimp.security.user.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/status")
    public ApiResponse<AuthResponseDto> checkMemberStatus(@AuthenticationPrincipal UserDetails member, HttpServletRequest request, HttpServletResponse response) {
        String kimprunToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("kimprun-token")) {
                    kimprunToken = cookie.getValue();
                }
            }
            if (member == null) {
                return ApiResponse.success(new UnLoginMemberResponseDto(kimprunToken));
            }
        }
        
        CustomUserDetails customUserDetails = (CustomUserDetails) member;
        LoginMemberResponseDto result = authService.checkAuthStatus(customUserDetails.getId());
        result.setUuid(kimprunToken);
        return ApiResponse.success(result);
    }
}
