package kimp.user.repository.expert;

import kimp.user.entity.ExpertVerificationRequest;
import kimp.user.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpertVerificationRequestRepositoryCustom {

    public Page<ExpertVerificationRequest> findExpertVerificationRequestByStatus(VerificationStatus status, Pageable pageable);

    public Page<ExpertVerificationRequest> findAllExpertVerificationRequest(Pageable pageable);

}
