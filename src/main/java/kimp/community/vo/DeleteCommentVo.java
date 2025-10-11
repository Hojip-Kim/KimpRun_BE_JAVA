package kimp.community.vo;

public class DeleteCommentVo {

    private final long memberId;
    private final long commentId;

    public DeleteCommentVo(long memberId, long commentId) {
        this.memberId = memberId;
        this.commentId = commentId;
    }

    public long getMemberId() {
        return memberId;
    }

    public long getCommentId() {
        return commentId;
    }
}
