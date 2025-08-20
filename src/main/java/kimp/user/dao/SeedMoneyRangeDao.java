package kimp.user.dao;

import kimp.user.entity.SeedMoneyRange;

import java.util.List;
import java.util.Optional;

public interface SeedMoneyRangeDao {
    
    SeedMoneyRange createSeedMoneyRange(SeedMoneyRange seedMoneyRange);
    
    Optional<SeedMoneyRange> findById(Long id);
    
    Optional<SeedMoneyRange> findBySeedRangeKey(String seedRangeKey);
    
    Optional<SeedMoneyRange> findByRank(String rank);
    
    List<SeedMoneyRange> findAll();
    
    void deleteById(Long id);
    
    boolean existsBySeedRangeKey(String seedRangeKey);
    
    boolean existsByRank(String rank);
    
    SeedMoneyRange update(Long id, SeedMoneyRange seedMoneyRange);
}
