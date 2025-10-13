package kimp.user.dao.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.ExpertVerificationRequestDao;
import kimp.user.entity.ExpertVerificationRequest;
import kimp.user.entity.QExpertVerificationRequest;
import kimp.user.enums.VerificationStatus;
import kimp.user.repository.expert.ExpertVerificationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ExpertVerificationRequestDaoImpl implements ExpertVerificationRequestDao {

    private final ExpertVerificationRequestRepository expertVerificationRequestRepository;

    @Override
    public ExpertVerificationRequest save(ExpertVerificationRequest application) {
        return expertVerificationRequestRepository.save(application);
    }

    @Override
    public Optional<ExpertVerificationRequest> findById(Long id) {
        return expertVerificationRequestRepository.findById(id);
    }

    @Override
    public Optional<ExpertVerificationRequest> findByMemberIdAndStatus(Long memberId, VerificationStatus status) {
        return expertVerificationRequestRepository.findByMemberIdAndStatus(memberId, status);
    }

    @Override
    public List<ExpertVerificationRequest> findByMemberId(Long memberId) {
        return expertVerificationRequestRepository.findByMemberId(memberId);
    }

    @Override
    public Page<ExpertVerificationRequest> findByStatus(VerificationStatus status, Pageable pageable) {
        try{
            Page<ExpertVerificationRequest> page = expertVerificationRequestRepository.findExpertVerificationRequestByStatus(status, pageable);
            return page;
        }catch (Exception e){
            throw new KimprunException(KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION, "Data가 없습니다.", HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    @Override
    public Page<ExpertVerificationRequest> findAll(Pageable pageable) {
        try{
        Page<ExpertVerificationRequest> page = expertVerificationRequestRepository.findAllExpertVerificationRequest(pageable);
        return page;
        }catch (Exception e){
            throw new KimprunException(KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION, "Data가 없습니다.", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public boolean existsByMemberIdAndStatus(Long memberId, VerificationStatus status) {
        return expertVerificationRequestRepository.existsByMemberIdAndStatus(memberId, status);
    }

    @Override
    public void delete(ExpertVerificationRequest application) {
        expertVerificationRequestRepository.delete(application);
    }
}
