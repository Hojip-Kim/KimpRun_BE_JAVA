package kimp.user.vo;

public class UpdateAnonNicknameVo {

    private final String uuid;
    private final String nickname;

    public UpdateAnonNicknameVo(String uuid, String nickname) {
        this.uuid = uuid;
        this.nickname = nickname;
    }

    public String getUuid() {
        return uuid;
    }

    public String getNickname() {
        return nickname;
    }
}
