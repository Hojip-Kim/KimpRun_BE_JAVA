package kimp.user.vo;

import kimp.user.dto.request.ExpertVerificationRequestDto;

public class CreateExpertVerificationRequestVo {

    private final long memberId;
    private final ExpertVerificationRequestDto requestDto;

    public CreateExpertVerificationRequestVo(long memberId, ExpertVerificationRequestDto requestDto) {
        this.memberId = memberId;
        this.requestDto = requestDto;
    }

    public long getMemberId() {
        return memberId;
    }

    public ExpertVerificationRequestDto getRequestDto() {
        return requestDto;
    }
}
