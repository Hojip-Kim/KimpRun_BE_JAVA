package kimp.user.vo;

public class GetProfileInfoVo {

    private final Long memberId;

    public GetProfileInfoVo(Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}
