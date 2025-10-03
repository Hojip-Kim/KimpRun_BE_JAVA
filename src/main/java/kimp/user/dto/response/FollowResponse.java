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
public class FollowResponse {
    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime followedAt;
}