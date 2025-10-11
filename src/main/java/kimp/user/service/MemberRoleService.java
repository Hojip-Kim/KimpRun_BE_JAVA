package kimp.user.service;

import kimp.user.dto.response.MemberRoleResponseDto;
import kimp.user.entity.MemberRole;
import kimp.user.enums.UserRole;
import kimp.user.vo.*;

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
    MemberRoleResponseDto createRoleDto(CreateRoleVo vo);

    MemberRoleResponseDto getRoleByIdDto(GetRoleByIdVo vo);

    MemberRoleResponseDto getRoleByKeyDto(GetRoleByKeyVo vo);

    List<MemberRoleResponseDto> getAllRolesDto();

    MemberRoleResponseDto updateRoleDto(UpdateRoleVo vo);

    // Batch methods for initialization
    void initializeUserRoles(List<UserRole> userRoles);
}