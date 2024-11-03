package kimp.user.service.impl;

import jakarta.transaction.Transactional;
import kimp.user.dao.UserDao;
import kimp.user.dto.UserCopyDto;
import kimp.user.dto.UserDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.dto.request.DeleteUserDTO;
import kimp.user.dto.request.UpdateUserNicknameDTO;
import kimp.user.dto.request.UpdateUserPasswordDTO;
import kimp.user.entity.User;
import kimp.user.enums.UserRole;
import kimp.user.service.UserService;
import kimp.user.util.NicknameGeneratorUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final NicknameGeneratorUtils nicknameGeneratorUtils;

    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, NicknameGeneratorUtils nicknameGeneratorUtils){
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.nicknameGeneratorUtils = nicknameGeneratorUtils;
    }

    @Override
    public User createUser(CreateUserDTO request) {
        User user;
        if(request.getNickname() != null || !request.getNickname().isEmpty()) {
             user = userDao.createUser(request.getEmail(), request.getNickname(), passwordEncoder.encode(request.getPassword())); // 유저 패스워드 솔트 + 해시화
        }
        else{
            user = userDao.createUser(request.getEmail(), nicknameGeneratorUtils.createRandomNickname(), passwordEncoder.encode(request.getPassword()));
        }
        return user;
    }

    @Override
    @Transactional
    public User updateNickname(Long id, UpdateUserNicknameDTO updateUserNicknameDto){
        User user = userDao.findUserById(id);

        return user.updateNickname(updateUserNicknameDto.getNickname());
    }


    @Override
    public User getUserByEmail(String email) {

        User user = userDao.findUserByEmail(email);

        return user;
    }

    // 외부 서비스에서 호출하는 메소드
    // 외부 서비스에서 객체 변경 방지를 위한 dto화
    @Override
    public UserCopyDto createCopyUserDtoByEmail(String email) {
        User user = userDao.findUserByEmail(email);
        return new UserCopyDto(user.getId(),user.getEmail(), user.getPassword(),user.getNickname(), user.getRole());
    }

    @Override
    public User getUserById(Long id) {
        User user = userDao.findUserById(id);
        return user;
    }

    @Override
    @Transactional
    public User updateUser(Long id, UpdateUserPasswordDTO updateUserPasswordDTO) {
        User user = userDao.findUserById(id);
        boolean isMatched = isMatchedPassword(updateUserPasswordDTO.getOldPassword(), user.getPassword());
        if(!isMatched){
            throw new IllegalArgumentException("password does not match");
        }
        userDao.updateUser(user, passwordEncoder.encode(updateUserPasswordDTO.getNewPassword()));

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

        return new UserDto(user.getEmail(), user.getNickname(), user.getRole());
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
