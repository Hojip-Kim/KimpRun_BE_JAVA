package kimp.community.vo;

public class GetCommentsVo {

    private final Long boardId;
    private final int page;

    public GetCommentsVo(Long boardId, int page) {
        this.boardId = boardId;
        this.page = page;
    }

    public Long getBoardId() {
        return boardId;
    }

    public int getPage() {
        return page;
    }
}
