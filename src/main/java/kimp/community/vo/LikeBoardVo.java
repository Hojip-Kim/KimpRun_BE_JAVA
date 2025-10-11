package kimp.community.vo;

public class LikeBoardVo {

    private final long boardId;
    private final long memberId;

    public LikeBoardVo(long boardId, long memberId) {
        this.boardId = boardId;
        this.memberId = memberId;
    }

    public long getBoardId() {
        return boardId;
    }

    public long getMemberId() {
        return memberId;
    }
}
