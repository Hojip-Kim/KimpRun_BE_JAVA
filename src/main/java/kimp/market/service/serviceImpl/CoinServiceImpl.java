package kimp.market.service.serviceImpl;

import kimp.common.method.DtoConverter;
import kimp.exchange.dao.ExchangeDao;
import kimp.exchange.entity.Exchange;
import kimp.market.Enum.MarketType;
import kimp.market.dao.CoinDao;
import kimp.market.dao.CoinExchangeDao;
import kimp.market.dto.coin.request.*;
import kimp.market.dto.coin.response.CoinResponseDto;
import kimp.market.dto.coin.response.CoinResponseWithMarketTypeDto;
import kimp.market.entity.Coin;
import kimp.market.entity.CoinExchange;
import kimp.market.service.CoinService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoinServiceImpl implements CoinService {

    private final CoinDao coinDao;
    private final DtoConverter dtoConverter;
    private final ExchangeDao exchangeDao;
    private final CoinExchangeDao coinExchangeDao;
    public CoinServiceImpl(CoinDao coinDao, DtoConverter dtoConverter, ExchangeDao exchangeDao, CoinExchangeDao coinExchangeDao) {
        this.coinDao = coinDao;
        this.dtoConverter = dtoConverter;
        this.exchangeDao = exchangeDao;
        this.coinExchangeDao = coinExchangeDao;
    }


    @Override
    @Transactional
    public CoinResponseWithMarketTypeDto getCoinByID(long id) {
        Coin coin = coinDao.findByIdWithExchanges(id);
        return dtoConverter.convertCoinToCoinResponseWithMarketTypeDto(coin);
    }

    @Override
    @Transactional
    public CoinResponseDto createCoin(CreateCoinDto createCoinDto) {

        Coin coin = coinDao.createCoin(createCoinDto.getSymlbol(), createCoinDto.getName(), createCoinDto.getEnglishName());

        List<MarketType> marketTypeList = createCoinDto.getMarketType();

        List<Exchange> exchanges = exchangeDao.getExchangeByMarketTypes(marketTypeList);

        for(Exchange exchange : exchanges) {
            CoinExchange coinExchange = new CoinExchange(coin, exchange);
            coin.addCoinExchanges(coinExchange);
            coinExchange.getExchange().addCoinExchanges(coinExchange);
        }

        return dtoConverter.convertCoinToCoinResponseDto(coin);
    }

    @Override
    @Transactional
    public CoinResponseDto updateContentCoin(UpdateContentCoinDto updateCoinDto) {
        Coin coin = coinDao.findById(updateCoinDto.getId());

        return dtoConverter.convertCoinToCoinResponseDto(coin.writeContent(updateCoinDto.getContent()));

    }

    // exchanges를 가져오는 떄에 CoinExchange, 이 CoinExchange에 해당하는 Coin까지 같이 fetch했으므로 N+1 발생하지않음.
    @Override
    @Transactional
    public CoinResponseDto addExchangeCoin(AdjustExchangeCoinDto addExchangeCoin) {
        Coin coin = coinDao.findByIdWithExchanges(addExchangeCoin.getCoinId());

        List<Exchange> exchanges = exchangeDao.getExchangesAndCoinExchangesByIds(addExchangeCoin.getCoinId(), addExchangeCoin.getExchangeIds());

        for(Exchange exchange : exchanges) {
            CoinExchange coinExchange = new CoinExchange(coin, exchange);
            coin.addCoinExchanges(coinExchange);
            exchange.addCoinExchanges(coinExchange);
        }

        return dtoConverter.convertCoinToCoinResponseDto(coin);
    }



    // 이 코인의 exchange만 지우는것임.
    // 즉, 코인에 해당하는 특정 exchange를 지우는 것.
    // 코인 자체를 지우는것이 아님.
    @Override
    @Transactional
    public void deleteExchangeCoin(AdjustExchangeCoinDto deleteExchangeCoin) {
        List<CoinExchange> coinExchanges = coinExchangeDao.findCoinExchangeWithExchangeByCoinIdAndExchangeIds(deleteExchangeCoin.getCoinId(), deleteExchangeCoin.getExchangeIds());

        List<CoinExchange> collectRemovedCoinExchanges = new ArrayList<>();

        for(CoinExchange coinExchange : coinExchanges) {
            coinExchange.getCoin().removeCoinExchange(coinExchange);
            coinExchange.getExchange().removeCoinExchanges(coinExchange);
            collectRemovedCoinExchanges.add(coinExchange);
        }
        coinExchangeDao.deleteAllByCoinExchanges(collectRemovedCoinExchanges);
    }

    @Override
    @Transactional
    public CoinResponseDto updateCoin(UpdateCoinDto updateCoinDto) {
        Coin coin = coinDao.findById(updateCoinDto.getId());
        Coin updatedCoin = coin.updateContent(updateCoinDto.getContent()).updateName(updateCoinDto.getName()).updateSymbol(updateCoinDto.getSymbol()).updateEnglishName(updateCoinDto.getEnglishName());
        return dtoConverter.convertCoinToCoinResponseDto(updatedCoin);
    }

    @Override
    @Transactional
    public void deleteCoin(DeleteCoinDto deleteCoinDto) {
        Coin coin = coinDao.findById(deleteCoinDto.getId());

        List<CoinExchange> coinExchanges = coinExchangeDao.findCoinExchangeWithExchangeByCoinId(deleteCoinDto.getId());
        for(CoinExchange coinExchange : coinExchanges) {
            coinExchange.getExchange().removeCoinExchanges(coinExchange);
        }

        coinDao.deleteCoin(coin);

    }
}
