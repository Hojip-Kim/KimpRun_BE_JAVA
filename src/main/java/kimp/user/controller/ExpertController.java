package kimp.user.controller;

import jakarta.validation.Valid;
import kimp.exception.response.ApiResponse;
import kimp.user.dto.request.ExpertVerificationRequestDto;
import kimp.user.dto.request.ExpertVerificationReviewRequestDto;
import kimp.user.dto.request.ExpertProfileUpdateRequestDto;
import kimp.user.dto.response.ExpertVerificationResponseDto;
import kimp.user.dto.response.ExpertProfileResponseDto;
import kimp.user.enums.VerificationStatus;
import kimp.user.service.expert.ExpertVerificationService;
import kimp.user.service.expert.ExpertProfileService;
import kimp.user.vo.*;
import kimp.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/community/expert")
@RequiredArgsConstructor
public class ExpertController {

    private final ExpertVerificationService expertVerificationService;
    private final ExpertProfileService expertProfileService;

    /**
     * 전문가 신청 생성
     */
    @PostMapping("/applications")
    public ApiResponse<ExpertVerificationResponseDto> createApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ExpertVerificationRequestDto requestDto
    ) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        long memberId = customUserDetails.getId();

        CreateExpertVerificationRequestVo vo = new CreateExpertVerificationRequestVo(memberId, requestDto);
        ExpertVerificationResponseDto response = expertVerificationService.createVerificationRequest(vo);
        return ApiResponse.success(HttpStatus.CREATED, response);
    }

    /**
     * 전문가 신청 수정
     */
    @PutMapping("/applications/{verificationRequestId}")
    public ApiResponse<ExpertVerificationResponseDto> updateApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long verificationRequestId,
            @Valid @RequestBody ExpertVerificationRequestDto requestDto
    ) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        long memberId = customUserDetails.getId();

        UpdateExpertVerificationRequestVo vo = new UpdateExpertVerificationRequestVo(memberId, verificationRequestId, requestDto);
        ExpertVerificationResponseDto response = expertVerificationService.updateVerificationRequest(vo);
        return ApiResponse.success(response);
    }

    /**
     * 전문가 신청 삭제 (취소)
     */
    @DeleteMapping("/applications/{verificationRequestId}")
    public ApiResponse<Void> deleteApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long verificationRequestId
    ) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        long memberId = customUserDetails.getId();

        DeleteExpertVerificationRequestVo vo = new DeleteExpertVerificationRequestVo(memberId, verificationRequestId);
        expertVerificationService.deleteVerificationRequest(vo);
        return ApiResponse.success(null);
    }

    /**
     * 전문가 신청 단건 조회
     */
    @GetMapping("/applications/{verificationRequestId}")
    public ApiResponse<ExpertVerificationResponseDto> getApplication(
            @PathVariable Long verificationRequestId
    ) {
        return expertVerificationService.getVerificationRequestById(verificationRequestId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Not Found", null));
    }

    /**
     * 내 전문가 신청 목록 조회
     */
    @GetMapping("/applications/my")
    public ApiResponse<Page<ExpertVerificationResponseDto>> getMyApplications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        long memberId = customUserDetails.getId();

        GetExpertVerificationRequestsByMemberVo vo = new GetExpertVerificationRequestsByMemberVo(memberId, page, size);
        Page<ExpertVerificationResponseDto> response = expertVerificationService.getVerificationRequestsByMember(vo);
        return ApiResponse.success(response);
    }

    /**
     * 관리자: 상태별 전문가 신청 목록 조회
     */
    @GetMapping("/admin/applications")
    public ApiResponse<Page<ExpertVerificationResponseDto>> getApplicationsByStatus(
            @RequestParam(required = false) VerificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExpertVerificationResponseDto> response;

        if (status != null) {
            response = expertVerificationService.getVerificationRequestsByStatus(status, pageable);
        } else {
            response = expertVerificationService.getAllVerificationRequests(pageable);
        }

        return ApiResponse.success(response);
    }

    /**
     * 관리자: 전문가 신청 승인
     */
    @PostMapping("/admin/applications/{verificationRequestId}/approve")
    public ApiResponse<ExpertVerificationResponseDto> approveApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long verificationRequestId
    ) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        long reviewerId = customUserDetails.getId();

        ReviewExpertVerificationRequestVo vo = new ReviewExpertVerificationRequestVo(reviewerId, verificationRequestId, null);
        ExpertVerificationResponseDto response = expertVerificationService.approveVerificationRequest(vo);
        return ApiResponse.success(response);
    }

    /**
     * 관리자: 전문가 신청 거부
     */
    @PostMapping("/admin/applications/{verificationRequestId}/reject")
    public ApiResponse<ExpertVerificationResponseDto> rejectApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long verificationRequestId,
            @Valid @RequestBody ExpertVerificationReviewRequestDto requestDto
    ) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        long reviewerId = customUserDetails.getId();

        ReviewExpertVerificationRequestVo vo = new ReviewExpertVerificationRequestVo(reviewerId, verificationRequestId, requestDto.getRejectionReason());
        ExpertVerificationResponseDto response = expertVerificationService.rejectVerificationRequest(vo);
        return ApiResponse.success(response);
    }

    /**
     * 전문가 프로필 조회
     */
    @GetMapping("/profiles/{memberId}")
    public ApiResponse<ExpertProfileResponseDto> getProfile(
            @PathVariable Long memberId
    ) {
        return expertProfileService.getProfileByMemberId(memberId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Not Found", null));
    }

    /**
     * 내 전문가 프로필 조회
     */
    @GetMapping("/profiles/my")
    public ApiResponse<ExpertProfileResponseDto> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        long memberId = customUserDetails.getId();

        return expertProfileService.getProfileByMemberId(memberId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Not Found", null));
    }

    /**
     * 활성 전문가 프로필 목록 조회
     */
    @GetMapping("/profiles")
    public ApiResponse<Page<ExpertProfileResponseDto>> getActiveProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExpertProfileResponseDto> response = expertProfileService.getActiveProfiles(pageable);
        return ApiResponse.success(response);
    }

    /**
     * 내 전문가 프로필 수정
     */
    @PutMapping("/profiles/my")
    public ApiResponse<ExpertProfileResponseDto> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ExpertProfileUpdateRequestDto requestDto
    ) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        long memberId = customUserDetails.getId();

        UpdateExpertProfileVo vo = new UpdateExpertProfileVo(memberId, requestDto);
        ExpertProfileResponseDto response = expertProfileService.updateMyProfile(vo);
        return ApiResponse.success(response);
    }

    /**
     * 관리자: 전체 전문가 프로필 목록 조회 (활성/비활성 포함)
     */
    @GetMapping("/admin/profiles")
    public ApiResponse<Page<ExpertProfileResponseDto>> getAllProfiles(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExpertProfileResponseDto> response;

        if (isActive != null && isActive) {
            response = expertProfileService.getActiveProfiles(pageable);
        } else {
            response = expertProfileService.getAllProfiles(pageable);
        }

        return ApiResponse.success(response);
    }

    /**
     * 관리자: 전문가 프로필 활성화
     */
    @PostMapping("/admin/profiles/{profileId}/activate")
    public ApiResponse<ExpertProfileResponseDto> activateProfile(
            @PathVariable Long profileId
    ) {
        ExpertProfileResponseDto response = expertProfileService.activateProfile(profileId);
        return ApiResponse.success(response);
    }

    /**
     * 관리자: 전문가 프로필 비활성화 (프로필 ID로)
     */
    @PostMapping("/admin/profiles/{profileId}/deactivate")
    public ApiResponse<ExpertProfileResponseDto> deactivateProfile(
            @PathVariable Long profileId
    ) {
        ExpertProfileResponseDto response = expertProfileService.deactivateProfile(profileId);
        return ApiResponse.success(response);
    }

    /**
     * 관리자: 전문가 자격 취소 (회원 ID로)
     */
    @PostMapping("/admin/members/{memberId}/revoke")
    public ApiResponse<ExpertProfileResponseDto> revokeExpertStatus(
            @PathVariable Long memberId
    ) {
        ExpertProfileResponseDto response = expertProfileService.deactivateProfileByMemberId(memberId);
        return ApiResponse.success(response);
    }

    /**
     * 관리자: 특정 회원의 전문가 프로필 조회
     */
    @GetMapping("/admin/members/{memberId}/profile")
    public ApiResponse<ExpertProfileResponseDto> getProfileByMemberIdForAdmin(
            @PathVariable Long memberId
    ) {
        return expertProfileService.getProfileByMemberId(memberId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Not Found", null));
    }
}
