package kimp.community.vo;

import kimp.community.dto.comment.request.RequestCreateCommentDto;

public class CreateCommentVo {

    private final long memberId;
    private final Long boardId;
    private final RequestCreateCommentDto requestCreateCommentDto;

    public CreateCommentVo(long memberId, Long boardId, RequestCreateCommentDto requestCreateCommentDto) {
        this.memberId = memberId;
        this.boardId = boardId;
        this.requestCreateCommentDto = requestCreateCommentDto;
    }

    public long getMemberId() {
        return memberId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public RequestCreateCommentDto getRequestCreateCommentDto() {
        return requestCreateCommentDto;
    }
}
