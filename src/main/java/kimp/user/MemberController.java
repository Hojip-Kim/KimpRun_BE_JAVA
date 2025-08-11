package kimp.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kimp.exception.response.ApiResponse;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dto.UserDto;
import kimp.user.dto.UserWithIdNameEmailDto;
import kimp.user.dto.request.*;
import kimp.user.dto.response.AdminResponse;
import kimp.user.dto.response.EmailVerifyCodeResponseDTO;
import kimp.user.dto.response.EmailVerifyResponseDTO;
import kimp.user.entity.Member;
import kimp.user.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        Member member = memberService.getmemberById(customUserDetails.getId());
        UserDto result = memberService.convertUserToUserDto(member);
        return ApiResponse.success(result);
    }

    // 관리자 전용
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{id}")
    public ApiResponse<UserDto> findMemberById(@AuthenticationPrincipal UserDetails UserDetails, @PathVariable("id") long id ) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        Member member = memberService.getmemberById(customUserDetails.getId());
        UserDto result = memberService.convertUserToUserDto(member);
        return ApiResponse.success(result);
    }

    @PostMapping("/email/verify")
    public ApiResponse<EmailVerifyCodeResponseDTO> verifyEmailCode(@RequestBody EmailVerifyCodeRequestDTO requestDTO){
        Boolean isVerify = this.memberService.verifyCode(requestDTO.getEmail(),requestDTO.getVerifyCode());

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
        Member member = memberService.getmemberByEmail(requestDTO.getEmail());

        EmailVerifyResponseDTO responseDTO = new EmailVerifyResponseDTO();

        if(member != null){
            responseDTO.setIsExisted(true);
            return ApiResponse.success(responseDTO);
        }

        String verifyCode = memberService.sendEmailVerifyCode(requestDTO.getEmail());

        responseDTO.setIsExisted(false);
        responseDTO.setVerificationCode(verifyCode);

        return ApiResponse.success(responseDTO);


    }

    @PostMapping("/sign-up")
    public ApiResponse<UserDto> createMember(@RequestBody CreateUserDTO request){

        Member member = memberService.createMember(request);
        UserDto result = memberService.convertUserToUserDto(member);
        return ApiResponse.success(result);
    }

    // MANAGER 권한 이상일 시에만 접근허용
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/update/role")
    public ApiResponse<UserDto> updateUserRole(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody UpdateUserRoleDTO updateUserRoleDTO){
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;
        Member member = memberService.grantRole(updateUserRoleDTO.getUserId(), updateUserRoleDTO.getRole());
        UserDto result = memberService.convertUserToUserDto(member);
        return ApiResponse.success(result);
    }

    @PatchMapping("/update")
    public ApiResponse<UserDto> updateMember(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody UpdateUserPasswordDTO UpdateUserPasswordDTO){
        if(UpdateUserPasswordDTO == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "UpdateUserPasswordDTO cannot be null", HttpStatus.BAD_REQUEST, "MemberController.updateMember");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        Member member = memberService.updateMember(customUserDetails.getId(), UpdateUserPasswordDTO);
        UserDto result = memberService.convertUserToUserDto(member);
        return ApiResponse.success(result);

    }

    @PatchMapping("/update/nickname")
    public ApiResponse<UserWithIdNameEmailDto> updateMemberNickname(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody UpdateUserNicknameDTO UpdateUserNicknameDTO){
        if(UpdateUserNicknameDTO == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "UpdateUserNicknameDTO cannot be null", HttpStatus.BAD_REQUEST, "MemberController.updateMemberNickname");
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;
        Member member = memberService.updateNickname(customUserDetails.getId(), UpdateUserNicknameDTO);
        UserWithIdNameEmailDto result = new UserWithIdNameEmailDto(member.getEmail(), member.getNickname(), member.getRole().name());
        return ApiResponse.success(result);
    }

    @DeleteMapping("/softDelete")
    public ApiResponse<Boolean> deActivateMember(@AuthenticationPrincipal UserDetails UserDetails, DeActivateUserDTO deActivateUserDTO ) {

        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        if(deActivateUserDTO == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "DeActivateUserDTO cannot be null", HttpStatus.BAD_REQUEST, "MemberController.deActivateMember");
        }

        Boolean isSuccessDeActivate = memberService.deActivateMember(customUserDetails.getId(), deActivateUserDTO);
        return ApiResponse.success(isSuccessDeActivate);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'OPERATOR')")
    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteMember(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody DeleteUserDTO request) {
        if(request == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "DeleteUserDTO cannot be null", HttpStatus.BAD_REQUEST, "MemberController.deleteMember");
        }

        Boolean isDeleted = memberService.deleteMember(request);
        return ApiResponse.success(isDeleted);
    }

    @PreAuthorize("hasAuthority('OPERATOR')")
    @GetMapping("/admin")
    public ApiResponse<AdminResponse> redirectAdmin(@AuthenticationPrincipal UserDetails UserDetails, HttpServletResponse response) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;
        Member member = memberService.getmemberById(customUserDetails.getId());
        AdminResponse result = new AdminResponse(adminUrl);
        return ApiResponse.success(result);
    }

}
