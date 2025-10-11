package kimp.user.repository.user;

import kimp.user.entity.BannedCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannedCountRepository extends JpaRepository<BannedCount, Long> {
}
