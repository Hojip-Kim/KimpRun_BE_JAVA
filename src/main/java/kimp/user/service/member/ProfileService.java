package kimp.user.service.member;

import kimp.user.dto.response.ProfileInfoResponse;
import kimp.user.entity.ActivityRank;
import kimp.user.entity.Member;
import kimp.user.entity.Profile;
import kimp.user.entity.SeedMoneyRange;
import kimp.user.vo.GetProfileInfoVo;

public interface ProfileService {

    /*
    * Profile Section
    * */

    // Member가 생성될 떄 자동으로 Profile생성을 위한 createProfile method
    public Profile createProfile(Member member, String imageUrl, SeedMoneyRange seedMoneyRange, ActivityRank activityRank);

    // profile image변경을 위한 method
    public void profileImageChange(Long memberId, String imageUrl);

    // seedMoneyRange 변경을 위한 method
    // 이는 관리자가 변경을 해주어야 함.
    public void profileSeedMoneyRangeChange(Long memberId, Long seedMoneyRangeTargetId);

    // activityRank 변경을 위한 method
    // 이는 관리자가 변경을 해주어야 함.
    public void profileActivityRankChange(Long memberId, Long activityTargetRankId);

    // 프로필 정보 조회
    public ProfileInfoResponse getProfileInfo(GetProfileInfoVo vo);
}
