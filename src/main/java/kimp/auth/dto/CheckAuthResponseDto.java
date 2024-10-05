package kimp.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.user.dto.UserWithIdNameEmailDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckAuthResponseDto {

    @JsonProperty("user")
    public UserWithIdNameEmailDto userWithIdNameEmailDto;

    public CheckAuthResponseDto(UserWithIdNameEmailDto userWithIdNameEmailDto) {
        this.userWithIdNameEmailDto = userWithIdNameEmailDto;
    }
}
