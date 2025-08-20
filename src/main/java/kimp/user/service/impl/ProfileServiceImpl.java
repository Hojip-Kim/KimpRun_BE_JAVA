package kimp.user.service.impl;

import kimp.user.dao.ProfileDao;
import kimp.user.entity.ActivityRank;
import kimp.user.entity.Member;
import kimp.user.entity.Profile;
import kimp.user.entity.SeedMoneyRange;
import kimp.user.service.ProfileService;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileDao profileDao;

    public ProfileServiceImpl(ProfileDao profileDao) {
        this.profileDao = profileDao;
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
}
