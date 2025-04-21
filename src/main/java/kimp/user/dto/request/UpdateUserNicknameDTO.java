package kimp.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdateUserNicknameDTO {

    public String nickname;

    public UpdateUserNicknameDTO(String nickname) {
        if(nickname == null || nickname.isEmpty()){
            throw new IllegalArgumentException("nickname is null or empty");
        }
        this.nickname = nickname;
    }
}
