package kimp.security.controller;

import kimp.exception.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/csrf")
@Slf4j
public class CsrfController {

    @GetMapping("/token")
    public ApiResponse<CsrfToken> getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        // 캐시 방지 헤더 설정
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        // Spring Security가 자동으로 CsrfToken을 request attribute에 추가
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        
        if (csrfToken != null) {
            log.info("🔐 CSRF 토큰 발급: headerName={}, parameterName={}, tokenLength={}, token={}", 
                    csrfToken.getHeaderName(), 
                    csrfToken.getParameterName(), 
                    csrfToken.getToken() != null ? csrfToken.getToken().length() : 0,
                    csrfToken.getToken() != null ? csrfToken.getToken().substring(0, Math.min(20, csrfToken.getToken().length())) + "..." : "null");
            
            log.info("🍪 Spring Security가 자동으로 XSRF-TOKEN 쿠키 설정 (Path=/, JavaScript 접근 가능)");
        } else {
            log.warn("⚠️ CSRF 토큰이 null입니다.");
        }
        
        return ApiResponse.success(csrfToken);
    }
}