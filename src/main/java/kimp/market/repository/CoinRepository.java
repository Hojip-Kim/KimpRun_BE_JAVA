package kimp.market.repository;

import kimp.market.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin, Long>, CoinRepositoryCustom {
}
