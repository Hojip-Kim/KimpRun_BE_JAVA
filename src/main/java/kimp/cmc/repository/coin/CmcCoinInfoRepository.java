package kimp.cmc.repository.coin;

import kimp.cmc.entity.coin.CmcCoinInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CmcCoinInfoRepository extends JpaRepository<CmcCoinInfo, Long> {
}
