package kimp.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnnonymousMemberInfoRequestDto {
    private String uuid;
    private String ip;

    public AnnonymousMemberInfoRequestDto(String uuid, String ip) {
        this.uuid = uuid;
        this.ip = ip;
    }
}
