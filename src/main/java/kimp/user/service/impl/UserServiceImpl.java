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

    public User createUser(CreateUserDTO request) {

        User user = userDao.createUser(request.getLoginId(), passwordEncoder.encode(request.getPassword())); // 유저 패스워드 솔트 + 해시화

        return user;
    }


    @Override
    public User getUserByLoginId(String loginId) {

        User user = userDao.findUserByLoginId(loginId);

        return user;
    }

    // 외부 서비스에서 호출하는 메소드
    // 외부 서비스에서 객체 변경 방지를 위한 dto화
    @Override
    public UserCopyDto createCopyUserDtoByLoginId(String loginId) {
        User user = userDao.findUserByLoginId(loginId);
        return new UserCopyDto(user.getId(),user.getEmail(),user.getLoginId(), user.getPassword(), user.getRole());
    }

    @Override
    public User getUserById(Long id) {
        User user = userDao.findUserById(id);
        return user;
    }
}
