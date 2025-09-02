package kimp.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ProfileInfoResponse {
    private Long memberId;
    private String nickname;
    private String email;
    private String role;
    private String profileImageUrl;
    private String seedMoneyRange;
    private String activityRankGrade;
    private LocalDateTime joinedAt;
    private int declarationCount;
    private int followerCount;
    private int followingCount;

    public ProfileInfoResponse(Long memberId, String nickname, String email, String role, 
                             String profileImageUrl, String seedMoneyRange, String activityRankGrade, 
                             LocalDateTime joinedAt, int declarationCount, int followerCount, int followingCount) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.seedMoneyRange = seedMoneyRange;
        this.activityRankGrade = activityRankGrade;
        this.joinedAt = joinedAt;
        this.declarationCount = declarationCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }
}