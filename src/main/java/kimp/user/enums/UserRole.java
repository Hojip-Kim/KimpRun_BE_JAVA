package kimp.user.enums;

public enum UserRole {
    USER(1),
    INFLUENCER(2),
    MANAGER(3),
    OPERATOR(4);

    private final int weight;

    UserRole(int weight) {
        this.weight = weight;
    }

    public boolean isHigherThan(UserRole userRole){
        return this.getWeight() >= userRole.getWeight();
    }

    public int getWeight() {
        return weight;
    }

}
