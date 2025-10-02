package kimp.user.vo;

import kimp.user.dto.request.UpdateUserNicknameDTO;

public class UpdateMemberNicknameVo {

    private final long memberId;
    private final UpdateUserNicknameDTO updateUserNicknameDTO;

    public UpdateMemberNicknameVo(long memberId, UpdateUserNicknameDTO updateUserNicknameDTO) {
        this.memberId = memberId;
        this.updateUserNicknameDTO = updateUserNicknameDTO;
    }

    public long getMemberId() {
        return memberId;
    }

    public UpdateUserNicknameDTO getUpdateUserNicknameDTO() {
        return updateUserNicknameDTO;
    }
}
