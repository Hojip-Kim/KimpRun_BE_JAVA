package kimp.user.vo;

import kimp.user.dto.request.ExpertProfileUpdateRequestDto;

public class UpdateExpertProfileVo {

    private final long memberId;
    private final ExpertProfileUpdateRequestDto requestDto;

    public UpdateExpertProfileVo(long memberId, ExpertProfileUpdateRequestDto requestDto) {
        this.memberId = memberId;
        this.requestDto = requestDto;
    }

    public long getMemberId() {
        return memberId;
    }

    public ExpertProfileUpdateRequestDto getRequestDto() {
        return requestDto;
    }
}
