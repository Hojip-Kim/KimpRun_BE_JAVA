package kimp.user.repository.user;

import kimp.user.entity.Oauth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OauthRepository extends JpaRepository<Oauth, Long> {
    
    @Query("SELECT o FROM Oauth o WHERE o.expiresAt < :expirationThreshold AND o.refreshToken IS NOT NULL")
    List<Oauth> findByExpiresAtBefore(@Param("expirationThreshold") LocalDateTime expirationThreshold);
}
