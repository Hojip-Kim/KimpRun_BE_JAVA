package kimp.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kimp.auth.dto.response.AuthResponseDto;
import kimp.auth.dto.response.LoginMemberResponseDto;
import kimp.auth.dto.response.UnLoginMemberResponseDto;
import kimp.auth.service.AuthService;
import kimp.auth.vo.CheckAuthStatusVo;
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
                return ApiResponse.success(UnLoginMemberResponseDto.builder()
                        .uuid(kimprunToken)
                        .build());
            }
        }
        
        CustomUserDetails customUserDetails = (CustomUserDetails) member;
        CheckAuthStatusVo vo = new CheckAuthStatusVo(customUserDetails.getId());
        LoginMemberResponseDto result = authService.checkAuthStatus(vo);
        result.setUuid(kimprunToken);
        return ApiResponse.success(result);
    }
}
