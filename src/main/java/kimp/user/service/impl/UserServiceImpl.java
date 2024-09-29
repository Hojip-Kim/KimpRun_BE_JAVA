package kimp.user.service.impl;

import kimp.user.dao.UserDao;
import kimp.user.dto.UserCopyDto;
import kimp.user.dto.UserDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.entity.User;
import kimp.user.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDao userDao){
        this.userDao = userDao;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public UserDto createUser(CreateUserDTO request) {

        User user = userDao.createUser(request.getLoginId(), passwordEncoder.encode(request.getPassword())); // 유저 패스워드 솔트 + 해시화

        return new UserDto(user.getLoginId(), user.getNickname());
    }


    @Override
    public UserDto getUserByLoginId(String loginId) {
        User user = userDao.findUserByLoginId(loginId);

        return new UserDto(user.getLoginId(), user.getNickname());
    }

    @Override
    public UserCopyDto createCopyUserDtoByLoginId(String loginId) {
        User user = userDao.findUserByLoginId(loginId);
        return new UserCopyDto(user.getLoginId(), user.getPassword(), user.getRole());
    }



}
