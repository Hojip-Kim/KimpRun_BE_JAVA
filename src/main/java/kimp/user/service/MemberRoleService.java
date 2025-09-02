package kimp.user.service;

import kimp.user.dto.response.MemberRoleResponseDto;
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

    // DTO 반환 메소드들 (Controller용)
    MemberRoleResponseDto createRoleDto(String roleKey, UserRole roleName);
    
    MemberRoleResponseDto getRoleByIdDto(Long id);
    
    MemberRoleResponseDto getRoleByKeyDto(String roleKey);
    
    List<MemberRoleResponseDto> getAllRolesDto();
    
    MemberRoleResponseDto updateRoleDto(Long id, UserRole roleName);
    
    // Batch methods for initialization
    void initializeUserRoles(List<UserRole> userRoles);
}