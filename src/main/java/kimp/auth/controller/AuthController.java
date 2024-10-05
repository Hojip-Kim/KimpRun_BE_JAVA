package kimp.auth.controller;

import kimp.auth.dto.CheckAuthResponseDto;
import kimp.auth.service.AuthService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/status")
    public CheckAuthResponseDto checkUserStatus(@AuthenticationPrincipal UserDetails user) {
        if(user == null){
            throw new IllegalArgumentException("userDetails user is null");
        }

        return authService.checkAuthStatus(user);
    }



}
