package kimp.user.dao.impl;

import kimp.user.dao.UserAgentDao;
import kimp.user.entity.Member;
import kimp.user.entity.UserAgent;
import kimp.user.repository.user.UserAgentRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserAgentDaoImpl implements UserAgentDao {

    private final UserAgentRepository userAgentRepository;

    public UserAgentDaoImpl(UserAgentRepository userAgentRepository) {
        this.userAgentRepository = userAgentRepository;
    }

    @Override
    public UserAgent createUserAgent(Member member){
        if(member == null) {
            throw new IllegalArgumentException("member is null");
        }
        UserAgent userAgent = new UserAgent(member);
        return userAgentRepository.save(userAgent);
    }

    @Override
    public UserAgent getUserAgentByMember(Member member) {
        UserAgent userAgent = userAgentRepository.findUserAgentByMember(member);

        if(userAgent == null) {
            throw new IllegalArgumentException("user agent not found");
        }

        return userAgent;
    }

}
