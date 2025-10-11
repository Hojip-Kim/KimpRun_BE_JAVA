package kimp.user.vo;

import kimp.user.dto.request.DeActivateUserDTO;

public class DeActivateMemberVo {

    private final long memberId;
    private final DeActivateUserDTO deActivateUserDTO;

    public DeActivateMemberVo(long memberId, DeActivateUserDTO deActivateUserDTO) {
        this.memberId = memberId;
        this.deActivateUserDTO = deActivateUserDTO;
    }

    public long getMemberId() {
        return memberId;
    }

    public DeActivateUserDTO getDeActivateUserDTO() {
        return deActivateUserDTO;
    }
}
