package kimp.market.service.serviceImpl;

import kimp.common.method.DtoConverter;
import kimp.exchange.dao.ExchangeDao;
import kimp.exchange.entity.Exchange;
import kimp.market.Enum.MarketType;
import kimp.market.dao.CoinDao;
import kimp.market.dao.CoinExchangeDao;
import kimp.market.dto.coin.common.ChangeCoinDto;
import kimp.market.dto.coin.request.*;
import kimp.market.dto.coin.response.CoinResponseDto;
import kimp.market.dto.coin.response.CoinResponseWithMarketTypeDto;
import kimp.market.entity.Coin;
import kimp.market.entity.CoinExchange;
import kimp.market.service.CoinService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            exchange.addCoinExchanges(coinExchange);
        }

        return dtoConverter.convertCoinToCoinResponseDto(coin);
    }

    @Override
    public void createWithDeleteCoin(ChangeCoinDto changeCoinDto) {
        List<String> listedCoinSymbol = changeCoinDto.getListedSymbols();
        List<String> delistedCoinSymbol = changeCoinDto.getDelistedSymbols();

        List<MarketType> marketTypes = new ArrayList<>();
        List<Long> exchangeIds = new ArrayList<>();

        marketTypes.add(changeCoinDto.getType());
        Exchange exchange = exchangeDao.getExchangeByMarketType(changeCoinDto.getType());

        exchangeIds.add(exchange.getId());

        if(!listedCoinSymbol.isEmpty()){
            for(String symbol : listedCoinSymbol){
                CreateCoinDto createCoinDto = new CreateCoinDto(symbol, null, null, marketTypes);
                createCoin(createCoinDto);
            }
        }
        if(!delistedCoinSymbol.isEmpty()){
            marketTypes.add(changeCoinDto.getType());
            for(String symbol : delistedCoinSymbol){
                Coin coin = coinDao.getCoinBySymbol(symbol);
                AdjustExchangeCoinDto adjustExchangeCoinDto = new AdjustExchangeCoinDto(coin.getId(), exchangeIds);
                deleteExchangeCoin(adjustExchangeCoinDto);
            }
        }
    }

    @Override
    @Transactional
    public List<CoinResponseDto> createCoinBulk(List<CreateCoinDto> createCoinDtos) {

        // 들어온 DTO 전체에서 symbols 뽑기
        List<String > symbols = createCoinDtos
                .stream().map(CreateCoinDto::getSymlbol)
                .toList();

        // 한번에 fetch-join해서 기존코인 + 연관된 coinExchange , Exchange 모두 로드
        List<Coin> existingCoins = coinDao.findWithExchangesBySymbols(symbols);

        // 기존에 있던 심볼만 set으로
        Map<String, Coin> existingMap = existingCoins.stream()
                .collect(Collectors.toMap(Coin::getSymbol, Function.identity()));

        // 모든 MarketType에 해당하는 Exchange 한번씩 조회 (N+1방지용)
        Set<MarketType> allMarkets = createCoinDtos.stream()
                .flatMap(dto -> dto.getMarketType().stream())
                .collect(Collectors.toSet());
        List<Exchange> allExchanges = exchangeDao.getExchangeByMarketTypes(new ArrayList<>(allMarkets));
        Map<MarketType, List<Exchange>> exchangeMap = allExchanges.stream()
                .collect(Collectors.groupingBy(Exchange::getMarket));

        // 신규 생성할 DTO와 기존 업데이트 할 DTO를 분리

        List<CreateCoinDto> toCreateCoinDtos = createCoinDtos.stream()
                .filter(dto -> !existingMap.containsKey(dto.getSymlbol()))
                .toList();
        List<CreateCoinDto> toUpdate = createCoinDtos.stream()
                .filter(dto -> existingMap.containsKey(dto.getSymlbol()))
                .toList();

        // 신규 코인 생성
        List<Coin> newCoins = toCreateCoinDtos.stream()
                .map(dto -> {
                    Coin coin = new Coin(dto.getSymlbol(), dto.getName(), dto.getEnglishName());
                    dto.getMarketType().stream()
                            .flatMap(marketType -> exchangeMap.getOrDefault(marketType, List.of()).stream())
                            .forEach(exchange -> {
                                CoinExchange coinExchange = new CoinExchange(coin, exchange);
                                coin.addCoinExchanges(coinExchange);
                                exchange.addCoinExchanges(coinExchange);
                            });
                    return coin;
                }).toList();

        // 기존 Coin에 없는 CoinExchange만 붙이기 (신규마켓만 붙이기)
        // 이미 코인은 영속화된 상태이므로 더티체킹으로 처리됨. (새로운 select문 생기지않음)
        for(CreateCoinDto dto : toUpdate) {
            Coin coin = existingMap.get(dto.getSymlbol());
            Set<MarketType> existMarkets = new HashSet<>(coin.getMarketTypes());
            dto.getMarketType().stream()
                    .filter(marketType -> !existMarkets.contains(marketType))
                    .flatMap(marketType -> exchangeMap.getOrDefault(marketType, List.of()).stream())
                    .forEach(exchange -> {
                        CoinExchange coinExchange = new CoinExchange(coin, exchange);
                        coin.addCoinExchanges(coinExchange);
                        exchange.addCoinExchanges(coinExchange);
                    });
        }

        if(!newCoins.isEmpty()) {
            coinDao.createCoinBulk(newCoins);
        }

        return dtoConverter.convertCoinListToCoinResponseDtoList(
                Stream.concat(existingCoins.stream(), newCoins.stream())
                        .toList()
        );
    }

    // coin 가져올 때 select 한번으로 가져오므로 N+1 발생안함
    @Override
    public List<CoinResponseDto> getCoinsByExchangeId(long exchangeId) {

        List<Coin> coinList = coinDao.getCoinsByExchangeId(exchangeId);

        return dtoConverter.convertCoinListToCoinResponseDtoList(coinList);
    }

    @Override
    @Transactional
    public CoinResponseDto updateContentCoin(UpdateContentCoinDto updateCoinDto) {

        return dtoConverter.convertCoinToCoinResponseDto(coinDao.updateContentCoin(updateCoinDto.getId(), updateCoinDto.getContent()));

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
