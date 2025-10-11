package kimp.user.service.expert;

import kimp.user.dto.response.ExpertVerificationResponseDto;
import kimp.user.enums.VerificationStatus;
import kimp.user.vo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ExpertVerificationService {

    ExpertVerificationResponseDto createVerificationRequest(CreateExpertVerificationRequestVo vo);

    ExpertVerificationResponseDto updateVerificationRequest(UpdateExpertVerificationRequestVo vo);

    void deleteVerificationRequest(DeleteExpertVerificationRequestVo vo);

    Optional<ExpertVerificationResponseDto> getVerificationRequestById(Long verificationRequestId);

    Page<ExpertVerificationResponseDto> getVerificationRequestsByMember(GetExpertVerificationRequestsByMemberVo vo);

    Page<ExpertVerificationResponseDto> getVerificationRequestsByStatus(VerificationStatus status, Pageable pageable);

    Page<ExpertVerificationResponseDto> getAllVerificationRequests(Pageable pageable);

    ExpertVerificationResponseDto approveVerificationRequest(ReviewExpertVerificationRequestVo vo);

    ExpertVerificationResponseDto rejectVerificationRequest(ReviewExpertVerificationRequestVo vo);

    boolean hasPendingVerificationRequest(Long memberId);

    boolean hasApprovedVerificationRequest(Long memberId);
}
