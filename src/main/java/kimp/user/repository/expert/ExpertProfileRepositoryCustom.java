package kimp.user.repository.expert;


import kimp.user.entity.ExpertProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpertProfileRepositoryCustom {

    public Page<ExpertProfile> findExpertProfilePageByIsActive(Boolean isActive, Pageable pageable);

    public Page<ExpertProfile> findAllExpertProfilePage(Pageable pageable);
}
