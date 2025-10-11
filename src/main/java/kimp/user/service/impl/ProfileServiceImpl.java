package kimp.user.service.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.DeclarationDao;
import kimp.user.dao.FollowDao;
import kimp.user.dao.MemberDao;
import kimp.user.dao.ProfileDao;
import kimp.user.dto.response.ProfileInfoResponse;
import kimp.user.entity.ActivityRank;
import kimp.user.entity.Member;
import kimp.user.entity.Profile;
import kimp.user.entity.SeedMoneyRange;
import kimp.user.service.ProfileService;
import kimp.user.vo.GetProfileInfoVo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileDao profileDao;
    private final MemberDao memberDao;
    private final FollowDao followDao;
    private final DeclarationDao declarationDao;

    public ProfileServiceImpl(ProfileDao profileDao, MemberDao memberDao, FollowDao followDao, DeclarationDao declarationDao) {
        this.profileDao = profileDao;
        this.memberDao = memberDao;
        this.followDao = followDao;
        this.declarationDao = declarationDao;
    }

    @Override
    public Profile createProfile(Member member, String imageUrl, SeedMoneyRange seedMoneyRange, ActivityRank activityRank) {

        return null;
    }

    @Override
    public void profileImageChange(Long memberId, String imageUrl) {

    }

    @Override
    public void profileSeedMoneyRangeChange(Long memberId, Long seedMoneyRangeTargetId) {

    }

    @Override
    public void profileActivityRankChange(Long memberId, Long activityTargetRankId) {

    }

    @Override
    @Transactional(readOnly = true)
    public ProfileInfoResponse getProfileInfo(GetProfileInfoVo vo) {
        Member member = memberDao.findMemberByIdWithProfile(vo.getMemberId());
        if (member == null) {
            throw new KimprunException(
                KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION,
                "사용자를 찾을 수 없습니다.",
                HttpStatus.NOT_FOUND,
                "ProfileServiceImpl.getProfileInfo"
            );
        }

        Profile profile = member.getProfile();
        
        long followerCount = followDao.getFollowerCount(member);
        long followingCount = followDao.getFollowingCount(member);

        // 신고당한 횟수 조회 (toMember가 해당 멤버의 ID)
        long declarationCount = declarationDao.getDeclarationCountByToMember(member.getId().toString());

        return ProfileInfoResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .role(member.getRole() != null ? member.getRole().getRoleName().name() : "USER")
                .profileImageUrl(profile != null ? profile.getImageUrl() : null)
                .seedMoneyRange(profile != null && profile.getSeedRange() != null ? profile.getSeedRange().getRange() : "미설정")
                .activityRankGrade(profile != null && profile.getActivityRank() != null ? profile.getActivityRank().getGrade() : "미설정")
                .joinedAt(member.getRegistedAt())
                .declarationCount((int) declarationCount)
                .followerCount((int) followerCount)
                .followingCount((int) followingCount)
                .build();
    }
}
