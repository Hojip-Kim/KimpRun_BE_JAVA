package kimp.user.service.impl;

import kimp.user.dao.ActivityRankDao;
import kimp.user.dao.SeedMoneyRangeDao;
import kimp.user.entity.ActivityRank;
import kimp.user.entity.SeedMoneyRange;
import kimp.user.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final ActivityRankDao activityRankDao;
    private final SeedMoneyRangeDao seedMoneyRangeDao;

    public AdminServiceImpl(ActivityRankDao activityRankDao, SeedMoneyRangeDao seedMoneyRangeDao) {
        this.activityRankDao = activityRankDao;
        this.seedMoneyRangeDao = seedMoneyRangeDao;
    }

    @Override
    @Transactional
    public ActivityRank createActivityRank(String grade) {
        log.info("Creating ActivityRank with grade: {}", grade);
        String rankKey = UUID.randomUUID().toString();
        ActivityRank activityRank = new ActivityRank(rankKey, grade);
        return activityRankDao.createActivityRank(activityRank);
    }
    
    @Override
    @Transactional
    public ActivityRank updateActivityRank(Long id, String grade) {
        log.info("Updating ActivityRank with id: {} and grade: {}", id, grade);
        ActivityRank updatedRank = new ActivityRank();
        updatedRank.setGrade(grade);
        return activityRankDao.update(id, updatedRank);
    }
    
    @Override
    public Optional<ActivityRank> getActivityRankById(Long id) {
        log.debug("Getting ActivityRank by id: {}", id);
        return activityRankDao.findById(id);
    }
    
    @Override
    public Optional<ActivityRank> getActivityRankByRankKey(String rankKey) {
        log.debug("Getting ActivityRank by rankKey: {}", rankKey);
        return activityRankDao.findByRankKey(rankKey);
    }
    
    @Override
    public Optional<ActivityRank> getActivityRankByGrade(String grade) {
        log.debug("Getting ActivityRank by grade: {}", grade);
        return activityRankDao.findByGrade(grade);
    }
    
    @Override
    public List<ActivityRank> getAllActivityRanks() {
        log.debug("Getting all ActivityRanks");
        return activityRankDao.findAll();
    }
    
    @Override
    @Transactional
    public void deleteActivityRank(Long id) {
        log.info("Deleting ActivityRank with id: {}", id);
        activityRankDao.deleteById(id);
    }
    
    @Override
    @Transactional
    public SeedMoneyRange createSeedMoneyRange(String range, String rank) {
        log.info("Creating SeedMoneyRange with range: {} and rank: {}", range, rank);
        String seedRangeKey = UUID.randomUUID().toString();
        SeedMoneyRange seedMoneyRange = new SeedMoneyRange(seedRangeKey, range, rank);
        SeedMoneyRange createdSeedMoneyRange = seedMoneyRangeDao.createSeedMoneyRange(seedMoneyRange);
        return createdSeedMoneyRange;
    }
    
    @Override
    @Transactional
    public SeedMoneyRange updateSeedMoneyRange(Long id, String range, String rank) {
        log.info("Updating SeedMoneyRange with id: {}, range: {} and rank: {}", id, range, rank);
        SeedMoneyRange updatedRange = new SeedMoneyRange();
        updatedRange.setRange(range);
        updatedRange.setRank(rank);
        return seedMoneyRangeDao.update(id, updatedRange);
    }
    
    @Override
    public Optional<SeedMoneyRange> getSeedMoneyRangeById(Long id) {
        log.debug("Getting SeedMoneyRange by id: {}", id);
        return seedMoneyRangeDao.findById(id);
    }
    
    @Override
    public Optional<SeedMoneyRange> getSeedMoneyRangeBySeedRangeKey(String seedRangeKey) {
        log.debug("Getting SeedMoneyRange by seedRangeKey: {}", seedRangeKey);
        return seedMoneyRangeDao.findBySeedRangeKey(seedRangeKey);
    }
    
    @Override
    public Optional<SeedMoneyRange> getSeedMoneyRangeByRank(String rank) {
        log.debug("Getting SeedMoneyRange by rank: {}", rank);
        return seedMoneyRangeDao.findByRank(rank);
    }
    
    @Override
    public List<SeedMoneyRange> getAllSeedMoneyRanges() {
        log.debug("Getting all SeedMoneyRanges");
        return seedMoneyRangeDao.findAll();
    }
    
    @Override
    @Transactional
    public void deleteSeedMoneyRange(Long id) {
        log.info("Deleting SeedMoneyRange with id: {}", id);
        seedMoneyRangeDao.deleteById(id);
    }
}
