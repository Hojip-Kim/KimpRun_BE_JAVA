package kimp.user.dto.response;

import kimp.user.entity.ExpertProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertProfileResponseDto {

    private Long id;
    private Long memberId;
    private String memberNickname;
    private Long verificationRequestId;
    private String expertiseField;
    private String bio;
    private String portfolioUrl;
    private Boolean isActive;
    private Integer articlesCount;
    private Integer followersCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ExpertProfileResponseDto from(ExpertProfile profile) {
        return ExpertProfileResponseDto.builder()
                .id(profile.getId())
                .memberId(profile.getMember().getId())
                .memberNickname(profile.getMember().getNickname())
                .verificationRequestId(profile.getVerificationRequest() != null ? profile.getVerificationRequest().getId() : null)
                .expertiseField(profile.getExpertiseField())
                .bio(profile.getBio())
                .portfolioUrl(profile.getPortfolioUrl())
                .isActive(profile.getIsActive())
                .articlesCount(profile.getArticlesCount())
                .followersCount(profile.getFollowersCount())
                .createdAt(profile.getRegistedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
