package kimp.user.vo;

import kimp.user.dto.request.CdnBanMemberRequestDto;

public class CdnBanMemberVo {

    private final CdnBanMemberRequestDto request;

    public CdnBanMemberVo(CdnBanMemberRequestDto request) {
        this.request = request;
    }

    public CdnBanMemberRequestDto getRequest() {
        return request;
    }
}
