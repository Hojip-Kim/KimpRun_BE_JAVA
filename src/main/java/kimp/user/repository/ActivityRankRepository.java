package kimp.user.repository;

import kimp.user.entity.ActivityRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityRankRepository extends JpaRepository<ActivityRank, Long> {
    
    Optional<ActivityRank> findByRankKey(String rankKey);
    
    Optional<ActivityRank> findByGrade(String grade);
    
    boolean existsByRankKey(String rankKey);
    
    boolean existsByGrade(String grade);
}