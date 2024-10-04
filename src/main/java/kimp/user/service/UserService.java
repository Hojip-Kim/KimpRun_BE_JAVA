package kimp.user.service;

import kimp.user.dto.UserCopyDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.entity.User;

public interface UserService {

    public User createUser(CreateUserDTO request);

    public User getUserByLoginId(String loginId);

    public UserCopyDto createCopyUserDtoByLoginId(String loginId);

    public User getUserById(Long id);


}
