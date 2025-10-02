package kimp.user.dto.internal;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserAgentIpDTO {

    // true : 이전 로그인 장소와 동일한 장소에서 로그인
    // false : 이전 로그인 장소와 다른 장소에서 로그인. 확인 필요.
    Boolean isEqualIpBeforeLogin;


}
