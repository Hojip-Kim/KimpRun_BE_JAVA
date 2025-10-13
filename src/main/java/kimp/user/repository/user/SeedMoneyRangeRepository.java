package kimp.user.repository.user;

import kimp.user.entity.SeedMoneyRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeedMoneyRangeRepository extends JpaRepository<SeedMoneyRange, Long> {
    
    Optional<SeedMoneyRange> findBySeedRangeKey(String seedRangeKey);
    
    Optional<SeedMoneyRange> findByRank(String rank);
    
    boolean existsBySeedRangeKey(String seedRangeKey);
    
    boolean existsByRank(String rank);
}