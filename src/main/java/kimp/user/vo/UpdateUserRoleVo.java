package kimp.user.vo;

public class UpdateUserRoleVo {

    private final long userId;
    private final String role;

    public UpdateUserRoleVo(long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}
