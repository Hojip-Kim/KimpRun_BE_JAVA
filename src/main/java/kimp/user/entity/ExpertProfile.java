package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expert_profile", indexes = {
    @Index(name = "idx_expert_profile_member_id", columnList = "member_id"),
    @Index(name = "idx_expert_profile_is_active", columnList = "isActive")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertProfile extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private ExpertVerificationRequest verificationRequest;

    @Column(nullable = false, length = 200)
    private String expertiseField;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 500)
    private String portfolioUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer articlesCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer followersCount = 0;

    public ExpertProfile updateExpertiseField(String expertiseField) {
        if (expertiseField != null && !expertiseField.isBlank()) {
            this.expertiseField = expertiseField;
        }
        return this;
    }

    public ExpertProfile updateBio(String bio) {
        this.bio = bio;
        return this;
    }

    public ExpertProfile updatePortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
        return this;
    }

    public ExpertProfile activate() {
        this.isActive = true;
        return this;
    }

    public ExpertProfile deactivate() {
        this.isActive = false;
        return this;
    }

    public ExpertProfile incrementArticlesCount() {
        this.articlesCount++;
        return this;
    }

    public ExpertProfile decrementArticlesCount() {
        if (this.articlesCount > 0) {
            this.articlesCount--;
        }
        return this;
    }

    public ExpertProfile incrementFollowersCount() {
        this.followersCount++;
        return this;
    }

    public ExpertProfile decrementFollowersCount() {
        if (this.followersCount > 0) {
            this.followersCount--;
        }
        return this;
    }
}
