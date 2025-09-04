package kimp.user.repository;

import kimp.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndIsActiveTrue(String email);
    
    Optional<Member> findByOauthProviderAndOauthProviderId(String provider, String providerId);
    Optional<Member> findByOauthProviderAndOauthProviderIdAndIsActiveTrue(String provider, String providerId);
    
    Optional<Member> findByIdAndIsActiveTrue(Long id);
    
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.role LEFT JOIN FETCH m.profile p " +
           "LEFT JOIN FETCH p.seedRange LEFT JOIN FETCH p.activityRank WHERE m.id = :id")
    Optional<Member> findByIdWithProfile(@Param("id") Long id);
    
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.role LEFT JOIN FETCH m.profile p " +
           "LEFT JOIN FETCH p.seedRange LEFT JOIN FETCH p.activityRank WHERE m.id = :id AND m.isActive = true")
    Optional<Member> findByIdWithProfileAndIsActiveTrue(@Param("id") Long id);

    @Query("""
        SELECT m FROM Member m
        LEFT JOIN FETCH m.role
        LEFT JOIN FETCH m.MemberWithdraw
        LEFT JOIN FETCH m.memberAgent ma
        LEFT JOIN FETCH ma.bannedCount
        LEFT JOIN FETCH m.profile p
        LEFT JOIN FETCH p.activityRank
        LEFT JOIN FETCH p.seedRange
        LEFT JOIN FETCH m.oauth
        WHERE m.id = :id AND m.isActive = :active
        """)
    Optional<Member> findActiveMemberForNicknameUpdateOptimized(@Param("id") Long id, @Param("active") boolean active);

    boolean existsMemberByNickname(String name);
}
