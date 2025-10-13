package kimp.user.service.member.impl;

import kimp.user.dao.ActivityRankDao;
import kimp.user.dao.SeedMoneyRangeDao;
import kimp.user.entity.ActivityRank;
import kimp.user.entity.SeedMoneyRange;
import kimp.user.service.member.AdminService;
import kimp.user.vo.CreateActivityRankVo;
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
    public ActivityRank createActivityRank(CreateActivityRankVo vo) {
        String grade = vo.getGrade();
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
    
    @Override
    @Transactional
    public void initializeActivityRanks(List<String> grades) {
        log.info("ActivityRank 배치 초기화 시작 - {} 개 등급", grades.size());
        
        // 1. 기존 모든 ActivityRank를 한 번에 조회
        List<ActivityRank> existingRanks = activityRankDao.findAll();
        List<String> existingGrades = existingRanks.stream()
            .map(ActivityRank::getGrade)
            .toList();
        
        // 2. 새로 생성해야 할 등급들 필터링
        List<String> newGrades = grades.stream()
            .filter(grade -> !existingGrades.contains(grade))
            .toList();
            
        // 3. 새로운 ActivityRank 생성
        if (!newGrades.isEmpty()) {
            List<ActivityRank> newRanks = newGrades.stream()
                .map(grade -> new ActivityRank(UUID.randomUUID().toString(), grade))
                .toList();

            for (ActivityRank rank : newRanks) {
                activityRankDao.createActivityRank(rank);
            }
            
            log.info("ActivityRank {} 개 배치 생성 완료", newRanks.size());
        } else {
            log.info("모든 ActivityRank가 이미 존재함");
        }
    }
    
    @Override
    @Transactional
    public void initializeSeedMoneyRanges(List<String[]> seedMoneyData) {
        log.info("SeedMoneyRange 배치 초기화 시작 - {} 개 범위", seedMoneyData.size());
        
        // 1. 기존 모든 SeedMoneyRange를 한 번에 조회
        List<SeedMoneyRange> existingRanges = seedMoneyRangeDao.findAll();
        List<String> existingRanks = existingRanges.stream()
            .map(SeedMoneyRange::getRank)
            .toList();
        
        // 2. 새로 생성해야 할 범위들 필터링
        List<String[]> newRangeData = seedMoneyData.stream()
            .filter(data -> !existingRanks.contains(data[1])) // data[1]이 rank
            .toList();
            
        // 3. 배치로 새로운 SeedMoneyRange 생성
        if (!newRangeData.isEmpty()) {
            List<SeedMoneyRange> newRanges = newRangeData.stream()
                .map(data -> new SeedMoneyRange(UUID.randomUUID().toString(), data[0], data[1]))
                .toList();
                
            // 저장
            for (SeedMoneyRange range : newRanges) {
                seedMoneyRangeDao.createSeedMoneyRange(range);
            }
            
            log.info("SeedMoneyRange {} 개 배치 생성 완료", newRanges.size());
        } else {
            log.info("모든 SeedMoneyRange가 이미 존재함");
        }
    }
}
