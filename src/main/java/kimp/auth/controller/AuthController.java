package kimp.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kimp.auth.dto.CheckAuthResponseDto;
import kimp.auth.service.AuthService;

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
    public CheckAuthResponseDto checkmemberStatus(@AuthenticationPrincipal UserDetails member, HttpServletRequest request, HttpServletResponse response) {

        if(member == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) member;

            return authService.checkAuthStatus(customUserDetails);
    }
}
