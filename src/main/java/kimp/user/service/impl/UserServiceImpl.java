package kimp.user.service.impl;

import jakarta.transaction.Transactional;
import kimp.user.dao.UserDao;
import kimp.user.dto.UserCopyDto;
import kimp.user.dto.UserDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.dto.request.DeleteUserDTO;
import kimp.user.dto.request.UpdateUserDTO;
import kimp.user.entity.User;
import kimp.user.enums.UserRole;
import kimp.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder){
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
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

    @Override
    @Transactional
    public User updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User user = userDao.findUserById(id);
        boolean isMatched = isMatchedPassword(updateUserDTO.getOldPassword(), user.getPassword());
        if(!isMatched){
            throw new IllegalArgumentException("password does not match");
        }
        userDao.updateUser(user, passwordEncoder.encode(updateUserDTO.getNewPassword()));

        return user;
    }

    @Override
    public Boolean deleteUser(Long id, DeleteUserDTO deleteUserDTO) {
        User user = userDao.findUserById(id);
        boolean isMatched = isMatchedPassword(deleteUserDTO.getPassword(), user.getPassword());
        if(!isMatched){
            throw new IllegalArgumentException("password does not match");
        }
        Boolean isDeleted = userDao.deleteUser(id);
        return isDeleted;
    }

    @Override
    public UserDto convertUserToUserDto(User user) {

        return new UserDto(user.getLoginId(), user.getNickname(), user.getRole());
    }

    @Override
    public Boolean isMatchedPassword(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }

    @Override
    @Transactional
    public User grantRole(Long userId, UserRole grantRole) {
        User user = userDao.findUserById(userId);
        user.grantRole(grantRole);
        return user;
    }
}
