package kimp.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.user.dto.response.UserWithIdNameEmailDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginMemberResponseDto extends AuthResponseDto {

    @JsonProperty("isAuthenticated")
    private boolean isAuthenticated;

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("member")
    private UserWithIdNameEmailDto member;

    public void setUuid(String uuid){
        this.uuid = uuid;
    }
}
