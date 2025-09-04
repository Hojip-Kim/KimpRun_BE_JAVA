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
import java.util.UUID;

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
        boolean needNewToken = false;
        
        if(tokenValue != null){
            CookiePayload cookiePayload = cookieVerifier.verify(tokenValue, cookieSecret);
            // Cookie verification failed - need new token
            if(cookiePayload == null){
                needNewToken = true;
            }
        } else {
            // No token exists - need new token
            needNewToken = true;
        }
        
        // Generate new UUID token if needed
        if(needNewToken) {
            String newTokenId = UUID.randomUUID().toString();
            String signedToken = cookieVerifier.createSignedCookie(newTokenId, cookieSecret);
            
            ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from("kimprun-token", signedToken)
                    .path("/")
                    .httpOnly(true)
                    .maxAge(60 * 60 * 24 * 365); // 1 year
            
            if(cookieDomain != null && !cookieDomain.isBlank()) {
                cookieBuilder.domain(cookieDomain).secure(true);
            } else {
                cookieBuilder.secure(false);
            }
            
            response.addHeader(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());
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
