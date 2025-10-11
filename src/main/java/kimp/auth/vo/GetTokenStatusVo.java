package kimp.auth.vo;

public class GetTokenStatusVo {
    
    private final Long memberId;
    
    public GetTokenStatusVo(Long memberId) {
        this.memberId = memberId;
    }
    
    public Long getMemberId() {
        return memberId;
    }
}