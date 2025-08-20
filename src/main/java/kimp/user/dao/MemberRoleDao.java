package kimp.user.dao;

import kimp.user.entity.MemberRole;
import kimp.user.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface MemberRoleDao {
    
    MemberRole save(MemberRole memberRole);
    
    Optional<MemberRole> findById(Long id);
    
    Optional<MemberRole> findByRoleKey(String roleKey);
    
    Optional<MemberRole> findByRoleName(UserRole roleName);
    
    List<MemberRole> findAll();
    
    void deleteById(Long id);
    
    boolean existsByRoleKey(String roleKey);
    
    MemberRole update(Long id, MemberRole memberRole);
}