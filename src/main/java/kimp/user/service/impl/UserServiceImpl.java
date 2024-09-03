package kimp.user.service.impl;

import kimp.user.dto.request.CreateUserDTO;
import kimp.user.dto.response.CreateUserResponseDto;
import kimp.user.entity.User;
import kimp.user.repository.UserRepository;
import kimp.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional
    public CreateUserResponseDto createUser(CreateUserDTO request) {

        if(this.userRepository.findUserByUserId(request.getUserId()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 유저입니다.");
        };

        User user = new User(request.getUserId(), request.getPassword(), request.getNickname());
        User createdUser = this.userRepository.save(user);

        return new CreateUserResponseDto(createdUser.getUserId(), createdUser.getNickname());
    }


}
