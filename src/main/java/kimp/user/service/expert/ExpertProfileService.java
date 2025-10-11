package kimp.user.service.expert;

import kimp.user.dto.response.ExpertProfileResponseDto;
import kimp.user.entity.ExpertProfile;
import kimp.user.vo.UpdateExpertProfileVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ExpertProfileService {

    ExpertProfile createProfile(ExpertProfile profile);

    Optional<ExpertProfileResponseDto> getProfileById(Long profileId);

    Optional<ExpertProfileResponseDto> getProfileByMemberId(Long memberId);

    Page<ExpertProfileResponseDto> getActiveProfiles(Pageable pageable);

    Page<ExpertProfileResponseDto> getAllProfiles(Pageable pageable);

    ExpertProfile updateProfile(ExpertProfile profile);

    ExpertProfileResponseDto updateMyProfile(UpdateExpertProfileVo vo);

    void deleteProfile(Long profileId);

    boolean existsByMemberId(Long memberId);

    // 관리자 기능
    ExpertProfileResponseDto activateProfile(Long profileId);

    ExpertProfileResponseDto deactivateProfile(Long profileId);

    ExpertProfileResponseDto deactivateProfileByMemberId(Long memberId);
}
