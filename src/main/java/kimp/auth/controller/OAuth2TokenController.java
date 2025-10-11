package kimp.auth.controller;

import kimp.auth.dto.response.OAuth2TokenStatusDto;
import kimp.auth.service.OAuth2TokenRefreshService;
import kimp.auth.vo.GetTokenStatusVo;
import kimp.auth.vo.RefreshMemberTokenVo;
import kimp.exception.response.ApiResponse;
import kimp.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/oauth2")
@RequiredArgsConstructor
@Slf4j
public class OAuth2TokenController {

    private final OAuth2TokenRefreshService tokenRefreshService;

    @PostMapping("/refresh")
    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR', 'INFLUENCER', 'USER')")
    public ResponseEntity<ApiResponse<String>> refreshToken(@AuthenticationPrincipal UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        
        try {
            RefreshMemberTokenVo vo = new RefreshMemberTokenVo(customUserDetails.getId());
            String result = tokenRefreshService.refreshMemberToken(vo);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("수동 토큰 갱신 실패 - Member ID: {}", customUserDetails.getId(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "TOKEN_REFRESH_FAILED", "토큰 갱신에 실패했습니다."));
        }
    }

    @GetMapping("/token/status")
    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR', 'INFLUENCER', 'USER')")
    public ResponseEntity<ApiResponse<OAuth2TokenStatusDto>> getTokenStatus(@AuthenticationPrincipal UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        
        GetTokenStatusVo vo = new GetTokenStatusVo(customUserDetails.getId());
        OAuth2TokenStatusDto tokenStatus = tokenRefreshService.getTokenStatus(vo);
        return ResponseEntity.ok(ApiResponse.success(tokenStatus));
    }
}