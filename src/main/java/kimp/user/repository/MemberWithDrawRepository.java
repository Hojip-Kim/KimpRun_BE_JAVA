package kimp.user.repository;

import kimp.user.entity.MemberWithdraw;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberWithDrawRepository extends JpaRepository<MemberWithdraw, Long> {
}
