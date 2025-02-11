package kimp.user.enums;

public enum UserRole {
    USER("ROLE_USER"),
    INFLUENCER("ROLE_INFLUENCER"),
    MANAGER("ROLE_MANAGER"),
    OPERATOR("ROLE_OPERATOR");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }
}
