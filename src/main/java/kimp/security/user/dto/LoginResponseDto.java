package kimp.security.user.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LoginResponseDto {

    @JsonProperty("result")
    public String result;

    // message
    @JsonProperty("message")
    public String message;

    // userIp
    @JsonProperty("data")
    public String data;

    // 유저 고유 식별자
    @JsonProperty("memberId")
    public Long memberId;

    public LoginResponseDto(String result, String message, String data, Long memberId) {
        this.result = result;
        this.message = message;
        this.data = data;
        this.memberId = memberId;
    }
}
