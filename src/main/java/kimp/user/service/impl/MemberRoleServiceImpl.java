package kimp.user.service.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.MemberRoleDao;
import kimp.user.dto.response.MemberRoleResponseDto;
import kimp.user.entity.MemberRole;
import kimp.user.enums.UserRole;
import kimp.user.service.MemberRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class MemberRoleServiceImpl implements MemberRoleService {
    
    private final MemberRoleDao memberRoleDao;
    
    public MemberRoleServiceImpl(MemberRoleDao memberRoleDao) {
        this.memberRoleDao = memberRoleDao;
    }
    
    @Override
    @Transactional
    public MemberRole createRole(String roleKey, UserRole roleName) {
        if (roleKey == null || roleKey.trim().isEmpty()) {
            roleKey = UUID.randomUUID().toString();
        }
        
        if (memberRoleDao.existsByRoleKey(roleKey)) {
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_ALREADY_EXISTS_EXCEPTION, 
                "Role key already exists: " + roleKey, HttpStatus.CONFLICT, "MemberRoleServiceImpl.createRole");
        }
        
        if (roleName == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "Role name cannot be null", HttpStatus.BAD_REQUEST, "MemberRoleServiceImpl.createRole");
        }
        
        MemberRole memberRole = new MemberRole(roleKey, roleName);
        return memberRoleDao.save(memberRole);
    }
    
    @Override
    public MemberRole getRoleById(Long id) {
        return memberRoleDao.findById(id)
            .orElseThrow(() -> new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, 
                "Role not found with id: " + id, HttpStatus.NOT_FOUND, "MemberRoleServiceImpl.getRoleById"));
    }
    
    @Override
    public MemberRole getRoleByKey(String roleKey) {
        return memberRoleDao.findByRoleKey(roleKey)
            .orElseThrow(() -> new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, 
                "Role not found with key: " + roleKey, HttpStatus.NOT_FOUND, "MemberRoleServiceImpl.getRoleByKey"));
    }
    
    @Override
    public MemberRole getRoleByName(UserRole roleName) {
        return memberRoleDao.findByRoleName(roleName)
            .orElseThrow(() -> new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, 
                "Role not found with name: " + roleName, HttpStatus.NOT_FOUND, "MemberRoleServiceImpl.getRoleByName"));
    }
    
    @Override
    public List<MemberRole> getAllRoles() {
        return memberRoleDao.findAll();
    }
    
    @Override
    @Transactional
    public MemberRole updateRole(Long id, UserRole roleName) {
        MemberRole memberRole = new MemberRole(null, roleName);
        return memberRoleDao.update(id, memberRole);
    }
    
    @Override
    @Transactional
    public void deleteRole(Long id) {
        memberRoleDao.deleteById(id);
    }
    
    @Override
    public boolean existsByRoleKey(String roleKey) {
        return memberRoleDao.existsByRoleKey(roleKey);
    }
    
    @Override
    public MemberRole getDefaultUserRole() {
        return memberRoleDao.findByRoleName(UserRole.USER)
            .orElseGet(() -> {
                String defaultRoleKey = UUID.randomUUID().toString();
                MemberRole defaultRole = new MemberRole(defaultRoleKey, UserRole.USER);
                return memberRoleDao.save(defaultRole);
            });
    }

    // DTO 반환 메소드들 (Controller용)
    @Override
    @Transactional
    public MemberRoleResponseDto createRoleDto(String roleKey, UserRole roleName) {
        MemberRole memberRole = createRole(roleKey, roleName);
        return new MemberRoleResponseDto(memberRole);
    }
    
    @Override
    public MemberRoleResponseDto getRoleByIdDto(Long id) {
        MemberRole memberRole = getRoleById(id);
        return new MemberRoleResponseDto(memberRole);
    }
    
    @Override
    public MemberRoleResponseDto getRoleByKeyDto(String roleKey) {
        MemberRole memberRole = getRoleByKey(roleKey);
        return new MemberRoleResponseDto(memberRole);
    }
    
    @Override
    public List<MemberRoleResponseDto> getAllRolesDto() {
        List<MemberRole> memberRoles = getAllRoles();
        return memberRoles.stream()
            .map(MemberRoleResponseDto::new)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public MemberRoleResponseDto updateRoleDto(Long id, UserRole roleName) {
        MemberRole memberRole = updateRole(id, roleName);
        return new MemberRoleResponseDto(memberRole);
    }
    
    @Override
    @Transactional
    public void initializeUserRoles(List<UserRole> userRoles) {
        log.info("UserRole 배치 초기화 시작 - {} 개 역할", userRoles.size());
        
        // 1. 기존 모든 MemberRole을 한 번에 조회
        List<MemberRole> existingRoles = memberRoleDao.findAll();
        List<UserRole> existingRoleNames = existingRoles.stream()
            .map(MemberRole::getRoleName)
            .toList();
        
        // 2. 새로 생성해야 할 역할들 필터링
        List<UserRole> newRoles = userRoles.stream()
            .filter(role -> !existingRoleNames.contains(role))
            .toList();
            
        // 3. 배치로 새로운 MemberRole 생성
        if (!newRoles.isEmpty()) {
            for (UserRole role : newRoles) {
                String randomUuid = UUID.randomUUID().toString();
                memberRoleDao.save(new MemberRole(randomUuid, role));
            }
            
            log.info("UserRole {} 개 배치 생성 완료", newRoles.size());
        } else {
            log.info("모든 UserRole이 이미 존재함");
        }
    }
}