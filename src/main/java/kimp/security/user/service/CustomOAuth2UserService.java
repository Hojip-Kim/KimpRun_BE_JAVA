package kimp.security.user.service;

import kimp.auth.dto.OauthProcessDTO;
import kimp.auth.service.OAuth2Service;
import kimp.security.user.CustomUserDetails;
import kimp.user.dto.UserCopyDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2Service oAuth2Service;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public CustomOAuth2UserService(OAuth2Service oAuth2Service, OAuth2AuthorizedClientService authorizedClientService) {
        this.oAuth2Service = oAuth2Service;
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        String accessTokenValue = accessToken.getTokenValue();
        
        // 토큰 정보 추출
        String tokenType = accessToken.getTokenType() != null ? accessToken.getTokenType().getValue() : "Bearer";
        
        Set<String> scopes = accessToken.getScopes();
        String scope = scopes != null ? String.join(" ", scopes) : null;
        
        Instant expiresAt = accessToken.getExpiresAt();
        Long expiresIn = null;
        if (expiresAt != null) {
            expiresIn = expiresAt.getEpochSecond() - Instant.now().getEpochSecond();
        }
        
        // OAuth2AuthorizedClient에서 refresh token 가져오기 시도
        String refreshTokenValue = null;
        try {
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            String principalName = oAuth2User.getAttribute("sub"); // Google의 경우 sub이 principal name
            if (principalName != null) {
                OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(registrationId, principalName);
                if (authorizedClient != null) {
                    OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();
                    if (refreshToken != null) {
                        refreshTokenValue = refreshToken.getTokenValue();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to retrieve refresh token: {}", e.getMessage());
        }
        
        log.info("OAuth Token Info - Type: {}, Scope: {}, ExpiresIn: {}, HasRefreshToken: {}", 
                 tokenType, scope, expiresIn, refreshTokenValue != null);

        OauthProcessDTO oauthProcessDTO = new OauthProcessDTO(
                accessTokenValue, 
                refreshTokenValue, 
                tokenType, 
                expiresIn, 
                scope, 
                oAuth2User
        );
        
        UserCopyDto userCopyDto = oAuth2Service.processOAuth2member(oauthProcessDTO);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        return new CustomUserDetails(userCopyDto, attributes);
    }
}