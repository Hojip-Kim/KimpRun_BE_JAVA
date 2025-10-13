package kimp.user.service.expert.impl;

import kimp.user.dao.ExpertVerificationRequestDao;
import kimp.user.dto.response.ExpertVerificationResponseDto;
import kimp.user.entity.ExpertVerificationRequest;
import kimp.user.entity.ExpertProfile;
import kimp.user.enums.VerificationStatus;
import kimp.user.service.expert.ExpertVerificationService;
import kimp.user.service.expert.ExpertProfileService;
import kimp.user.vo.*;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.MemberDao;
import kimp.user.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpertVerificationServiceImpl implements ExpertVerificationService {

    private final ExpertVerificationRequestDao expertVerificationRequestDao;
    private final ExpertProfileService expertProfileService;
    private final MemberDao memberDao;

    @Override
    @Transactional
    public ExpertVerificationResponseDto createVerificationRequest(CreateExpertVerificationRequestVo vo) {
        Member member = memberDao.findById(vo.getMemberId())
                .orElseThrow(() -> new KimprunException(
                        KimprunExceptionEnum.NOT_FOUND_EXCEPTION,
                        "회원을 찾을 수 없습니다",
                        HttpStatus.NOT_FOUND,
                        "ExpertVerificationServiceImpl.createVerificationRequest"
                ));

        // 이미 승인된 전문가인지 확인
        if (expertProfileService.existsByMemberId(vo.getMemberId())) {
            throw new KimprunException(
                    KimprunExceptionEnum.ALREADY_EXISTS_EXCEPTION,
                    "이미 전문가로 등록되어 있습니다",
                    HttpStatus.BAD_REQUEST,
                    "ExpertVerificationServiceImpl.createVerificationRequest"
            );
        }

        // 대기 중인 신청이 있는지 확인
        if (expertVerificationRequestDao.existsByMemberIdAndStatus(vo.getMemberId(), VerificationStatus.PENDING)) {
            throw new KimprunException(
                    KimprunExceptionEnum.ALREADY_EXISTS_EXCEPTION,
                    "이미 대기 중인 신청이 있습니다",
                    HttpStatus.BAD_REQUEST,
                    "ExpertVerificationServiceImpl.createVerificationRequest"
            );
        }

        ExpertVerificationRequest verificationRequest = ExpertVerificationRequest.builder()
                .member(member)
                .expertiseField(vo.getRequestDto().getExpertiseField())
                .description(vo.getRequestDto().getDescription())
                .credentials(vo.getRequestDto().getCredentials())
                .portfolioUrl(vo.getRequestDto().getPortfolioUrl())
                .status(VerificationStatus.PENDING)
                .build();

        ExpertVerificationRequest savedVerificationRequest = expertVerificationRequestDao.save(verificationRequest);
        return ExpertVerificationResponseDto.from(savedVerificationRequest);
    }

    @Override
    @Transactional
    public ExpertVerificationResponseDto updateVerificationRequest(UpdateExpertVerificationRequestVo vo) {
        ExpertVerificationRequest verificationRequest = expertVerificationRequestDao.findById(vo.getVerificationRequestId())
                .orElseThrow(() -> new KimprunException(
                        KimprunExceptionEnum.NOT_FOUND_EXCEPTION,
                        "신청을 찾을 수 없습니다",
                        HttpStatus.NOT_FOUND,
                        "ExpertVerificationServiceImpl.updateVerificationRequest"
                ));

        if (!verificationRequest.getMember().getId().equals(vo.getMemberId())) {
            throw new KimprunException(
                    KimprunExceptionEnum.UNAUTHORIZED_EXCEPTION,
                    "본인의 신청만 수정할 수 있습니다",
                    HttpStatus.FORBIDDEN,
                    "ExpertVerificationServiceImpl.updateVerificationRequest"
            );
        }

        if (!verificationRequest.isPending()) {
            throw new KimprunException(
                    KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION,
                    "대기 중인 신청만 수정할 수 있습니다",
                    HttpStatus.BAD_REQUEST,
                    "ExpertVerificationServiceImpl.updateVerificationRequest"
            );
        }

        verificationRequest.updateExpertiseField(vo.getRequestDto().getExpertiseField())
                .updateDescription(vo.getRequestDto().getDescription())
                .updateCredentials(vo.getRequestDto().getCredentials())
                .updatePortfolioUrl(vo.getRequestDto().getPortfolioUrl());

        ExpertVerificationRequest savedVerificationRequest = expertVerificationRequestDao.save(verificationRequest);
        return ExpertVerificationResponseDto.from(savedVerificationRequest);
    }

    @Override
    @Transactional
    public void deleteVerificationRequest(DeleteExpertVerificationRequestVo vo) {
        ExpertVerificationRequest verificationRequest = expertVerificationRequestDao.findById(vo.getVerificationRequestId())
                .orElseThrow(() -> new KimprunException(
                        KimprunExceptionEnum.NOT_FOUND_EXCEPTION,
                        "신청을 찾을 수 없습니다",
                        HttpStatus.NOT_FOUND,
                        "ExpertVerificationServiceImpl.deleteVerificationRequest"
                ));

        if (!verificationRequest.getMember().getId().equals(vo.getMemberId())) {
            throw new KimprunException(
                    KimprunExceptionEnum.UNAUTHORIZED_EXCEPTION,
                    "본인의 신청만 삭제할 수 있습니다",
                    HttpStatus.FORBIDDEN,
                    "ExpertVerificationServiceImpl.deleteVerificationRequest"
            );
        }

        if (!verificationRequest.isPending()) {
            throw new KimprunException(
                    KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION,
                    "대기 중인 신청만 삭제할 수 있습니다",
                    HttpStatus.BAD_REQUEST,
                    "ExpertVerificationServiceImpl.deleteVerificationRequest"
            );
        }

        verificationRequest.cancel();
        expertVerificationRequestDao.save(verificationRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExpertVerificationResponseDto> getVerificationRequestById(Long verificationRequestId) {
        return expertVerificationRequestDao.findById(verificationRequestId)
                .map(ExpertVerificationResponseDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpertVerificationResponseDto> getVerificationRequestsByMember(GetExpertVerificationRequestsByMemberVo vo) {
        List<ExpertVerificationRequest> verificationRequests = expertVerificationRequestDao.findByMemberId(vo.getMemberId());
        Pageable pageable = PageRequest.of(vo.getPage(), vo.getSize());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), verificationRequests.size());

        List<ExpertVerificationRequest> pageContent = verificationRequests.subList(start, end);
        Page<ExpertVerificationRequest> verificationRequestPage = new PageImpl<>(pageContent, pageable, verificationRequests.size());
        return verificationRequestPage.map(ExpertVerificationResponseDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpertVerificationResponseDto> getVerificationRequestsByStatus(VerificationStatus status, Pageable pageable) {
        return expertVerificationRequestDao.findByStatus(status, pageable)
                .map(ExpertVerificationResponseDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpertVerificationResponseDto> getAllVerificationRequests(Pageable pageable) {
        return expertVerificationRequestDao.findAll(pageable)
                .map(ExpertVerificationResponseDto::from);
    }

    @Override
    @Transactional
    public ExpertVerificationResponseDto approveVerificationRequest(ReviewExpertVerificationRequestVo vo) {
        Member reviewer = memberDao.findById(vo.getReviewerId())
                .orElseThrow(() -> new KimprunException(
                        KimprunExceptionEnum.NOT_FOUND_EXCEPTION,
                        "검토자를 찾을 수 없습니다",
                        HttpStatus.NOT_FOUND,
                        "ExpertVerificationServiceImpl.approveVerificationRequest"
                ));

        ExpertVerificationRequest verificationRequest = expertVerificationRequestDao.findById(vo.getVerificationRequestId())
                .orElseThrow(() -> new KimprunException(
                        KimprunExceptionEnum.NOT_FOUND_EXCEPTION,
                        "신청을 찾을 수 없습니다",
                        HttpStatus.NOT_FOUND,
                        "ExpertVerificationServiceImpl.approveVerificationRequest"
                ));

        if (!verificationRequest.isPending()) {
            throw new KimprunException(
                    KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION,
                    "대기 중인 신청만 승인할 수 있습니다",
                    HttpStatus.BAD_REQUEST,
                    "ExpertVerificationServiceImpl.approveVerificationRequest"
            );
        }

        verificationRequest.approve(reviewer);
        ExpertVerificationRequest savedVerificationRequest = expertVerificationRequestDao.save(verificationRequest);

        // ExpertProfile 생성
        ExpertProfile profile = ExpertProfile.builder()
                .member(verificationRequest.getMember())
                .verificationRequest(savedVerificationRequest)
                .expertiseField(verificationRequest.getExpertiseField())
                .bio(verificationRequest.getDescription())
                .portfolioUrl(verificationRequest.getPortfolioUrl())
                .isActive(true)
                .articlesCount(0)
                .followersCount(0)
                .build();

        expertProfileService.createProfile(profile);

        return ExpertVerificationResponseDto.from(savedVerificationRequest);
    }

    @Override
    @Transactional
    public ExpertVerificationResponseDto rejectVerificationRequest(ReviewExpertVerificationRequestVo vo) {
        Member reviewer = memberDao.findById(vo.getReviewerId())
                .orElseThrow(() -> new KimprunException(
                        KimprunExceptionEnum.NOT_FOUND_EXCEPTION,
                        "검토자를 찾을 수 없습니다",
                        HttpStatus.NOT_FOUND,
                        "ExpertVerificationServiceImpl.rejectVerificationRequest"
                ));

        ExpertVerificationRequest verificationRequest = expertVerificationRequestDao.findById(vo.getVerificationRequestId())
                .orElseThrow(() -> new KimprunException(
                        KimprunExceptionEnum.NOT_FOUND_EXCEPTION,
                        "신청을 찾을 수 없습니다",
                        HttpStatus.NOT_FOUND,
                        "ExpertVerificationServiceImpl.rejectVerificationRequest"
                ));

        if (!verificationRequest.isPending()) {
            throw new KimprunException(
                    KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION,
                    "대기 중인 신청만 거부할 수 있습니다",
                    HttpStatus.BAD_REQUEST,
                    "ExpertVerificationServiceImpl.rejectVerificationRequest"
            );
        }

        verificationRequest.reject(reviewer, vo.getRejectionReason());
        ExpertVerificationRequest savedVerificationRequest = expertVerificationRequestDao.save(verificationRequest);
        return ExpertVerificationResponseDto.from(savedVerificationRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPendingVerificationRequest(Long memberId) {
        return expertVerificationRequestDao.existsByMemberIdAndStatus(memberId, VerificationStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasApprovedVerificationRequest(Long memberId) {
        return expertVerificationRequestDao.existsByMemberIdAndStatus(memberId, VerificationStatus.APPROVED);
    }
}
