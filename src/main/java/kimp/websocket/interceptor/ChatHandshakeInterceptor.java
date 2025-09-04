package kimp.websocket.interceptor;

import kimp.chat.service.ChatTrackingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    private final ChatTrackingService chatTrackingService;

    public ChatHandshakeInterceptor(ChatTrackingService chatTrackingService) {
        this.chatTrackingService = chatTrackingService;
    }

    private static final String KIMPRUN_TOKEN_COOKIE = "kimprun-token";
    private static final String SESSION_KIMPRUN_TOKEN = "kimprun-token";
    private static final String SESSION_ANON_NICKNAME = "nickname";
    private static final String ANON_NICKNAME = "nickname";
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

            // 3. anon nickname 추출 및 URL 디코딩
            String anonNickname = extractNicknameFromCookies(servletRequest);
            if (anonNickname != null) {
                try {
                    anonNickname = URLDecoder.decode(anonNickname, StandardCharsets.UTF_8);
                    attributes.put(SESSION_ANON_NICKNAME, anonNickname);
                    log.debug("anon-nickname extracted and decoded from cookie: {}", anonNickname);
                } catch (Exception e) {
                    log.warn("Failed to decode nickname from cookie: {}", anonNickname, e);
                    anonNickname = null;
                }
            } else {
                log.debug("No anon-nickname found in cookies");
            }

            // 4. Spring Security 세션 인증 상태 확인
            boolean isLoggedIn = isAuthenticated(servletRequest);
            log.debug("User authentication status: {}", isLoggedIn);

            // 5. 비로그인 사용자의 ChatTracking 처리
            if (!isLoggedIn && anonNickname != null && kimprunToken != null) {
                handleAnonUserChatTracking(kimprunToken, anonNickname);
            }

            log.info("WebSocket handshake completed - IP: {}, Token present: {}, anon nickname: {}, authenticated: {}",
                clientIp, kimprunToken != null, anonNickname, isLoggedIn);
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
     * 쿠키에서 nickname 추출
     */
    private String extractNicknameFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ANON_NICKNAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Spring Security 세션을 통한 인증 상태 확인
     */
    private boolean isAuthenticated(HttpServletRequest request) {
        try {
            // HTTP 세션에서 Spring Security Context 확인
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object securityContext = session.getAttribute("SPRING_SECURITY_CONTEXT");
                if (securityContext != null) {
                    // SecurityContextHolder에서 현재 인증 상태 확인
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    return authentication != null && authentication.isAuthenticated() 
                           && !"anonymousUser".equals(authentication.getPrincipal());
                }
            }
            return false;
        } catch (Exception e) {
            log.debug("Failed to check authentication status", e);
            return false;
        }
    }

    /**
     * 사용자 UUID 생성 또는 기존 UUID 반환
     */
    private String generateOrGetUserUuid(Map<String, Object> attributes) {
        String existingUuid = (String) attributes.get("userUuid");
        if (existingUuid != null) {
            return existingUuid;
        }
        return UUID.randomUUID().toString();
    }

    /**
     * 비로그인 사용자의 ChatTracking 처리
     */
    private void handleAnonUserChatTracking(String uuid, String nickname) {
        try {
            // authenticated=false인 ChatTracking 확인
            log.info("kimprun-token : {}", uuid);
            String existingNickname = chatTrackingService.getNicknameByUuidAndAuthenticated(uuid, false);
            log.info("found kimprun-token : {}", existingNickname);
            
            if (existingNickname != null) {
                // 기존 ChatTracking이 있고 닉네임이 다른 경우에만 업데이트
                if (!existingNickname.equals(nickname)) {
                    log.debug("Updating anon user ChatTracking - UUID: {}, old nickname: {}, new nickname: {}", 
                             uuid, existingNickname, nickname);
                    chatTrackingService.updateNicknameByUuid(uuid, nickname);
                }
            } else {
                // 새로운 ChatTracking 생성 (authenticated = false, memberId = null)
                log.debug("Creating new anon user ChatTracking - UUID: {}, nickname: {}", uuid, nickname);
                chatTrackingService.createOrUpdateChatTracking(uuid, nickname, null, false);
            }
        } catch (Exception e) {
            log.error("Failed to handle anon user ChatTracking - UUID: {}, nickname: {}", uuid, nickname, e);
        }
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