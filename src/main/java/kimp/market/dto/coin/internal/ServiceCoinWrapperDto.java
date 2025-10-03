package kimp.market.dto.coin.internal;

import kimp.market.Enum.MarketType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ServiceCoinWrapperDto {

    private MarketType marketType;

    private List<ServiceCoinDto> serviceCoins;

    public ServiceCoinWrapperDto(MarketType marketType, List<ServiceCoinDto> serviceCoins) {
        this.marketType = marketType;

        if(serviceCoins.isEmpty()){
            throw new IllegalArgumentException("serviceCoins is empty");
        }
        this.serviceCoins = serviceCoins;
    }
}
