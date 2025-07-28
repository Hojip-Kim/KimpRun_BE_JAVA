package kimp.cmc.repository.coin;

import kimp.cmc.entity.coin.CmcCoin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CmcCoinRepository extends JpaRepository<CmcCoin, Long>, CmcCoinRepositoryCustom {
}
