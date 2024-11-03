package kimp.user.dao;

import kimp.user.entity.User;

public interface UserDao {

    public User findUserById(Long id);
    public User findUserByEmail(String email);
    public User createUser(String email,String nickname,  String password);
    public User updateUser(User user, String newHashedPassword);
    public Boolean deleteUser(Long id);
}
