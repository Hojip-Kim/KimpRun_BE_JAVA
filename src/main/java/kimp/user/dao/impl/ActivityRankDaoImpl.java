package kimp.user.dao.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.ActivityRankDao;
import kimp.user.entity.ActivityRank;
import kimp.user.repository.user.ActivityRankRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ActivityRankDaoImpl implements ActivityRankDao {
    
    private final ActivityRankRepository activityRankRepository;
    
    public ActivityRankDaoImpl(ActivityRankRepository activityRankRepository) {
        this.activityRankRepository = activityRankRepository;
    }
    
    @Override
    @Transactional
    public ActivityRank createActivityRank(ActivityRank activityRank) {
        if (activityRank == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "ActivityRank cannot be null", HttpStatus.BAD_REQUEST, "ActivityRankDaoImpl.save");
        }
        return activityRankRepository.save(activityRank);
    }
    
    @Override
    public Optional<ActivityRank> findById(Long id) {
        if (id == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "ID cannot be null", HttpStatus.BAD_REQUEST, "ActivityRankDaoImpl.findById");
        }
        return activityRankRepository.findById(id);
    }
    
    @Override
    public Optional<ActivityRank> findByRankKey(String rankKey) {
        if (rankKey == null || rankKey.trim().isEmpty()) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "Rank key cannot be null or empty", HttpStatus.BAD_REQUEST, "ActivityRankDaoImpl.findByRankKey");
        }
        return activityRankRepository.findByRankKey(rankKey);
    }
    
    @Override
    public Optional<ActivityRank> findByGrade(String grade) {
        if (grade == null || grade.trim().isEmpty()) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "Grade cannot be null or empty", HttpStatus.BAD_REQUEST, "ActivityRankDaoImpl.findByGrade");
        }
        return activityRankRepository.findByGrade(grade);
    }
    
    @Override
    public List<ActivityRank> findAll() {
        return activityRankRepository.findAll();
    }
    
    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "ID cannot be null", HttpStatus.BAD_REQUEST, "ActivityRankDaoImpl.deleteById");
        }
        
        if (!activityRankRepository.existsById(id)) {
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, 
                "ActivityRank not found with id: " + id, HttpStatus.NOT_FOUND, "ActivityRankDaoImpl.deleteById");
        }
        
        activityRankRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByRankKey(String rankKey) {
        if (rankKey == null || rankKey.trim().isEmpty()) {
            return false;
        }
        return activityRankRepository.existsByRankKey(rankKey);
    }
    
    @Override
    public boolean existsByGrade(String grade) {
        if (grade == null || grade.trim().isEmpty()) {
            return false;
        }
        return activityRankRepository.existsByGrade(grade);
    }
    
    @Override
    public ActivityRank update(Long id, ActivityRank activityRank) {
        if (id == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "ID cannot be null", HttpStatus.BAD_REQUEST, "ActivityRankDaoImpl.update");
        }
        
        if (activityRank == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "ActivityRank cannot be null", HttpStatus.BAD_REQUEST, "ActivityRankDaoImpl.update");
        }
        
        ActivityRank existingRank = activityRankRepository.findById(id)
            .orElseThrow(() -> new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, 
                "ActivityRank not found with id: " + id, HttpStatus.NOT_FOUND, "ActivityRankDaoImpl.update"));
        
        return existingRank.updateGrade(activityRank.getGrade());
    }
}
