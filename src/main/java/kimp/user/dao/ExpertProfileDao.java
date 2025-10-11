package kimp.user.dao;

import kimp.user.entity.ExpertProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ExpertProfileDao {

    ExpertProfile save(ExpertProfile profile);

    Optional<ExpertProfile> findById(Long id);

    Optional<ExpertProfile> findByMemberId(Long memberId);

    Optional<ExpertProfile> findByMemberIdAndIsActive(Long memberId, Boolean isActive);

    Page<ExpertProfile> findByIsActive(Boolean isActive, Pageable pageable);

    Page<ExpertProfile> findAll(Pageable pageable);

    boolean existsByMemberId(Long memberId);

    void delete(ExpertProfile profile);
}
