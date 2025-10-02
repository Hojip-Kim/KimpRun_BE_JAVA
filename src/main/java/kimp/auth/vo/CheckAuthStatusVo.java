package kimp.auth.vo;

public class CheckAuthStatusVo {
    
    private final Long memberId;
    
    public CheckAuthStatusVo(Long memberId) {
        this.memberId = memberId;
    }
    
    public Long getMemberId() {
        return memberId;
    }
}