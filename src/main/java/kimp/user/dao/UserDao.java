package kimp.user.dao;

import kimp.user.entity.User;

public interface UserDao {

    public User findUserById(Long id);
    public User findUserByLoginId(String loginId);
    public User createUser(String loginId, String password);
}
