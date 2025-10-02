package kimp.community.vo;

public class DeleteBoardVo {

    private final long memberId;
    private final long boardId;

    public DeleteBoardVo(long memberId, long boardId) {
        this.memberId = memberId;
        this.boardId = boardId;
    }

    public long getMemberId() {
        return memberId;
    }

    public long getBoardId() {
        return boardId;
    }
}
