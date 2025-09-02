package kimp.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowRequest {
    private Long followingId;

    public FollowRequest(Long followingId) {
        this.followingId = followingId;
    }
}