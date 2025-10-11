package kimp.user.dto.response;

import kimp.user.entity.ExpertVerificationRequest;
import kimp.user.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertVerificationResponseDto {

    private Long id;
    private Long memberId;
    private String memberNickname;
    private String expertiseField;
    private String description;
    private String credentials;
    private String portfolioUrl;
    private VerificationStatus status;
    private String statusDescription;
    private String rejectionReason;
    private Long reviewedBy;
    private String reviewerNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ExpertVerificationResponseDto from(ExpertVerificationRequest verificationRequest) {
        return ExpertVerificationResponseDto.builder()
                .id(verificationRequest.getId())
                .memberId(verificationRequest.getMember().getId())
                .memberNickname(verificationRequest.getMember().getNickname())
                .expertiseField(verificationRequest.getExpertiseField())
                .description(verificationRequest.getDescription())
                .credentials(verificationRequest.getCredentials())
                .portfolioUrl(verificationRequest.getPortfolioUrl())
                .status(verificationRequest.getStatus())
                .statusDescription(verificationRequest.getStatus().getDescription())
                .rejectionReason(verificationRequest.getRejectionReason())
                .reviewedBy(verificationRequest.getReviewedBy() != null ? verificationRequest.getReviewedBy().getId() : null)
                .reviewerNickname(verificationRequest.getReviewedBy() != null ? verificationRequest.getReviewedBy().getNickname() : null)
                .createdAt(verificationRequest.getRegistedAt())
                .updatedAt(verificationRequest.getUpdatedAt())
                .build();
    }
}
