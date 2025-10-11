package kimp.community.vo;

import kimp.community.dto.comment.request.RequestUpdateCommentDto;

public class UpdateCommentVo {

    private final long memberId;
    private final RequestUpdateCommentDto requestUpdateCommentDto;

    public UpdateCommentVo(long memberId, RequestUpdateCommentDto requestUpdateCommentDto) {
        this.memberId = memberId;
        this.requestUpdateCommentDto = requestUpdateCommentDto;
    }

    public long getMemberId() {
        return memberId;
    }

    public RequestUpdateCommentDto getRequestUpdateCommentDto() {
        return requestUpdateCommentDto;
    }
}
