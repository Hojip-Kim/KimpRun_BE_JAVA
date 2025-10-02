package kimp.community.vo;

import kimp.common.dto.PageRequestDto;

public class GetBoardsByMemberVo {

    private final Long memberId;
    private final PageRequestDto pageRequestDto;

    public GetBoardsByMemberVo(Long memberId, PageRequestDto pageRequestDto) {
        this.memberId = memberId;
        this.pageRequestDto = pageRequestDto;
    }

    public Long getMemberId() {
        return memberId;
    }

    public PageRequestDto getPageRequestDto() {
        return pageRequestDto;
    }
}
