package kimp.user;

import kimp.security.user.CustomUserDetails;
import kimp.user.dto.UserDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.entity.User;
import kimp.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/test")
    public Map<String, String> redirectToHome(@AuthenticationPrincipal UserDetails userDetails) throws IOException {

        Map<String, String> testMap = new HashMap<>();

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        if(userDetails != null) {
            testMap.put("result", customUserDetails.getEmail());
            return testMap;
        }
        else{
            testMap.put("result", "not member");
            return testMap;
        }

    }


    @PostMapping("/sign-up")
    public UserDto createUser(@RequestBody CreateUserDTO request){

        User user = userService.createUser(request);

        return new UserDto(user.getLoginId(), user.getNickname());
    }
}
