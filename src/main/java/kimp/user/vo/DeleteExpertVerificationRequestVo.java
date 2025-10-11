package kimp.user.vo;

public class DeleteExpertVerificationRequestVo {

    private final long memberId;
    private final long verificationRequestId;

    public DeleteExpertVerificationRequestVo(long memberId, long verificationRequestId) {
        this.memberId = memberId;
        this.verificationRequestId = verificationRequestId;
    }

    public long getMemberId() {
        return memberId;
    }

    public long getVerificationRequestId() {
        return verificationRequestId;
    }
}
