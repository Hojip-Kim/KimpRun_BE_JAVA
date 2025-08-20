package kimp.user.dao;

import kimp.user.entity.Member;
import kimp.user.entity.Profile;

import java.util.Optional;

public interface ProfileDao {
    
    Profile save(Profile profile);
    
    Optional<Profile> findById(Long id);
    
    Optional<Profile> findByMember(Member member);
    
    void deleteById(Long id);
    
    Profile createDefaultProfile(Member member);
}
