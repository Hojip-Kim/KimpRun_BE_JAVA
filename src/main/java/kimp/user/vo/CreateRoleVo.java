package kimp.user.vo;

import kimp.user.enums.UserRole;

public class CreateRoleVo {

    private final String roleKey;
    private final UserRole roleName;

    public CreateRoleVo(String roleKey, UserRole roleName) {
        this.roleKey = roleKey;
        this.roleName = roleName;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public UserRole getRoleName() {
        return roleName;
    }
}
