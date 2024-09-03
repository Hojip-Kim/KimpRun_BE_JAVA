package kimp.user.service;

import kimp.user.dto.request.CreateUserDTO;
import kimp.user.dto.response.CreateUserResponseDto;

public interface UserService {
    public CreateUserResponseDto createUser(CreateUserDTO request);

}
