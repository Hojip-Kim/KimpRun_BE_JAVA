package kimp.user.dao.impl;

import kimp.user.dao.UserDao;
import kimp.user.entity.User;
import kimp.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;

    public UserDaoImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
    }

    @Override
    public User findUserById(Long id){
        Optional<User> user =  this.userRepository.findById(id);

        if(user.isEmpty()){
            throw new IllegalArgumentException("user not found");
        }

        return user.get();
    }

    @Override
    public User findUserByLoginId(String loginId){
        Optional<User> user = this.userRepository.findByLoginId(loginId);
        if(user.isEmpty()){
            throw new IllegalArgumentException("user not found");
        }
        return user.get();
    }

    @Override
    public User createUser(String loginId, String password){
        Optional<User> user = this.userRepository.findByLoginId(loginId);
        if(user.isPresent()){
            throw new IllegalArgumentException("user already exists");
        }
        return this.userRepository.save(new User(loginId, password));
    }

    @Override
    public User updateUser(User user ,String newHashedPassword) {
        user.updatePassword(newHashedPassword);

        return this.userRepository.save(user);
    }

    @Override
    public Boolean deleteUser(Long id) {
        User user = findUserById(id);
        this.userRepository.delete(user);

        User findUser = findUserById(id);
        if(findUser == null){
            return false;
        }

        return true;
    }
}
