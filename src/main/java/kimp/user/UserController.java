package kimp.user;

import kimp.security.user.CustomUserDetails;
import kimp.user.dto.UserDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.session.SessionRegistry;
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
    private final SessionRegistry sessionRegistry;

    public UserController(UserService userService, SessionRegistry sessionRegistry) {
        this.userService = userService;
        this.sessionRegistry = sessionRegistry;
    }
    

    @GetMapping("/test")
    public Map<String, String> redirectToHome(@AuthenticationPrincipal UserDetails userDetails) throws IOException {



        Map<String, String> testMap = new HashMap<>();


        if(userDetails != null) {
            testMap.put("result", userDetails.getUsername());
            return testMap;
        }
        else{
            testMap.put("result", "not member");
            return testMap;
        }

    }


    @PostMapping("/sign-up")
    public UserDto createUser(@RequestBody CreateUserDTO request){

        return userService.createUser(request);
    }
}
