package kimp.community.vo;

import kimp.common.dto.request.PageRequestDto;

public class GetCommentsByMemberVo {

    private final Long memberId;
    private final PageRequestDto pageRequestDto;

    public GetCommentsByMemberVo(Long memberId, PageRequestDto pageRequestDto) {
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
