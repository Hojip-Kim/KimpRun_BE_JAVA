package kimp.security.user.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LoginResponseDto {

    public String result;

    public String message;

    public String data;

    public LoginResponseDto(String result, String message) {
        this.result = result;
        this.message = message;
    }
}
