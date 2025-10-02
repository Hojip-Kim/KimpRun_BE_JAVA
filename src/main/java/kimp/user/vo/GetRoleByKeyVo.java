package kimp.user.vo;

public class GetRoleByKeyVo {

    private final String roleKey;

    public GetRoleByKeyVo(String roleKey) {
        this.roleKey = roleKey;
    }

    public String getRoleKey() {
        return roleKey;
    }
}
