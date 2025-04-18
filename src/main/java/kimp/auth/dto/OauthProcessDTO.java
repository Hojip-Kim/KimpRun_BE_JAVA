package kimp.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

@NoArgsConstructor
@Getter
public class OauthProcessDTO {

    String accessToken;

    String refreshToken;

    OAuth2User oauth2User;

    public OauthProcessDTO(String accessToken, String refreshToken, OAuth2User oauth2User) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.oauth2User = oauth2User;
    }
}
