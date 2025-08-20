package kimp.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kimp.exception.response.ApiResponse;
import kimp.security.cookie.CookieVerifier;
import kimp.security.user.dto.CookiePayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AnonymousCookieGuardFilter extends OncePerRequestFilter {

    private final CookieVerifier cookieVerifier;
    private final ObjectMapper objectMapper;

    @Value("${app.cookie.domain}")
    public String cookieDomain;
    @Value("${app.cookie.secret}")
    public String cookieSecret;

    public AnonymousCookieGuardFilter(CookieVerifier cookieVerifier, ObjectMapper objectMapper) {
        this.cookieVerifier = cookieVerifier;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = readCookie(request, "kimprun-token");
        if(tokenValue != null){
            CookiePayload cookiePayload = cookieVerifier.verify(tokenValue, cookieSecret);

            // secret을 톨한 cookie verify가 실패할 경우
            if(cookiePayload == null){
                ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from("kimprun-token", tokenValue)
                        .path("/")
                        .httpOnly(true)
                        .secure(true)
                        .maxAge(0);
                if(cookieDomain != null && !cookieDomain.isBlank()) {
                    cookieBuilder.secure(true);
                }else{
                    cookieBuilder.secure(false);
                }
                response.addHeader(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType("application/json;charset=UTF-8");
                response.setHeader("Cache-Control", "no-store");

                ApiResponse responseBody = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "INVALID_COOKIE", "Signature verification failed.");
                objectMapper.writeValue(response.getWriter(), responseBody);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String readCookie(HttpServletRequest request, String cookieName) {
        if(request.getCookies() == null) {
            return null;
        }else {
            for(Cookie cookie : request.getCookies()) {
                if(cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
            // cookieName에 해당하는 cookie를 찾지못하면 null
            return null;
        }
    }


}
