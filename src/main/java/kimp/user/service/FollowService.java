package kimp.user.service;

import kimp.user.dto.response.FollowResponse;
import kimp.user.vo.*;
import org.springframework.data.domain.Page;

public interface FollowService {
    void followMember(FollowMemberVo vo);
    void unfollowMember(FollowMemberVo vo);
    Page<FollowResponse> getFollowers(GetFollowersVo vo);
    Page<FollowResponse> getFollowing(GetFollowingVo vo);
    boolean isFollowing(GetFollowStatusVo vo);
}