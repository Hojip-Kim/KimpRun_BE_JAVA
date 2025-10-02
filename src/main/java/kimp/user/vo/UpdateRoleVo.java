package kimp.user.vo;

import kimp.user.enums.UserRole;

public class UpdateRoleVo {

    private final Long id;
    private final UserRole roleName;

    public UpdateRoleVo(Long id, UserRole roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public Long getId() {
        return id;
    }

    public UserRole getRoleName() {
        return roleName;
    }
}
