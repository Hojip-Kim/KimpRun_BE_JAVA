package kimp.user.vo;

import kimp.user.dto.request.ExpertVerificationRequestDto;

public class UpdateExpertVerificationRequestVo {

    private final long memberId;
    private final long verificationRequestId;
    private final ExpertVerificationRequestDto requestDto;

    public UpdateExpertVerificationRequestVo(long memberId, long verificationRequestId, ExpertVerificationRequestDto requestDto) {
        this.memberId = memberId;
        this.verificationRequestId = verificationRequestId;
        this.requestDto = requestDto;
    }

    public long getMemberId() {
        return memberId;
    }

    public long getVerificationRequestId() {
        return verificationRequestId;
    }

    public ExpertVerificationRequestDto getRequestDto() {
        return requestDto;
    }
}
