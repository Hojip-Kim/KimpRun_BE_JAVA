package kimp.user.dto.request;

import lombok.Getter;

@Getter
public class CreateUserDTO {
    private Long userId;
    private String password;
    private String nickname;

}
