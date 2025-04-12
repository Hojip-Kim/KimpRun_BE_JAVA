package kimp.market.repository;

import kimp.market.entity.CoinExchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinExchangeRepository extends JpaRepository<CoinExchange, Long>, CoinExchangeRepositoryCustom {


}
