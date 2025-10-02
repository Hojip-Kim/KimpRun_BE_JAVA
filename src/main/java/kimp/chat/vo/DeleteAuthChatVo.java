package kimp.chat.vo;

public class DeleteAuthChatVo {

    private final Long userId;
    private final String inherenceId;

    public DeleteAuthChatVo(Long userId, String inherenceId) {
        this.userId = userId;
        this.inherenceId = inherenceId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getInherenceId() {
        return inherenceId;
    }
}
