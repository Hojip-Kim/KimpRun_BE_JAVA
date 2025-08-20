package kimp.user.dao;

import kimp.user.entity.ActivityRank;

import java.util.List;
import java.util.Optional;

public interface ActivityRankDao {
    
    ActivityRank createActivityRank(ActivityRank activityRank);
    
    Optional<ActivityRank> findById(Long id);
    
    Optional<ActivityRank> findByRankKey(String rankKey);
    
    Optional<ActivityRank> findByGrade(String grade);
    
    List<ActivityRank> findAll();
    
    void deleteById(Long id);
    
    boolean existsByRankKey(String rankKey);
    
    boolean existsByGrade(String grade);
    
    ActivityRank update(Long id, ActivityRank activityRank);
}
