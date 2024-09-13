package kimp.user.service;

import kimp.user.dto.UserCopyDto;
import kimp.user.dto.UserDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.dto.response.CreateUserResponseDto;
import kimp.user.entity.User;

public interface UserService {

    public UserDto createUser(CreateUserDTO request);

    public UserDto getUserByLoginId(String loginId);

    public UserCopyDto createCopyUserDtoByLoginId(String loginId);


}
