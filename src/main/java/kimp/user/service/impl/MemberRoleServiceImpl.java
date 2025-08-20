package kimp.user.service.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.MemberRoleDao;
import kimp.user.entity.MemberRole;
import kimp.user.enums.UserRole;
import kimp.user.service.MemberRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
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
}