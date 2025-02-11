package kimp.security.user.service;

import kimp.auth.dto.OauthProcessDTO;
import kimp.auth.service.OAuth2Service;
import kimp.security.user.CustomUserDetails;
import kimp.user.dto.UserCopyDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2Service oAuth2Service;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public CustomOAuth2UserService(OAuth2Service oAuth2Service, OAuth2AuthorizedClientService authorizedClientService) {
        this.oAuth2Service = oAuth2Service;
        this.authorizedClientService = authorizedClientService;
    }

    // TODO : refresh token 얻는 로직 고안
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        String accessTokenValue = accessToken.getTokenValue();

        OauthProcessDTO oauthProcessDTO = new OauthProcessDTO(accessTokenValue,null, oAuth2User);
        UserCopyDto userCopyDto = oAuth2Service.processOAuth2member(oauthProcessDTO);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        return new CustomUserDetails(userCopyDto, attributes);
    }
}