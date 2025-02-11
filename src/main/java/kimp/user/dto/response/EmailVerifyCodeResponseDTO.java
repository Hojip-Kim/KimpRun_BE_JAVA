package kimp.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class EmailVerifyCodeResponseDTO {

    Boolean isVerified;

    public EmailVerifyCodeResponseDTO(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public EmailVerifyCodeResponseDTO successVerified(){
        this.isVerified = true;
        return this;
    }

    public EmailVerifyCodeResponseDTO failureVerified(){
        this.isVerified = false;
        return this;
    }

}
