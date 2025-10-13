package kimp.user.repository.expert;

import kimp.user.entity.ExpertProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpertProfileRepository extends JpaRepository<ExpertProfile, Long>, ExpertProfileRepositoryCustom {

    Optional<ExpertProfile> findByMemberId(Long memberId);

    boolean existsByMemberId(Long memberId);

    Optional<ExpertProfile> findByMemberIdAndIsActive(Long memberId, Boolean isActive);
}
