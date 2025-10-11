package kimp.user.vo;

import kimp.user.dto.request.DeleteUserDTO;

public class DeleteMemberVo {

    private final DeleteUserDTO deleteUserDTO;

    public DeleteMemberVo(DeleteUserDTO deleteUserDTO) {
        this.deleteUserDTO = deleteUserDTO;
    }

    public DeleteUserDTO getDeleteUserDTO() {
        return deleteUserDTO;
    }
}
