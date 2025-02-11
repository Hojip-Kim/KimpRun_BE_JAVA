package kimp.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.user.dto.UserWithIdNameEmailDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckAuthResponseDto {

    @JsonProperty("isAuthenticated")
    public boolean isAuthenticated;

    @JsonProperty("member")
    public UserWithIdNameEmailDto member;

    public CheckAuthResponseDto(boolean authenticated, UserWithIdNameEmailDto UserWithIdNameEmailDto) {
        this.isAuthenticated = authenticated;
        this.member = UserWithIdNameEmailDto;
    }
}
