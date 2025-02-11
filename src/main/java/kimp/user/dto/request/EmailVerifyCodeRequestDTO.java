package kimp.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class EmailVerifyCodeRequestDTO {

    String email;
    String verifyCode;

    public EmailVerifyCodeRequestDTO(String email, String verifyCode){
        this.email = email;
        this.verifyCode = verifyCode;
    }
}
