package kimp.user.vo;

public class GetExpertVerificationRequestsByMemberVo {

    private final long memberId;
    private final int page;
    private final int size;

    public GetExpertVerificationRequestsByMemberVo(long memberId, int page, int size) {
        this.memberId = memberId;
        this.page = page;
        this.size = size;
    }

    public long getMemberId() {
        return memberId;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
