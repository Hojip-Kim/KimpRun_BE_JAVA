package kimp.auth.vo;

public class RefreshMemberTokenVo {
    
    private final Long memberId;
    
    public RefreshMemberTokenVo(Long memberId) {
        this.memberId = memberId;
    }
    
    public Long getMemberId() {
        return memberId;
    }
}