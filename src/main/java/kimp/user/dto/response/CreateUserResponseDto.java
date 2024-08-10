package kimp.user.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateUserResponseDto {
    public CreateUserResponseDto(String userid, String nickname) {
        this.userid = userid;
        this.nickname = nickname;
    }

    private String userid;
    private String nickname;
}
