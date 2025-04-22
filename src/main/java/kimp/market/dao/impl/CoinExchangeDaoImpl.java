package kimp.market.dao.impl;

import kimp.market.dao.CoinExchangeDao;
import kimp.market.entity.CoinExchange;
import kimp.market.repository.CoinExchangeRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CoinExchangeDaoImpl implements CoinExchangeDao {

    private final CoinExchangeRepository coinExchangeRepository;

    public CoinExchangeDaoImpl(CoinExchangeRepository coinExchangeRepositoryCustom) {
        this.coinExchangeRepository = coinExchangeRepositoryCustom;
    }

    @Override
    public CoinExchange createCoinExchange(CoinExchange coinExchange) {
        return coinExchangeRepository.save(coinExchange);
    }

    @Transactional
    @Override
    public List<CoinExchange> findCoinExchangeWithExchangeByCoinIdAndExchangeIds(long coinId, List<Long> exchangeIds) {

        return coinExchangeRepository.findCoinExchangeWithExchangeByCoinAndExchangeIn(coinId, exchangeIds);

    }

    @Override
    public void deleteAllByCoinExchanges(List<CoinExchange> coinExchanges) {
        coinExchangeRepository.deleteAll(coinExchanges);
    }

    @Override
    public List<CoinExchange> findCoinExchangeWithExchangeByCoinId(Long coinId) {

        return coinExchangeRepository.findCoinExchangeWithExchangeByCoinId(coinId);
    }


}
