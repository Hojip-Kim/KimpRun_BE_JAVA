package kimp.user.vo;

public class GetFollowingVo {

    private final Long memberId;
    private final int page;
    private final int size;

    public GetFollowingVo(Long memberId, int page, int size) {
        this.memberId = memberId;
        this.page = page;
        this.size = size;
    }

    public Long getMemberId() {
        return memberId;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
