package kimp.websocket.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    private static final String KIMPRUN_TOKEN_COOKIE = "kimprun-token";
    private static final String SESSION_KIMPRUN_TOKEN = "kimprun-token";
    private static final String SESSION_CLIENT_IP = "clientIp";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        log.debug("WebSocket handshake started");

        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            
            // 1. 쿠키에서 kimprun-token 추출
            String kimprunToken = extractKimprunTokenFromCookies(servletRequest);
            if (kimprunToken != null) {
                attributes.put(SESSION_KIMPRUN_TOKEN, kimprunToken);
                log.debug("Kimprun token extracted from cookie: {}", maskToken(kimprunToken));
            } else {
                log.debug("No kimprun-token found in cookies");
            }

            // 2. 클라이언트 IP 추출
            String clientIp = extractClientIp(servletRequest);
            attributes.put(SESSION_CLIENT_IP, clientIp);
            log.debug("Client IP extracted: {}", clientIp);

            log.info("WebSocket handshake completed - IP: {}, Token present: {}", 
                clientIp, kimprunToken != null);
        }

        return true; // 핸드셰이크 계속 진행
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake failed", exception);
        } else {
            log.debug("WebSocket handshake completed successfully");
        }
    }

    /**
     * 쿠키에서 kimprun-token 추출
     */
    private String extractKimprunTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (KIMPRUN_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 클라이언트 실제 IP 추출 (프록시, 로드밸런서 고려)
     */
    private String extractClientIp(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP", 
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };

        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For의 경우 여러 IP가 콤마로 구분될 수 있음
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        // 헤더에서 찾지 못한 경우 기본 remote address 사용
        return request.getRemoteAddr();
    }

    /**
     * 로깅용 토큰 마스킹
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "***";
        }
        return token.substring(0, 4) + "***" + token.substring(token.length() - 4);
    }
}