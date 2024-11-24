package kimp.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class EmailVerifyResponseDTO {

    Boolean isExisted;

    String verificationCode;


    public EmailVerifyResponseDTO setIsExisted(Boolean isExisted) {
        this.isExisted = isExisted;
        return this;
    }

    public EmailVerifyResponseDTO setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
        return this;
    }





}
