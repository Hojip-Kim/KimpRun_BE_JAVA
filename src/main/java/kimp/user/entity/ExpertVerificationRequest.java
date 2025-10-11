package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.user.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expert_application", indexes = {
    @Index(name = "idx_expert_app_member_id", columnList = "member_id"),
    @Index(name = "idx_expert_app_status", columnList = "status"),
    @Index(name = "idx_expert_app_member_status", columnList = "member_id,status")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertVerificationRequest extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 200)
    private String expertiseField;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String credentials;

    @Column(length = 500)
    private String portfolioUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationStatus status;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Member reviewedBy;

    public ExpertVerificationRequest updateExpertiseField(String expertiseField) {
        if (expertiseField != null && !expertiseField.isBlank()) {
            this.expertiseField = expertiseField;
        }
        return this;
    }

    public ExpertVerificationRequest updateDescription(String description) {
        if (description != null && !description.isBlank()) {
            this.description = description;
        }
        return this;
    }

    public ExpertVerificationRequest updateCredentials(String credentials) {
        this.credentials = credentials;
        return this;
    }

    public ExpertVerificationRequest updatePortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
        return this;
    }

    public ExpertVerificationRequest approve(Member reviewer) {
        this.status = VerificationStatus.APPROVED;
        this.reviewedBy = reviewer;
        this.rejectionReason = null;
        return this;
    }

    public ExpertVerificationRequest reject(Member reviewer, String rejectionReason) {
        this.status = VerificationStatus.REJECTED;
        this.reviewedBy = reviewer;
        this.rejectionReason = rejectionReason;
        return this;
    }

    public ExpertVerificationRequest cancel() {
        this.status = VerificationStatus.CANCELLED;
        return this;
    }

    public boolean isPending() {
        return this.status == VerificationStatus.PENDING;
    }

    public boolean isApproved() {
        return this.status == VerificationStatus.APPROVED;
    }
}
