package kimp.user.repository;

import kimp.user.entity.AnnonyMousMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnonyMousMemberRepository extends JpaRepository<AnnonyMousMember, Long> {

    public Optional<AnnonyMousMember> findAnonymousMemberByMemberUuid(String uuid);

    public Optional<AnnonyMousMember> findAnonymousMemberByMemberIp(String ip);

    public List<AnnonyMousMember> findAllByBannedExpiryTimeBeforeAndIsBannedTrue(Long expireTime);


}
