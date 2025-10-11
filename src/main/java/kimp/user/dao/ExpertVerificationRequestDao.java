package kimp.user.dao;

import kimp.user.entity.ExpertVerificationRequest;
import kimp.user.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ExpertVerificationRequestDao {

    ExpertVerificationRequest save(ExpertVerificationRequest application);

    Optional<ExpertVerificationRequest> findById(Long id);

    Optional<ExpertVerificationRequest> findByMemberIdAndStatus(Long memberId, VerificationStatus status);

    List<ExpertVerificationRequest> findByMemberId(Long memberId);

    Page<ExpertVerificationRequest> findByStatus(VerificationStatus status, Pageable pageable);

    Page<ExpertVerificationRequest> findAll(Pageable pageable);

    boolean existsByMemberIdAndStatus(Long memberId, VerificationStatus status);

    void delete(ExpertVerificationRequest application);
}
