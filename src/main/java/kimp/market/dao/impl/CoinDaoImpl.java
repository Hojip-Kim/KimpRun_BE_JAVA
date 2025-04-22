package kimp.market.dao.impl;

import kimp.market.Enum.MarketType;
import kimp.market.dao.CoinDao;
import kimp.market.entity.Coin;
import kimp.market.entity.CoinExchange;
import kimp.market.repository.CoinRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CoinDaoImpl implements CoinDao {

    private final CoinRepository coinRepository;

    public CoinDaoImpl(CoinRepository coinRepository) {
        this.coinRepository = coinRepository;
    }

    // 단순히 Coin의 정보만 가져오는 메서드
    @Override
    public Coin findById(long id) {
        Optional<Coin> coin = coinRepository.findById(id);
        if(coin.isEmpty()) {
            throw new IllegalArgumentException("No such coin");
        }
        return coin.get();
    }

    @Override
    public List<Coin> getCoinsByExchangeId(long exchangeId) {
        List<Coin> coinList = coinRepository.findCoinsByExchange(exchangeId);

        if(coinList.isEmpty()) {
            throw new IllegalArgumentException("No such coin");
        }

        return coinList;
    }

    @Override
    public List<Coin> findWithExchangesBySymbols(List<String> symbols) {
        List<Coin> coins = coinRepository.findCoinWithExchangesBySymbols(symbols);
        return coins;
    }

    @Override
    @Transactional(readOnly = true)
    public Coin getCoinBySymbol(String symbol) {
        Coin coin = coinRepository.findBySymbol(symbol);
        if(coin == null) {
            throw new IllegalArgumentException("No such coin");
        }
        return coin;
    }

    @Override
    public List<Coin> createCoinBulk(List<Coin> coins) {
        List<Coin> coinList = coinRepository.saveAll(coins);
        if(coinList.isEmpty()) {
            throw new IllegalArgumentException("No such coin");
        }
        return coinList;
    }

    @Override
    @Transactional
    public Coin createCoin(String symbol, String name, String englishName) {
        Coin coin = new Coin(symbol, name, englishName);

        return this.coinRepository.save(coin);
    }

    @Override
    @Transactional
    public Coin updateContentCoin(long id, String content) {
        Optional<Coin> coin = coinRepository.findById(id);
        if(coin.isEmpty()) {
            throw new IllegalArgumentException("No such coin");
        }
        return coin.get().writeContent(content);
    }

    // queryDSL을 통해 exchange까지 같이 받아왔으므로 N+1발생 안함.
    @Override
    @Transactional
    public void removeExchangeIdsCoin(long coinId, List<Long> exchangeIds) {
        Optional<Coin> coin = this.coinRepository.findByIdWithExchanges(coinId);
        if(coin.isEmpty()) {
            throw new IllegalArgumentException("No such coin");
        }
        coin.get().getCoinExchanges().removeIf(e -> exchangeIds.contains(e.getExchange().getId()));
    }

    @Override
    @Transactional
    public Coin addExchangeIdsCoin(long coinId, List<CoinExchange> exchangeIds) {
        Optional<Coin> coin = this.coinRepository.findByIdWithExchanges(coinId);
        if(coin.isEmpty()) {
            throw new IllegalArgumentException("No such coin");
        }
        coin.get().getCoinExchanges().addAll(exchangeIds);
        return this.coinRepository.save(coin.get());
    }

    @Override
    public void deleteCoin(Coin coin) {
     this.coinRepository.delete(coin);
    }

    // Coin의 CoinExchange, CoinExchange의 Exchange까지 같이 불러온 데이터 호출 메서드
    @Override
    @Transactional(readOnly = true)
    public Coin findByIdWithExchanges(long id) {
        Optional<Coin> coin = coinRepository.findByIdWithExchanges(id);
        return coin.isPresent() ? coin.get() : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarketType> findMarketTypesByCoinId(long coinId) {
        List<MarketType> marketTypes = coinRepository.findMarketTypesByCoinId(coinId);
        if(marketTypes.isEmpty()){
         throw new IllegalArgumentException("Coin MarketTypes is Empty");
        }

        return marketTypes;
    }


}
