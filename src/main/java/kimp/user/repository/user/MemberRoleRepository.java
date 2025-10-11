package kimp.user.repository.user;

import kimp.user.entity.MemberRole;
import kimp.user.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {

    Optional<MemberRole> findByRoleKey(String roleKey);
    
    Optional<MemberRole> findByRoleName(UserRole roleName);
    
    boolean existsByRoleKey(String roleKey);
}
