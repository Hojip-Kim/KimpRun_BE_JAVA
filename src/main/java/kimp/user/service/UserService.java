package kimp.user.service;

import kimp.user.dto.UserCopyDto;
import kimp.user.dto.UserDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.dto.request.DeleteUserDTO;
import kimp.user.dto.request.UpdateUserDTO;
import kimp.user.entity.User;
import kimp.user.enums.UserRole;

public interface UserService {

    public User createUser(CreateUserDTO request);

    public User getUserByLoginId(String loginId);

    public UserCopyDto createCopyUserDtoByLoginId(String loginId);

    public User getUserById(Long id);

    public User updateUser(Long id,UpdateUserDTO updateUserDTO);

    public Boolean deleteUser(Long id, DeleteUserDTO deleteUserDTO);

    public UserDto convertUserToUserDto(User user);

    public Boolean isMatchedPassword(String password, String hashedPassword);

    public User grantRole(Long userId, UserRole grantRole);
}
