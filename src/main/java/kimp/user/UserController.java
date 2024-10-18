package kimp.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import kimp.security.user.CustomUserDetails;
import kimp.user.dto.UserDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.dto.request.DeleteUserDTO;
import kimp.user.dto.request.UpdateUserDTO;
import kimp.user.dto.request.UpdateUserRoleDTO;
import kimp.user.entity.User;
import kimp.user.enums.UserRole;
import kimp.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@Tag(name = "유저 관련 게이트웨이", description = "유저에 관련된.")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping()
    public UserDto getUser(@AuthenticationPrincipal UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        User user = userService.getUserById(customUserDetails.getId());

        return userService.convertUserToUserDto(user);
    }

    // 관리자 전용
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{id}")
    public UserDto findUserById(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") long id ) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        User user = userService.getUserById(customUserDetails.getId());
        return userService.convertUserToUserDto(user);
    }


    @PostMapping("/sign-up")
    public UserDto createUser(@RequestBody CreateUserDTO request){

        User user = userService.createUser(request);

        return userService.convertUserToUserDto(user);
    }

    // MANAGER 권한 이상일 시에만 접근허용
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/update/role")
    public UserDto updateUserRole(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UpdateUserRoleDTO updateUserRoleDTO){
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        User user = userService.grantRole(updateUserRoleDTO.getUserId(), updateUserRoleDTO.getRole());

        return userService.convertUserToUserDto(user);
    }

    @PatchMapping("/update")
    public UserDto updateUser(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UpdateUserDTO request){
        if(request == null) {
            throw new IllegalArgumentException("request is null");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        User user = userService.updateUser(customUserDetails.getId(), request);

        return userService.convertUserToUserDto(user);

    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteUser(@AuthenticationPrincipal UserDetails userDetails, @RequestBody DeleteUserDTO request) {
        if(request == null) {
            throw new IllegalArgumentException("request is null");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        Boolean isDeleted = userService.deleteUser(customUserDetails.getId(), request);

        if(isDeleted){
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.ok(false);
        }
    }

}
