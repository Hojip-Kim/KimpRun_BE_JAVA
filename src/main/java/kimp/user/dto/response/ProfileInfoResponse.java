package kimp.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}