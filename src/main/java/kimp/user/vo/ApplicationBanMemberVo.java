package kimp.user.vo;

import kimp.user.dto.request.ApplicationBanMemberRequestDto;

public class ApplicationBanMemberVo {

    private final ApplicationBanMemberRequestDto request;

    public ApplicationBanMemberVo(ApplicationBanMemberRequestDto request) {
        this.request = request;
    }

    public ApplicationBanMemberRequestDto getRequest() {
        return request;
    }
}
