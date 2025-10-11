package kimp.community.vo;

public class GetBoardVo {

    private final long memberId;
    private final long boardId;
    private final int commentPage;

    public GetBoardVo(long memberId, long boardId, int commentPage) {
        this.memberId = memberId;
        this.boardId = boardId;
        this.commentPage = commentPage;
    }

    public long getMemberId() {
        return memberId;
    }

    public long getBoardId() {
        return boardId;
    }

    public int getCommentPage() {
        return commentPage;
    }
}
