package kimp.user.dao.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.MemberRoleDao;
import kimp.user.entity.MemberRole;
import kimp.user.enums.UserRole;
import kimp.user.repository.MemberRoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRoleDaoImpl implements MemberRoleDao {
    
    private final MemberRoleRepository memberRoleRepository;
    
    public MemberRoleDaoImpl(MemberRoleRepository memberRoleRepository) {
        this.memberRoleRepository = memberRoleRepository;
    }
    
    @Override
    public MemberRole save(MemberRole memberRole) {
        if (memberRole == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "MemberRole cannot be null", HttpStatus.BAD_REQUEST, "MemberRoleDaoImpl.save");
        }
        return memberRoleRepository.save(memberRole);
    }
    
    @Override
    public Optional<MemberRole> findById(Long id) {
        if (id == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "ID cannot be null", HttpStatus.BAD_REQUEST, "MemberRoleDaoImpl.findById");
        }
        return memberRoleRepository.findById(id);
    }
    
    @Override
    public Optional<MemberRole> findByRoleKey(String roleKey) {
        if (roleKey == null || roleKey.trim().isEmpty()) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "Role key cannot be null or empty", HttpStatus.BAD_REQUEST, "MemberRoleDaoImpl.findByRoleKey");
        }
        return memberRoleRepository.findByRoleKey(roleKey);
    }
    
    @Override
    public Optional<MemberRole> findByRoleName(UserRole roleName) {
        if (roleName == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "Role name cannot be null", HttpStatus.BAD_REQUEST, "MemberRoleDaoImpl.findByRoleName");
        }
        return memberRoleRepository.findByRoleName(roleName);
    }
    
    @Override
    public List<MemberRole> findAll() {
        return memberRoleRepository.findAll();
    }
    
    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "ID cannot be null", HttpStatus.BAD_REQUEST, "MemberRoleDaoImpl.deleteById");
        }
        
        if (!memberRoleRepository.existsById(id)) {
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, 
                "MemberRole not found with id: " + id, HttpStatus.NOT_FOUND, "MemberRoleDaoImpl.deleteById");
        }
        
        memberRoleRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByRoleKey(String roleKey) {
        if (roleKey == null || roleKey.trim().isEmpty()) {
            return false;
        }
        return memberRoleRepository.existsByRoleKey(roleKey);
    }
    
    @Override
    public MemberRole update(Long id, MemberRole memberRole) {
        if (id == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "ID cannot be null", HttpStatus.BAD_REQUEST, "MemberRoleDaoImpl.update");
        }
        
        if (memberRole == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "MemberRole cannot be null", HttpStatus.BAD_REQUEST, "MemberRoleDaoImpl.update");
        }
        
        MemberRole existingRole = memberRoleRepository.findById(id)
            .orElseThrow(() -> new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, 
                "MemberRole not found with id: " + id, HttpStatus.NOT_FOUND, "MemberRoleDaoImpl.update"));
        
        return existingRole.updateRole(memberRole.getRoleName());
    }
}