package kimp.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UnLoginMemberResponseDto extends AuthResponseDto {

    private String uuid;

    public UnLoginMemberResponseDto(String uuid) {
        this.uuid = uuid;
    }
}
