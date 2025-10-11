package kimp.user.dao.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.ActivityRankDao;
import kimp.user.dao.ProfileDao;
import kimp.user.dao.SeedMoneyRangeDao;
import kimp.user.entity.ActivityRank;
import kimp.user.entity.Member;
import kimp.user.entity.Profile;
import kimp.user.entity.SeedMoneyRange;
import kimp.user.repository.user.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProfileDaoImpl implements ProfileDao {

    private final ProfileRepository profileRepository;
    private final ActivityRankDao activityRankDao;
    private final SeedMoneyRangeDao seedMoneyRangeDao;

    public ProfileDaoImpl(ProfileRepository profileRepository, ActivityRankDao activityRankDao, SeedMoneyRangeDao seedMoneyRangeDao) {
        this.profileRepository = profileRepository;
        this.activityRankDao = activityRankDao;
        this.seedMoneyRangeDao = seedMoneyRangeDao;
    }

    @Override
    public Profile save(Profile profile) {
        if (profile == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "Profile cannot be null", HttpStatus.BAD_REQUEST, "ProfileDaoImpl.save");
        }
        return profileRepository.save(profile);
    }

    @Override
    public Optional<Profile> findById(Long id) {
        if (id == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "ID cannot be null", HttpStatus.BAD_REQUEST, "ProfileDaoImpl.findById");
        }
        return profileRepository.findById(id);
    }

    @Override
    public Optional<Profile> findByMember(Member member) {
        if (member == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "Member cannot be null", HttpStatus.BAD_REQUEST, "ProfileDaoImpl.findByMember");
        }
        return profileRepository.findByMember(member);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "ID cannot be null", HttpStatus.BAD_REQUEST, "ProfileDaoImpl.deleteById");
        }
        
        if (!profileRepository.existsById(id)) {
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, 
                "Profile not found with id: " + id, HttpStatus.NOT_FOUND, "ProfileDaoImpl.deleteById");
        }
        
        profileRepository.deleteById(id);
    }

    @Override
    public Profile createDefaultProfile(Member member) {
        if (member == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "Member cannot be null", HttpStatus.BAD_REQUEST, "ProfileDaoImpl.createDefaultProfile");
        }

        ActivityRank defaultActivityRank = activityRankDao.findByGrade("새싹")
            .orElseThrow(() -> new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, 
                "Default ActivityRank '새싹' not found", HttpStatus.NOT_FOUND, "ProfileDaoImpl.createDefaultProfile"));

        SeedMoneyRange defaultSeedRange = seedMoneyRangeDao.findByRank("Bronze")
            .orElseThrow(() -> new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, 
                "Default SeedMoneyRange 'Bronze' not found", HttpStatus.NOT_FOUND, "ProfileDaoImpl.createDefaultProfile"));

        Profile profile = new Profile(member, null, defaultSeedRange, defaultActivityRank);
        return profileRepository.save(profile);
    }
}
