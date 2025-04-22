package kimp.market.service.serviceImpl;

import jakarta.annotation.PostConstruct;
import kimp.exchange.service.ExchangeService;
import kimp.market.Enum.MarketType;
import kimp.market.dto.coin.common.ServiceCoinDto;
import kimp.market.dto.coin.common.ServiceCoinWrapperDto;
import kimp.market.dto.coin.request.CreateCoinDto;
import kimp.market.dto.coin.response.CoinResponseDto;
import kimp.market.service.CoinService;
import kimp.market.service.MarketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoinExchangePacadeService {

    private final CoinService coinService;
    private final ExchangeService exchangeService;
    private final MarketService marketService;

    public CoinExchangePacadeService(CoinService coinService, ExchangeService exchangeService, MarketService marketService) {
        this.coinService = coinService;
        this.exchangeService = exchangeService;
        this.marketService = marketService;
    }
    @PostConstruct
    private void coinExchangePacadeServiceInit(){
        MarketType[] marketTypes = MarketType.values();

        for(MarketType marketType : marketTypes){
            List<MarketType> marketTypeList = new ArrayList<>();
            marketTypeList.add(marketType);
            List<ServiceCoinDto> serviceCoinDtoList = getCoinsByExchange(marketType);
            if(serviceCoinDtoList != null){
                createCoinBulk(marketTypeList, serviceCoinDtoList);
            }

        }

    }

    // 코인을 마켓타입에 따라 연관관계 매핑을 합니다.
    // Exchange가 먼저 데이터베이스에 적재되어있어야 합니다.
    @Transactional
    public List<CoinResponseDto> createCoinBulk(List<MarketType> marketTypes,List<ServiceCoinDto> serviceCoinDtos){
        List<CreateCoinDto> createCoinDtos = new ArrayList<>();

        for(ServiceCoinDto serviceCoinDto : serviceCoinDtos){
            CreateCoinDto createCoinDto = new CreateCoinDto(serviceCoinDto.getSymbol(), marketTypes);
            createCoinDtos.add(createCoinDto);
        }

        List<CoinResponseDto> bulkCreatedCoins = coinService.createCoinBulk(createCoinDtos);

        return bulkCreatedCoins;
    }

    public List<ServiceCoinDto> getCoinsByExchange(MarketType marketType){
        ServiceCoinWrapperDto serviceCoinWrapperDto = marketService.getCoinListFromExchange(marketType);
        if(serviceCoinWrapperDto == null){
            return null;
        }
        return serviceCoinWrapperDto.getServiceCoins();
    }

}
