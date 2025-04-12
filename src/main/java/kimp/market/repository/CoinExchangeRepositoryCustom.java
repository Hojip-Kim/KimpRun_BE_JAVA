package kimp.market.repository;

import kimp.market.entity.CoinExchange;

import java.util.List;

public interface CoinExchangeRepositoryCustom {

    // 코인 아이디와 거래소 아이디들에 해당하는 CoinExchange를 가져오고, 이에 해당하는 Exchange까지 같이 가져오는 메소드
    // JPA의 N+1을 해결하기 위함
    List<CoinExchange> findCoinExchangeWithExchangeByCoinAndExchangeIn(Long coin, List<Long> exhangeIds);

    // 코인아이디에 해당하는 CoinExchange들을 가져오고, 이에 해당하는 거래소들을 가져오는 메서드
    // JPA의 N+1을 해결하기 위함
    public List<CoinExchange> findCoinExchangeWithExchangeByCoinId(Long coinId);

}

