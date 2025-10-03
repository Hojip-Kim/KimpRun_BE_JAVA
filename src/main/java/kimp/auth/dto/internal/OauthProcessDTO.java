package kimp.auth.dto.internal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

@NoArgsConstructor
@Getter
public class OauthProcessDTO {

    String accessToken;

    String refreshToken;

    String tokenType;

    Long expiresIn;

    String scope;

    OAuth2User oauth2User;

    public OauthProcessDTO(String accessToken, String refreshToken, OAuth2User oauth2User) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.oauth2User = oauth2User;
    }

    public OauthProcessDTO(String accessToken, String refreshToken, String tokenType, Long expiresIn, String scope, OAuth2User oauth2User) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.scope = scope;
        this.oauth2User = oauth2User;
    }
}
