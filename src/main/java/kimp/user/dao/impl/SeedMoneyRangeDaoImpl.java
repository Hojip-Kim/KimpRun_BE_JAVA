package kimp.user.dao.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.SeedMoneyRangeDao;
import kimp.user.entity.SeedMoneyRange;
import kimp.user.repository.user.SeedMoneyRangeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class SeedMoneyRangeDaoImpl implements SeedMoneyRangeDao {
    
    private final SeedMoneyRangeRepository seedMoneyRangeRepository;
    
    public SeedMoneyRangeDaoImpl(SeedMoneyRangeRepository seedMoneyRangeRepository) {
        this.seedMoneyRangeRepository = seedMoneyRangeRepository;
    }
    
    @Override
    @Transactional
    public SeedMoneyRange createSeedMoneyRange(SeedMoneyRange seedMoneyRange) {
        if (seedMoneyRange == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "SeedMoneyRange cannot be null", HttpStatus.BAD_REQUEST, "SeedMoneyRangeDaoImpl.save");
        }
        return seedMoneyRangeRepository.save(seedMoneyRange);
    }

    @Override
    public Optional<SeedMoneyRange> findById(Long id) {
        if (id == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "ID cannot be null", HttpStatus.BAD_REQUEST, "SeedMoneyRangeDaoImpl.findById");
        }
        return seedMoneyRangeRepository.findById(id);
    }
    
    @Override
    public Optional<SeedMoneyRange> findBySeedRangeKey(String seedRangeKey) {
        if (seedRangeKey == null || seedRangeKey.trim().isEmpty()) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "Seed range key cannot be null or empty", HttpStatus.BAD_REQUEST, "SeedMoneyRangeDaoImpl.findBySeedRangeKey");
        }
        return seedMoneyRangeRepository.findBySeedRangeKey(seedRangeKey);
    }
    
    @Override
    public Optional<SeedMoneyRange> findByRank(String rank) {
        if (rank == null || rank.trim().isEmpty()) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "Rank cannot be null or empty", HttpStatus.BAD_REQUEST, "SeedMoneyRangeDaoImpl.findByRank");
        }
        return seedMoneyRangeRepository.findByRank(rank);
    }
    
    @Override
    public List<SeedMoneyRange> findAll() {
        return seedMoneyRangeRepository.findAll();
    }
    
    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "ID cannot be null", HttpStatus.BAD_REQUEST, "SeedMoneyRangeDaoImpl.deleteById");
        }
        
        if (!seedMoneyRangeRepository.existsById(id)) {
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, 
                "SeedMoneyRange not found with id: " + id, HttpStatus.NOT_FOUND, "SeedMoneyRangeDaoImpl.deleteById");
        }
        
        seedMoneyRangeRepository.deleteById(id);
    }
    
    @Override
    public boolean existsBySeedRangeKey(String seedRangeKey) {
        if (seedRangeKey == null || seedRangeKey.trim().isEmpty()) {
            return false;
        }
        return seedMoneyRangeRepository.existsBySeedRangeKey(seedRangeKey);
    }
    
    @Override
    public boolean existsByRank(String rank) {
        if (rank == null || rank.trim().isEmpty()) {
            return false;
        }
        return seedMoneyRangeRepository.existsByRank(rank);
    }
    
    @Override
    public SeedMoneyRange update(Long id, SeedMoneyRange seedMoneyRange) {
        if (id == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "ID cannot be null", HttpStatus.BAD_REQUEST, "SeedMoneyRangeDaoImpl.update");
        }
        
        if (seedMoneyRange == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "SeedMoneyRange cannot be null", HttpStatus.BAD_REQUEST, "SeedMoneyRangeDaoImpl.update");
        }
        
        SeedMoneyRange existingRange = seedMoneyRangeRepository.findById(id)
            .orElseThrow(() -> new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, 
                "SeedMoneyRange not found with id: " + id, HttpStatus.NOT_FOUND, "SeedMoneyRangeDaoImpl.update"));
        
        existingRange.updateRange(seedMoneyRange.getRange());
        existingRange.updateRank(seedMoneyRange.getRank());
        return existingRange;
    }
}
