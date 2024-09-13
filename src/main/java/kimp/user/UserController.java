package kimp.user;

import kimp.security.user.CustomUserDetails;
import kimp.user.dto.UserDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.dto.response.CreateUserResponseDto;
import kimp.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/loginTest")
    public Map<String, String> test(@AuthenticationPrincipal CustomUserDetails userDetails) {

        Map<String, String> testMap = new HashMap<>();
        testMap.put("result", "Welcome, " + userDetails.getUsername());

        return testMap;

    }


    @PostMapping("/sign-up")
    public UserDto createUser(@RequestBody CreateUserDTO request){

        return userService.createUser(request);
    }
}
