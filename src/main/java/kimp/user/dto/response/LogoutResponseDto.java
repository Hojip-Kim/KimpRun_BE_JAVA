package kimp.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LogoutResponseDto {
    private String result;
    private String message;

    public LogoutResponseDto(String result, String message) {
        this.result = result;
        this.message = message;
    }
}
