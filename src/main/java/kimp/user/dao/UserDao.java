package kimp.user.dao;

import kimp.user.entity.User;
import kimp.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class UserDao {

    private final UserRepository userRepository;

    public UserDao(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserById(Long id){
        Optional<User> user =  this.userRepository.findById(id);

        if(user.isEmpty()){
            throw new IllegalArgumentException("user not found");
        }

        return user.get();
    }

    public User findUserByLoginId(String loginId){
        Optional<User> user = this.userRepository.findByLoginId(loginId);
        if(user.isEmpty()){
            throw new IllegalArgumentException("user not found");
        }
        return user.get();
    }

    public User createUser(String loginId, String password){
        Optional<User> user = this.userRepository.findByLoginId(loginId);
        if(user.isPresent()){
            throw new IllegalArgumentException("user already exists");
        }
        return this.userRepository.save(new User(loginId, password));
    }

}
