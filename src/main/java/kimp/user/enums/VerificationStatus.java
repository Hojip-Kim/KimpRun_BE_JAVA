package kimp.user.enums;

public enum VerificationStatus {
    PENDING("대기중"),
    APPROVED("승인됨"),
    REJECTED("거부됨"),
    CANCELLED("취소됨");

    private final String description;

    VerificationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
