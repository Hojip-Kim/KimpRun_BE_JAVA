package kimp.user.service;

import kimp.user.entity.ActivityRank;
import kimp.user.entity.SeedMoneyRange;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    // ActivityRank methods
    ActivityRank createActivityRank(String grade);
    
    ActivityRank updateActivityRank(Long id, String grade);
    
    Optional<ActivityRank> getActivityRankById(Long id);
    
    Optional<ActivityRank> getActivityRankByRankKey(String rankKey);
    
    Optional<ActivityRank> getActivityRankByGrade(String grade);
    
    List<ActivityRank> getAllActivityRanks();
    
    void deleteActivityRank(Long id);
    
    // SeedMoneyRange methods
    SeedMoneyRange createSeedMoneyRange(String range, String rank);
    
    SeedMoneyRange updateSeedMoneyRange(Long id, String range, String rank);
    
    Optional<SeedMoneyRange> getSeedMoneyRangeById(Long id);
    
    Optional<SeedMoneyRange> getSeedMoneyRangeBySeedRangeKey(String seedRangeKey);
    
    Optional<SeedMoneyRange> getSeedMoneyRangeByRank(String rank);
    
    List<SeedMoneyRange> getAllSeedMoneyRanges();
    
    void deleteSeedMoneyRange(Long id);
}
