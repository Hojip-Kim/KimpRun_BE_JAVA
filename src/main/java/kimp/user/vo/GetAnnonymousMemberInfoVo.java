package kimp.user.vo;

import kimp.user.dto.request.AnnonymousMemberInfoRequestDto;

public class GetAnnonymousMemberInfoVo {

    private final AnnonymousMemberInfoRequestDto request;

    public GetAnnonymousMemberInfoVo(AnnonymousMemberInfoRequestDto request) {
        this.request = request;
    }

    public AnnonymousMemberInfoRequestDto getRequest() {
        return request;
    }
}
