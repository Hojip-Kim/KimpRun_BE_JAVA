package kimp.user.vo;

public class VerifyEmailCodeVo {

    private final String email;
    private final String verifyCode;

    public VerifyEmailCodeVo(String email, String verifyCode) {
        this.email = email;
        this.verifyCode = verifyCode;
    }

    public String getEmail() {
        return email;
    }

    public String getVerifyCode() {
        return verifyCode;
    }
}
