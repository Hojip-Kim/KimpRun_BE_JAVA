package kimp.user.repository;

import kimp.user.entity.Oauth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthRepository extends JpaRepository<Oauth, Long> {
}
