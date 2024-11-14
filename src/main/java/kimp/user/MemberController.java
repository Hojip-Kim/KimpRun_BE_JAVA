package kimp.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kimp.member.dto.response.AdminResponse;
import kimp.user.dto.UserDto;
import kimp.user.dto.UserWithIdNameEmailDto;
import kimp.user.dto.request.*;
import kimp.user.entity.Member;
import kimp.user.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import kimp.security.user.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@Tag(name = "유저 관련 게이트웨이", description = "유저에 관련된.")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @Value("${admin.url}")
    private String adminUrl;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @GetMapping()
    public UserDto getmember(@AuthenticationPrincipal UserDetails UserDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;


        Member member = memberService.getmemberById(customUserDetails.getId());

        return memberService.convertUserToUserDto(member);
    }

    // 관리자 전용
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{id}")
    public UserDto findMemberById(@AuthenticationPrincipal UserDetails UserDetails, @PathVariable("id") long id ) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        Member member = memberService.getmemberById(customUserDetails.getId());
        return memberService.convertUserToUserDto(member);
    }


    @PostMapping("/sign-up")
    public UserDto createMember(@RequestBody CreateUserDTO request){

        Member member = memberService.createMember(request);

        return memberService.convertUserToUserDto(member);
    }

    // MANAGER 권한 이상일 시에만 접근허용
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/update/role")
    public UserDto updateUserRole(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody UpdateUserRoleDTO updateUserRoleDTO){
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;
        Member member = memberService.grantRole(updateUserRoleDTO.getUserId(), updateUserRoleDTO.getRole());

        return memberService.convertUserToUserDto(member);
    }

    @PatchMapping("/update")
    public UserDto updateMember(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody UpdateUserPasswordDTO UpdateUserPasswordDTO){
        if(UpdateUserPasswordDTO == null) {
            throw new IllegalArgumentException("request is null");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        Member member = memberService.updateMember(customUserDetails.getId(), UpdateUserPasswordDTO);

        return memberService.convertUserToUserDto(member);

    }

    @PatchMapping("/update/nickname")
    public UserWithIdNameEmailDto updateMemberNickname(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody UpdateUserNicknameDTO UpdateUserNicknameDTO){
        if(UpdateUserNicknameDTO == null) {
            throw new IllegalArgumentException("UpdateUserNicknameDTO is null");
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;
        Member member = memberService.updateNickname(customUserDetails.getId(), UpdateUserNicknameDTO);

        return new UserWithIdNameEmailDto(member.getEmail(), member.getNickname(), member.getRole().name());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deletemember(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody DeleteUserDTO request) {
        if(request == null) {
            throw new IllegalArgumentException("request is null");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        Boolean isDeleted = memberService.deletemember(customUserDetails.getId(), request);

        if(isDeleted){
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.ok(false);
        }
    }

    @PreAuthorize("hasAuthority('OPERATOR')")
    @GetMapping("/admin")
    public AdminResponse redirectAdmin(@AuthenticationPrincipal UserDetails UserDetails, HttpServletResponse response) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;
        Member member = memberService.getmemberById(customUserDetails.getId());
        return new AdminResponse(adminUrl);
    }

}
