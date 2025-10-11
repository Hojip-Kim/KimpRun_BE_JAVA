package kimp.user.service.expert.impl;

import kimp.user.dao.ExpertProfileDao;
import kimp.user.dto.response.ExpertProfileResponseDto;
import kimp.user.entity.ExpertProfile;
import kimp.user.service.expert.ExpertProfileService;
import kimp.user.vo.UpdateExpertProfileVo;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class ExpertProfileServiceImpl implements ExpertProfileService {

    private final ExpertProfileDao expertProfileDao;

    public ExpertProfileServiceImpl(ExpertProfileDao expertProfileDao) {
        this.expertProfileDao = expertProfileDao;
    }

    @Override
    @Transactional
    public ExpertProfile createProfile(ExpertProfile profile) {
        if (expertProfileDao.existsByMemberId(profile.getMember().getId())) {
            throw new KimprunException(
                    KimprunExceptionEnum.ALREADY_EXISTS_EXCEPTION,
                    "이미 전문가 프로필이 존재합니다",
                    HttpStatus.BAD_REQUEST,
                    "ExpertProfileServiceImpl.createProfile"
            );
        }

        return expertProfileDao.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExpertProfileResponseDto> getProfileById(Long profileId) {
        return expertProfileDao.findById(profileId)
                .map(ExpertProfileResponseDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExpertProfileResponseDto> getProfileByMemberId(Long memberId) {
        return expertProfileDao.findByMemberId(memberId)
                .map(ExpertProfileResponseDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpertProfileResponseDto> getActiveProfiles(Pageable pageable) {
        return expertProfileDao.findByIsActive(true, pageable)
                .map(ExpertProfileResponseDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpertProfileResponseDto> getAllProfiles(Pageable pageable) {
        return expertProfileDao.findAll(pageable)
                .map(ExpertProfileResponseDto::from);
    }

    @Override
    @Transactional
    public ExpertProfile updateProfile(ExpertProfile profile) {
        return expertProfileDao.save(profile);
    }

    @Override
    @Transactional
    public void deleteProfile(Long profileId) {
        ExpertProfile profile = expertProfileDao.findById(profileId)
                .orElseThrow(() -> new KimprunException(
                        KimprunExceptionEnum.NOT_FOUND_EXCEPTION,
                        "전문가 프로필을 찾을 수 없습니다",
                        HttpStatus.NOT_FOUND,
                        "ExpertProfileServiceImpl.deleteProfile"
                ));

        expertProfileDao.delete(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByMemberId(Long memberId) {
        return expertProfileDao.existsByMemberId(memberId);
    }

    @Override
    @Transactional
    public ExpertProfileResponseDto updateMyProfile(UpdateExpertProfileVo vo) {
        ExpertProfile profile = expertProfileDao.findByMemberId(vo.getMemberId())
                .orElseThrow(() -> new KimprunException(
                        KimprunExceptionEnum.NOT_FOUND_EXCEPTION,
                        "전문가 프로필을 찾을 수 없습니다",
                        HttpStatus.NOT_FOUND,
                        "ExpertProfileServiceImpl.updateMyProfile"
                ));

        if (vo.getRequestDto().getExpertiseField() != null) {
            profile.updateExpertiseField(vo.getRequestDto().getExpertiseField());
        }
        if (vo.getRequestDto().getBio() != null) {
            profile.updateBio(vo.getRequestDto().getBio());
        }
        if (vo.getRequestDto().getPortfolioUrl() != null) {
            profile.updatePortfolioUrl(vo.getRequestDto().getPortfolioUrl());
        }

        ExpertProfile savedProfile = expertProfileDao.save(profile);
        return ExpertProfileResponseDto.from(savedProfile);
    }

    @Override
    @Transactional
    public ExpertProfileResponseDto activateProfile(Long profileId) {
        ExpertProfile profile = expertProfileDao.findById(profileId)
                .orElseThrow(() -> new KimprunException(
                        KimprunExceptionEnum.NOT_FOUND_EXCEPTION,
                        "전문가 프로필을 찾을 수 없습니다",
                        HttpStatus.NOT_FOUND,
                        "ExpertProfileServiceImpl.activateProfile"
                ));

        profile.activate();
        ExpertProfile savedProfile = expertProfileDao.save(profile);
        return ExpertProfileResponseDto.from(savedProfile);
    }

    @Override
    @Transactional
    public ExpertProfileResponseDto deactivateProfile(Long profileId) {
        ExpertProfile profile = expertProfileDao.findById(profileId)
                .orElseThrow(() -> new KimprunException(
                        KimprunExceptionEnum.NOT_FOUND_EXCEPTION,
                        "전문가 프로필을 찾을 수 없습니다",
                        HttpStatus.NOT_FOUND,
                        "ExpertProfileServiceImpl.deactivateProfile"
                ));

        profile.deactivate();
        ExpertProfile savedProfile = expertProfileDao.save(profile);
        return ExpertProfileResponseDto.from(savedProfile);
    }

    @Override
    @Transactional
    public ExpertProfileResponseDto deactivateProfileByMemberId(Long memberId) {
        ExpertProfile profile = expertProfileDao.findByMemberId(memberId)
                .orElseThrow(() -> new KimprunException(
                        KimprunExceptionEnum.NOT_FOUND_EXCEPTION,
                        "전문가 프로필을 찾을 수 없습니다",
                        HttpStatus.NOT_FOUND,
                        "ExpertProfileServiceImpl.deactivateProfileByMemberId"
                ));

        profile.deactivate();
        ExpertProfile savedProfile = expertProfileDao.save(profile);
        return ExpertProfileResponseDto.from(savedProfile);
    }
}
