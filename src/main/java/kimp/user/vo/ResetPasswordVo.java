package kimp.user.vo;

import kimp.user.dto.request.UpdateUserPasswordRequest;

public class ResetPasswordVo {

    private final UpdateUserPasswordRequest request;

    public ResetPasswordVo(UpdateUserPasswordRequest request) {
        this.request = request;
    }

    public UpdateUserPasswordRequest getRequest() {
        return request;
    }
}
