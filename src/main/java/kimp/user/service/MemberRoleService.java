package kimp.user.service;

import kimp.user.entity.MemberRole;
import kimp.user.enums.UserRole;

import java.util.List;

public interface MemberRoleService {
    
    MemberRole createRole(String roleKey, UserRole roleName);
    
    MemberRole getRoleById(Long id);
    
    MemberRole getRoleByKey(String roleKey);
    
    MemberRole getRoleByName(UserRole roleName);
    
    List<MemberRole> getAllRoles();
    
    MemberRole updateRole(Long id, UserRole roleName);
    
    void deleteRole(Long id);
    
    boolean existsByRoleKey(String roleKey);
    
    MemberRole getDefaultUserRole();
}