package kimp.user.vo;

public class SendEmailVerifyCodeVo {

    private final String email;

    public SendEmailVerifyCodeVo(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
