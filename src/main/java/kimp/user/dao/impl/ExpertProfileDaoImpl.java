package kimp.user.dao.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.ExpertProfileDao;
import kimp.user.entity.ExpertProfile;
import kimp.user.repository.expert.ExpertProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ExpertProfileDaoImpl implements ExpertProfileDao {

    private final ExpertProfileRepository expertProfileRepository;

    @Override
    public ExpertProfile save(ExpertProfile profile) {
        return expertProfileRepository.save(profile);
    }

    @Override
    public Optional<ExpertProfile> findById(Long id) {
        return expertProfileRepository.findById(id);
    }

    @Override
    public Optional<ExpertProfile> findByMemberId(Long memberId) {
        return expertProfileRepository.findByMemberId(memberId);
    }

    @Override
    public Optional<ExpertProfile> findByMemberIdAndIsActive(Long memberId, Boolean isActive) {
        return expertProfileRepository.findByMemberIdAndIsActive(memberId, isActive);
    }

    @Override
    @Transactional
    public Page<ExpertProfile> findByIsActive(Boolean isActive, Pageable pageable) {
        try {
            Page<ExpertProfile> activeExpertProfiles = expertProfileRepository.findExpertProfilePageByIsActive(isActive, pageable);
            return activeExpertProfiles;
        } catch(Exception e) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION, "Data가 없습니다.", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    @Transactional
    public Page<ExpertProfile> findAll(Pageable pageable) {
        try {
            Page<ExpertProfile> expertProfiles = expertProfileRepository.findAllExpertProfilePage(pageable);
            return expertProfiles;
        } catch (Exception e) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION, "Data가 없습니다.", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        return expertProfileRepository.existsByMemberId(memberId);
    }

    @Override
    public void delete(ExpertProfile profile) {
        expertProfileRepository.delete(profile);
    }
}
