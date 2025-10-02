package kimp.user.vo;

import kimp.user.dto.request.CreateUserDTO;

public class CreateMemberVo {

    private final CreateUserDTO createUserDTO;

    public CreateMemberVo(CreateUserDTO createUserDTO) {
        this.createUserDTO = createUserDTO;
    }

    public CreateUserDTO getCreateUserDTO() {
        return createUserDTO;
    }
}
