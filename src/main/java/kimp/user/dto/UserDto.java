package kimp.user.dto;

import kimp.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDto {

    private String loginId;

    private String nickname;

    private UserRole role;

}
