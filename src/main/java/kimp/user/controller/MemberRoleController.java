package kimp.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kimp.exception.response.ApiResponse;
import kimp.user.dto.request.CreateRoleRequestDto;
import kimp.user.dto.request.UpdateRoleRequestDto;
import kimp.user.dto.response.MemberRoleResponseDto;
import kimp.user.service.MemberRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@Tag(name = "권한 관리", description = "사용자 권한 역할 관리 컨트롤러")
public class MemberRoleController {
    
    private final MemberRoleService memberRoleService;
    
    public MemberRoleController(MemberRoleService memberRoleService) {
        this.memberRoleService = memberRoleService;
    }
    
    @PreAuthorize("hasAuthority('OPERATOR')")
    @PostMapping
    public ApiResponse<MemberRoleResponseDto> createRole(@RequestBody CreateRoleRequestDto request) {
        MemberRoleResponseDto response = memberRoleService.createRoleDto(request.getRoleKey(), request.getRoleName());
        return ApiResponse.success(response);
    }
    
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OPERATOR')")
    @GetMapping("/{id}")
    public ApiResponse<MemberRoleResponseDto> getRoleById(@PathVariable Long id) {
        MemberRoleResponseDto response = memberRoleService.getRoleByIdDto(id);
        return ApiResponse.success(response);
    }
    
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OPERATOR')")
    @GetMapping("/key/{roleKey}")
    public ApiResponse<MemberRoleResponseDto> getRoleByKey(@PathVariable String roleKey) {
        MemberRoleResponseDto response = memberRoleService.getRoleByKeyDto(roleKey);
        return ApiResponse.success(response);
    }
    
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OPERATOR')")
    @GetMapping
    public ApiResponse<List<MemberRoleResponseDto>> getAllRoles() {
        List<MemberRoleResponseDto> response = memberRoleService.getAllRolesDto();
        return ApiResponse.success(response);
    }
    
    @PreAuthorize("hasAuthority('OPERATOR')")
    @PutMapping("/{id}")
    public ApiResponse<MemberRoleResponseDto> updateRole(@PathVariable Long id, 
                                                        @RequestBody UpdateRoleRequestDto request) {
        MemberRoleResponseDto response = memberRoleService.updateRoleDto(id, request.getRoleName());
        return ApiResponse.success(response);
    }
    
    @PreAuthorize("hasAuthority('OPERATOR')")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteRole(@PathVariable Long id) {
        memberRoleService.deleteRole(id);
        return ApiResponse.success(true);
    }
}