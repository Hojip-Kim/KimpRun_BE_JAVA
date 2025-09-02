package kimp.auth.service;

import kimp.auth.dto.OAuth2TokenStatusDto;
import kimp.user.entity.Member;
import kimp.user.entity.Oauth;
import kimp.user.repository.OauthRepository;
import kimp.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2TokenRefreshService {

    private final OauthRepository oauthRepository;
    private final MemberService memberService;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Scheduled(fixedRate = 1800000) // 30분마다 실행
    @Transactional
    public void refreshExpiredTokens() {
        log.info("OAuth 토큰 갱신 스케줄 작업 시작");
        
        // 30분 후 만료될 토큰들 조회
        LocalDateTime expirationThreshold = LocalDateTime.now().plusMinutes(30);
        List<Oauth> expiringSoonTokens = oauthRepository.findByExpiresAtBefore(expirationThreshold);
        
        log.info("곧 만료될 토큰 {}개 발견", expiringSoonTokens.size());
        
        for (Oauth oauth : expiringSoonTokens) {
            try {
                refreshToken(oauth);
            } catch (Exception e) {
                log.error("토큰 갱신 실패 - OAuth ID: {}, Member ID: {}", oauth.getId(), oauth.getMember().getId(), e);
            }
        }
    }

    @Async
    @Transactional
    public void refreshToken(Oauth oauth) {
        if (oauth.getRefreshToken() == null || oauth.getRefreshToken().isEmpty()) {
            log.warn("Refresh token이 없음 - OAuth ID: {}", oauth.getId());
            return;
        }

        try {
            log.info("토큰 갱신 시도 - OAuth ID: {}, Member ID: {}", oauth.getId(), oauth.getMember().getId());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("refresh_token", oauth.getRefreshToken());
            params.add("grant_type", "refresh_token");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token", 
                request, 
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> tokenData = response.getBody();
                
                String newAccessToken = (String) tokenData.get("access_token");
                Long expiresIn = Long.valueOf(tokenData.get("expires_in").toString());
                String newRefreshToken = (String) tokenData.get("refresh_token");
                String scope = (String) tokenData.get("scope");
                
                // 새로운 토큰 정보로 업데이트
                oauth.setAccessToken(newAccessToken);
                oauth.setExpiresIn(expiresIn);
                oauth.setExpiresAt(LocalDateTime.now().plusSeconds(expiresIn));
                
                if (newRefreshToken != null) {
                    oauth.setRefreshToken(newRefreshToken);
                }
                
                if (scope != null) {
                    oauth.setScope(scope);
                }
                
                oauthRepository.save(oauth);
                
                log.info("토큰 갱신 성공 - OAuth ID: {}, 새로운 만료 시간: {}", oauth.getId(), oauth.getExpiresAt());
            } else {
                log.error("토큰 갱신 API 응답 오류 - Status: {}", response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("토큰 갱신 중 예외 발생 - OAuth ID: {}", oauth.getId(), e);
        }
    }

    public boolean isTokenExpired(Oauth oauth) {
        if (oauth.getExpiresAt() == null) {
            return true;
        }
        return oauth.getExpiresAt().isBefore(LocalDateTime.now());
    }

    public boolean isTokenExpiringSoon(Oauth oauth, int minutesThreshold) {
        if (oauth.getExpiresAt() == null) {
            return true;
        }
        return oauth.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(minutesThreshold));
    }

    public OAuth2TokenStatusDto getTokenStatus(Long memberId) {
        Member member = memberService.getmemberById(memberId);
        
        if (member.getOauth() == null) {
            return new OAuth2TokenStatusDto(false, "OAuth 정보가 없습니다.");
        }
        
        Oauth oauth = member.getOauth();
        boolean isExpired = isTokenExpired(oauth);
        boolean isExpiringSoon = isTokenExpiringSoon(oauth, 30);
        
        return new OAuth2TokenStatusDto(
            true,
            isExpired,
            isExpiringSoon,
            oauth.getExpiresAt(),
            oauth.getProvider(),
            oauth.getRefreshToken() != null,
            null
        );
    }

    public String refreshMemberToken(Long memberId) {
        Member member = memberService.getmemberById(memberId);
        
        if (member.getOauth() == null) {
            throw new RuntimeException("OAuth 정보가 없습니다.");
        }
        
        refreshToken(member.getOauth());
        return "토큰이 성공적으로 갱신되었습니다.";
    }
}