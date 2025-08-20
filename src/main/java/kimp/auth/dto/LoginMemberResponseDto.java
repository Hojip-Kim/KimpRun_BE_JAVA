package kimp.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.user.dto.UserWithIdNameEmailDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginMemberResponseDto extends AuthResponseDto {

    @JsonProperty("isAuthenticated")
    public boolean isAuthenticated;

    @JsonProperty("uuid")
    public String uuid;

    @JsonProperty("member")
    public UserWithIdNameEmailDto member;

    public LoginMemberResponseDto(boolean authenticated, UserWithIdNameEmailDto UserWithIdNameEmailDto, String uuid) {
        this.isAuthenticated = authenticated;
        this.member = UserWithIdNameEmailDto;
        this.uuid = uuid;
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }
}
