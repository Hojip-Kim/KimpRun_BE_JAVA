package kimp.user.controller;

import kimp.chat.service.ChatTrackingService;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.exception.response.ApiResponse;
import kimp.security.user.CustomUserDetails;
import kimp.user.dto.request.*;
import kimp.user.dto.response.AnnonymousMemberResponseDto;
import kimp.user.dto.response.UpdateAnonNicknameResponse;
import kimp.user.service.AnnonyMousService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/anonymous/member")
public class AnnonyMousMemberController {

    private final AnnonyMousService annonyMousService;
    private final ChatTrackingService chatTrackingService;

    public AnnonyMousMemberController(AnnonyMousService annonyMousService, ChatTrackingService chatTrackingService) {
        this.annonyMousService = annonyMousService;
        this.chatTrackingService = chatTrackingService;
    }

    @PreAuthorize("hasAuthority('OPERATOR')")
    @PostMapping("/info")
    public ApiResponse<AnnonymousMemberResponseDto> getAnnonymousMemberInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestBody AnnonymousMemberInfoRequestDto request) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        if(request == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "AnnonymousMemberInfoRequestDto cannot be null", HttpStatus.BAD_REQUEST, "AnnonyMousMemberController.getAnnonymousMemberInfo");
        }

        AnnonymousMemberResponseDto annonymousMemberResponseDto = annonyMousService.getAnnonymousMemberByUuidOrIp(request);


        return ApiResponse.success(annonymousMemberResponseDto);
    }

    @PreAuthorize("hasAuthority('OPERATOR')")
    @PostMapping("/application/ban")
    public ApiResponse<Void> applicationBanMember(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ApplicationBanMemberRequestDto request) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        if(request == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "ApplicationBanMemberRequestDto cannot be null", HttpStatus.BAD_REQUEST, "AnnonyMousMemberController.applicationBanMember");
        }

        annonyMousService.applicationBanMember(request);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasAuthority('OPERATOR')")
    @PostMapping("/application/unban")
    public ApiResponse<Void> applicationUnBanMember(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ApplicationUnBanMemberRequestDto request) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        if(request == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "ApplicationUnBanMemberRequestDto cannot be null", HttpStatus.BAD_REQUEST, "AnnonyMousMemberController.applicationUnBanMember");
        }

        annonyMousService.applicationUnBanMember(request);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasAuthority('OPERATOR')")
    @PostMapping("/cdn/ban")
    public ApiResponse<Void> cdnBanMember(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CdnBanMemberRequestDto request) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        if(request == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "ApplicationBanMemberRequestDto cannot be null", HttpStatus.BAD_REQUEST, "AnnonyMousMemberController.cdnBanMember");
        }

        annonyMousService.cdnBanMember(request);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasAuthority('OPERATOR')")
    @PostMapping("/cdn/unban")
    public ApiResponse<Void> cdnUnBanMember(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CdnUnbanMemberRequestDto request) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        if(request == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "ApplicationBanMemberRequestDto cannot be null", HttpStatus.BAD_REQUEST, "AnnonyMousMemberController.cdnUnBanMember");
        }

        annonyMousService.cdnUnBanMember(request);
        return ApiResponse.success(null);
    }

    /**
     *
     * @param request
     * @return email: string;
     *   name: string;
     *   role: string;
     *   memberId: number;
     */
    @PutMapping("/nickname")
    public ApiResponse<UpdateAnonNicknameResponse> updateAnonNickname(@RequestBody UpdateAnonNicknameRequestDto request) {

        if(request == null || request.getUuid() == null || request.getNickname() == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "UUID and nickname cannot be null", HttpStatus.BAD_REQUEST, "AnnonyMousMemberController.updateAnonNickname");
        }

        UpdateAnonNicknameResponse updateAnonNicknameResponse = chatTrackingService.createOrUpdateChatTracking(request.getUuid(), request.getNickname(), null, false);
        return ApiResponse.success(updateAnonNicknameResponse);
    }

}
