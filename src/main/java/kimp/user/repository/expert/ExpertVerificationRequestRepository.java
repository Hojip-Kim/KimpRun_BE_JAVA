package kimp.user.repository.expert;

import kimp.user.entity.ExpertVerificationRequest;
import kimp.user.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpertVerificationRequestRepository extends JpaRepository<ExpertVerificationRequest, Long>, ExpertVerificationRequestRepositoryCustom {

    Optional<ExpertVerificationRequest> findByMemberIdAndStatus(Long memberId, VerificationStatus status);

    List<ExpertVerificationRequest> findByMemberId(Long memberId);

    List<ExpertVerificationRequest> findByStatus(VerificationStatus status);

    boolean existsByMemberIdAndStatus(Long memberId, VerificationStatus status);
}
