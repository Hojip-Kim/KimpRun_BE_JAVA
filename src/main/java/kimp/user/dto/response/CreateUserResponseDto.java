package kimp.user.dto.response;

import lombok.Getter;


@Getter
public class CreateUserResponseDto {
    private String email;
    private String nickname;

    public CreateUserResponseDto(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

}
