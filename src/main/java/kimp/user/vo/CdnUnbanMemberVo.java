package kimp.user.vo;

import kimp.user.dto.request.CdnUnbanMemberRequestDto;

public class CdnUnbanMemberVo {

    private final CdnUnbanMemberRequestDto request;

    public CdnUnbanMemberVo(CdnUnbanMemberRequestDto request) {
        this.request = request;
    }

    public CdnUnbanMemberRequestDto getRequest() {
        return request;
    }
}
