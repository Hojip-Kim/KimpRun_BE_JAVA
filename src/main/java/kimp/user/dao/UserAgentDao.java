package kimp.user.dao;

import kimp.user.entity.Member;
import kimp.user.entity.UserAgent;

public interface UserAgentDao {
    public UserAgent createUserAgent(Member member);

    public UserAgent getUserAgentByMember(Member member);
}
