package kimp.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kimp.exception.response.ApiResponse;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dto.response.UserDto;
import kimp.user.dto.response.UserWithIdNameEmailDto;
import kimp.user.dto.request.*;
import kimp.user.dto.response.AdminResponse;
import kimp.user.dto.response.EmailVerifyCodeResponseDTO;
import kimp.user.dto.response.EmailVerifyResponseDTO;
import kimp.user.service.MemberService;
import kimp.user.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import kimp.security.user.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@Tag(name = "유저 관련 게이트웨이", description = "유저에 관련된 컨트롤러.")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @Value("${admin.url}")
    private String adminUrl;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @GetMapping
    public ApiResponse<UserDto> getMember(@AuthenticationPrincipal UserDetails UserDetails) {
        if (UserDetails == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "User not authenticated", HttpStatus.UNAUTHORIZED, "MemberController.getMember");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        GetMemberByIdVo vo = new GetMemberByIdVo(customUserDetails.getId());
        UserDto result = memberService.getmemberById(vo);
        return ApiResponse.success(result);
    }

    // 관리자 전용
    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/{id}")
    public ApiResponse<UserDto> findMemberById(@AuthenticationPrincipal UserDetails UserDetails, @PathVariable("id") long id ) throws IOException {
        GetMemberByIdVo vo = new GetMemberByIdVo(id);
        UserDto result = memberService.getmemberById(vo);
        return ApiResponse.success(result);
    }

    @PostMapping("/email/verify")
    public ApiResponse<EmailVerifyCodeResponseDTO> verifyEmailCode(@RequestBody EmailVerifyCodeRequestDTO requestDTO){
        VerifyEmailCodeVo vo = new VerifyEmailCodeVo(requestDTO.getEmail(), requestDTO.getVerifyCode());
        Boolean isVerify = this.memberService.verifyCode(vo);

        EmailVerifyCodeResponseDTO responseDTO = new EmailVerifyCodeResponseDTO();

        if(isVerify){
            responseDTO.successVerified();
        }else{
            responseDTO.failureVerified();
        }
        return ApiResponse.success(responseDTO);
    }

    @PostMapping("/email")
    public ApiResponse<EmailVerifyResponseDTO> sendEmailVerificationCode(@RequestBody EmailVerifyRequestDTO requestDTO) {
        boolean memberExists = memberService.getmemberByEmail(requestDTO.getEmail()) != null;

        EmailVerifyResponseDTO responseDTO = new EmailVerifyResponseDTO();

        if(!memberExists){
            responseDTO.setIsExisted(false);
            return ApiResponse.success(responseDTO);
        }

        SendEmailVerifyCodeVo vo = new SendEmailVerifyCodeVo(requestDTO.getEmail());
        String verifyCode = memberService.sendEmailVerifyCode(vo);

        responseDTO.setIsExisted(true);
        responseDTO.setVerificationCode(verifyCode);

        return ApiResponse.success(responseDTO);


    }

    @PostMapping("/email/new")
    public ApiResponse<EmailVerifyResponseDTO> sendEmailVerificationCodeForNew(@RequestBody EmailVerifyRequestDTO requestDTO) {

        EmailVerifyResponseDTO responseDTO = new EmailVerifyResponseDTO();

        SendEmailVerifyCodeVo vo = new SendEmailVerifyCodeVo(requestDTO.getEmail());
        String verifyCode = memberService.sendEmailVerifyCode(vo);

        responseDTO.setIsExisted(false);
        responseDTO.setVerificationCode(verifyCode);

        return ApiResponse.success(responseDTO);


    }

    @PostMapping("/sign-up")
    public ApiResponse<UserDto> createMember(@RequestBody CreateUserDTO request){

        CreateMemberVo vo = new CreateMemberVo(request);
        UserDto result = memberService.createMember(vo);
        return ApiResponse.success(result);
    }

    // MANAGER 권한 이상일 시에만 접근허용
    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/update/role")
    public ApiResponse<UserDto> updateUserRole(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody UpdateUserRoleDTO updateUserRoleDTO){
        UpdateUserRoleVo vo = new UpdateUserRoleVo(updateUserRoleDTO.getUserId(), updateUserRoleDTO.getRole().name());
        UserDto result = memberService.grantRole(vo);
        return ApiResponse.success(result);
    }

    // 로그인 한 유저가 비밀번호 업데이트하는 로직
    @PatchMapping("/update")
    public ApiResponse<UserDto> updateMember(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody UpdateUserPasswordDTO UpdateUserPasswordDTO){
        if(UpdateUserPasswordDTO == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "UpdateUserPasswordDTO cannot be null", HttpStatus.BAD_REQUEST, "MemberController.updateMember");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        UpdateMemberPasswordVo vo = new UpdateMemberPasswordVo(customUserDetails.getId(), UpdateUserPasswordDTO);
        UserDto result = memberService.updateMember(vo);
        return ApiResponse.success(result);

    }

    // 로그인 안한 유저가 비밀번호 리셋하는 로직
    @PatchMapping("/password")
    public ApiResponse<Boolean> updateMemberPassword(@RequestBody UpdateUserPasswordRequest request) {
        if(request == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "UpdateUserPasswordDTO cannot be null", HttpStatus.BAD_REQUEST, "MemberController.updateMemberPassword");
        }
        ResetPasswordVo vo = new ResetPasswordVo(request);
        boolean isSuccess = memberService.updatePassword(vo);

        return ApiResponse.success(isSuccess);
    }

    @PatchMapping("/update/nickname")
    public ApiResponse<UserWithIdNameEmailDto> updateMemberNickname(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody UpdateUserNicknameDTO UpdateUserNicknameDTO){
        if(UpdateUserNicknameDTO == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "UpdateUserNicknameDTO cannot be null", HttpStatus.BAD_REQUEST, "MemberController.updateMemberNickname");
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;
        UpdateMemberNicknameVo vo = new UpdateMemberNicknameVo(customUserDetails.getId(), UpdateUserNicknameDTO);
        UserWithIdNameEmailDto result = memberService.updateNickname(vo);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/softDelete")
    public ApiResponse<Boolean> deActivateMember(@AuthenticationPrincipal UserDetails UserDetails, DeActivateUserDTO deActivateUserDTO ) {

        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        if(deActivateUserDTO == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "DeActivateUserDTO cannot be null", HttpStatus.BAD_REQUEST, "MemberController.deActivateMember");
        }

        DeActivateMemberVo vo = new DeActivateMemberVo(customUserDetails.getId(), deActivateUserDTO);
        Boolean isSuccessDeActivate = memberService.deActivateMember(vo);
        return ApiResponse.success(isSuccessDeActivate);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'OPERATOR')")
    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteMember(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody DeleteUserDTO request) {
        if(request == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "DeleteUserDTO cannot be null", HttpStatus.BAD_REQUEST, "MemberController.deleteMember");
        }

        DeleteMemberVo vo = new DeleteMemberVo(request);
        Boolean isDeleted = memberService.deleteMember(vo);
        return ApiResponse.success(isDeleted);
    }

    @PreAuthorize("hasAuthority('OPERATOR')")
    @GetMapping("/admin")
    public ApiResponse<AdminResponse> redirectAdmin(@AuthenticationPrincipal UserDetails UserDetails, HttpServletResponse response) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;
        AdminResponse result = AdminResponse.builder()
                .response(adminUrl)
                .build();
        return ApiResponse.success(result);
    }

}
