package kimp.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FollowResponse {
    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime followedAt;

    public FollowResponse(Long memberId, String nickname, String profileImageUrl, LocalDateTime followedAt) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.followedAt = followedAt;
    }
}