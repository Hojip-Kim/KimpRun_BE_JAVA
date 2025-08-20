package kimp.user.dto.request;

import kimp.user.enums.Oauth;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserDTO {
    private String nickname;
    private String email;
    private String password;
    private Oauth oauth = null;
    private String providerId = null;
    private String accessToken = null;
    private String refreshToken = null;
    private String tokenType = null;
    private Long expiresIn = null;
    private String scope = null;

    public CreateUserDTO() {}

    public CreateUserDTO(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

}
