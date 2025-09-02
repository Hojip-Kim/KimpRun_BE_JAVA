package kimp.user.service;

import kimp.user.dto.response.FollowResponse;
import org.springframework.data.domain.Page;

public interface FollowService {
    void followMember(Long followerId, Long followingId);
    void unfollowMember(Long followerId, Long followingId);
    Page<FollowResponse> getFollowers(Long memberId, int page, int size);
    Page<FollowResponse> getFollowing(Long memberId, int page, int size);
    boolean isFollowing(Long followerId, Long followingId);
}