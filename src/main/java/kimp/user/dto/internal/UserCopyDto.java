package kimp.user.dto.internal;

import kimp.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
* 주의할것
* 이 DTO는 password를 보관하고있는 dto이므로 각별히 주의하여다뤄야 합니다.
* hash + salt화 되어 외부에 탈취되어도 크게 문제는 없으나,
* 그래도 외부에 노출되는 경우가 없도록 최대한 조심하여 다뤄야합니다.
* */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserCopyDto {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private UserRole role;
}
