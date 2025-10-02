package kimp.user.vo;

import kimp.user.dto.request.UpdateUserPasswordDTO;

public class UpdateMemberPasswordVo {

    private final long memberId;
    private final UpdateUserPasswordDTO updateUserPasswordDTO;

    public UpdateMemberPasswordVo(long memberId, UpdateUserPasswordDTO updateUserPasswordDTO) {
        this.memberId = memberId;
        this.updateUserPasswordDTO = updateUserPasswordDTO;
    }

    public long getMemberId() {
        return memberId;
    }

    public UpdateUserPasswordDTO getUpdateUserPasswordDTO() {
        return updateUserPasswordDTO;
    }
}
