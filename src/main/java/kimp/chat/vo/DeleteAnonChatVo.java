package kimp.chat.vo;

public class DeleteAnonChatVo {

    private final String kimprunToken;
    private final String inherenceId;

    public DeleteAnonChatVo(String kimprunToken, String inherenceId) {
        this.kimprunToken = kimprunToken;
        this.inherenceId = inherenceId;
    }

    public String getKimprunToken() {
        return kimprunToken;
    }

    public String getInherenceId() {
        return inherenceId;
    }
}
