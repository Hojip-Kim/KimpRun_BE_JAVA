package kimp.config.session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Redis 기반 HTTP 세션 설정
 *
 * 분산 서버 환경에서 세션 공유를 위한 설정:
 * - Spring Session을 Redis에 저장하여 모든 서버 인스턴스가 세션 공유
 * - Sticky Session 불필요 (어느 서버로 요청이 와도 동일한 세션 사용)
 *
 * 동작 방식:
 * 1. Spring Security 필터 체인보다 앞단에서 SessionRepositoryFilter가 동작
 * 2. HttpServletRequest를 래핑하여 getSession() 호출 시 Redis에서 세션 조회
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600) // 1시간
public class RedisHttpSessionConfig {

    /**
     * 세션 쿠키 직렬화 설정
     *
     * Spring Session은 기본적으로 "SESSION" 쿠키를 사용하지만,
     * 기존 JSESSIONID와의 호환성을 위해 쿠키명 유지 가능
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID");
        serializer.setCookiePath("/");
        serializer.setSameSite("Lax");
        serializer.setUseHttpOnlyCookie(true);
        serializer.setUseSecureCookie(true);
        
        return serializer;
    }
}
