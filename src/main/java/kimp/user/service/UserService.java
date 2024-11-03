package kimp.user.service;

import kimp.user.dto.UserCopyDto;
import kimp.user.dto.UserDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.dto.request.DeleteUserDTO;
import kimp.user.dto.request.UpdateUserNicknameDTO;
import kimp.user.dto.request.UpdateUserPasswordDTO;
import kimp.user.entity.User;
import kimp.user.enums.UserRole;

public interface UserService {

    public User createUser(CreateUserDTO request);

    public User getUserByEmail(String email);

    public UserCopyDto createCopyUserDtoByEmail(String email);

    public User getUserById(Long id);

    public User updateUser(Long id, UpdateUserPasswordDTO updateUserPasswordDTO);

    public User updateNickname(Long id, UpdateUserNicknameDTO updateUserNicknameDto);

    public Boolean deleteUser(Long id, DeleteUserDTO deleteUserDTO);

    public UserDto convertUserToUserDto(User user);

    public Boolean isMatchedPassword(String password, String hashedPassword);

    public User grantRole(Long userId, UserRole grantRole);
}
