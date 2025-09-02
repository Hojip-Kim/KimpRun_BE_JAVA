package kimp.user.util;

import kimp.user.dao.MemberDao;
import org.springframework.stereotype.Component;


@Component
public class NicknameGeneratorUtils {

    private final MemberDao memberDao;
    private static final int MAX_RETRIES = 10;

    public NicknameGeneratorUtils(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public String createRandomNickname() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String candidate = "유저_" + ((int)(Math.random() * 900000) + 100000);
            if (!memberDao.isExistsByNickname(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("랜덤 닉네임 생성 실패 (중복 너무 많음)");
    }
}
