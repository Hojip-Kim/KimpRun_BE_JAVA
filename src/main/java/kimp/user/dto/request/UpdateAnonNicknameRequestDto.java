package kimp.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateAnonNicknameRequestDto {
    
    private String uuid;
    private String nickname;

    public UpdateAnonNicknameRequestDto(String uuid, String nickname) {
        this.uuid = uuid;
        this.nickname = nickname;
    }
}