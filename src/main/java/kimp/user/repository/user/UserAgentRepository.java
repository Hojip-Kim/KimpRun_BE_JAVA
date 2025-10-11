package kimp.user.repository.user;

import kimp.user.entity.Member;
import kimp.user.entity.UserAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAgentRepository extends JpaRepository<UserAgent, Long> {

    public UserAgent findUserAgentByMember(Member member);
}
