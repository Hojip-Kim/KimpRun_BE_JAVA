package kimp.user;

import kimp.user.dto.request.CreateUserDTO;
import kimp.user.dto.response.CreateUserResponseDto;
import kimp.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class.getName());

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public CreateUserResponseDto createUser(@RequestBody CreateUserDTO request){
        logger.info("user로 들어왔음");

        return this.userService.createUser(request);
    }
}
