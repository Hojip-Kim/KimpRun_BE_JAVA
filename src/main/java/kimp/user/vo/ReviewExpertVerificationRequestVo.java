package kimp.user.vo;

public class ReviewExpertVerificationRequestVo {

    private final long reviewerId;
    private final long verificationRequestId;
    private final String rejectionReason;

    public ReviewExpertVerificationRequestVo(long reviewerId, long verificationRequestId, String rejectionReason) {
        this.reviewerId = reviewerId;
        this.verificationRequestId = verificationRequestId;
        this.rejectionReason = rejectionReason;
    }

    public long getReviewerId() {
        return reviewerId;
    }

    public long getVerificationRequestId() {
        return verificationRequestId;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }
}
