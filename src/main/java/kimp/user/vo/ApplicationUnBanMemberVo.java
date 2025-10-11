package kimp.user.vo;

import kimp.user.dto.request.ApplicationUnBanMemberRequestDto;

public class ApplicationUnBanMemberVo {

    private final ApplicationUnBanMemberRequestDto request;

    public ApplicationUnBanMemberVo(ApplicationUnBanMemberRequestDto request) {
        this.request = request;
    }

    public ApplicationUnBanMemberRequestDto getRequest() {
        return request;
    }
}
