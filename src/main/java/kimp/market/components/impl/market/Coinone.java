package kimp.market.components.impl.market;

import jakarta.annotation.PostConstruct;
import kimp.market.Enum.MarketType;
import kimp.market.components.MarketListProvider;
import kimp.market.components.impl.Market;
import kimp.market.dto.coin.common.ServiceCoinDto;
import kimp.market.dto.coin.common.ServiceCoinWrapperDto;
import kimp.market.dto.coin.common.crypto.CoinoneCryptoDto;
import kimp.market.dto.coin.common.market.CoinoneDto;
import kimp.market.dto.market.common.MarketList;
import kimp.market.dto.market.response.CoinoneTicker;
import kimp.market.dto.market.response.CoinoneTickerInfo;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.service.CoinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("coinone")
public class Coinone extends Market<CoinoneCryptoDto> {

    private final MarketListProvider coinoneMarketListProvider;
    private final RestClient restClient;
    private final CoinService coinService;

    public Coinone(@Qualifier("coinoneName") MarketListProvider coinoneMarketListProvider, RestClient restClient, CoinService coinService) {
        this.coinoneMarketListProvider = coinoneMarketListProvider;
        this.restClient = restClient;
        this.coinService = coinService;
    }

    public MarketList<CoinoneCryptoDto> coinoneMarketList = null;

    public MarketDataList<CoinoneDto> coinoneMarketDataList;

    @Value("${coinone.ticker.url}")
    private String coinoneTickerUrl;

    @PostConstruct
    @Override
    public void initFirst() throws IOException {
        if(this.coinoneMarketList == null){
            setCoinoneMarketList();
        }
    }

    @Override
    public MarketList<CoinoneCryptoDto> getMarketList() {
        return this.coinoneMarketList;
    }

    @Override
    public MarketDataList getMarketDataList() {
        if(this.coinoneMarketDataList == null) {
            setMarketDataList();
        }
        return this.coinoneMarketDataList;
    }

    @Override
    public ServiceCoinWrapperDto getServiceCoins() {
        List<String> stringMarketPair = getMarketList().getPairList();

        List<ServiceCoinDto> serviceCoinDtos = new ArrayList<>();
        for(String pair : stringMarketPair){
            serviceCoinDtos.add(new ServiceCoinDto(pair, pair, null));
        }
        return new ServiceCoinWrapperDto(this.getMarketType(), serviceCoinDtos);
    }

    @Override
    public MarketType getMarketType() {
        return MarketType.COINONE;
    }

    public void setMarketDataList() {
        if (this.coinoneMarketList == null) {
            throw new IllegalArgumentException("Upbit Market List is null");
        }
        // coinonePairList : btc 등 (lower-case)
        List<String> coinonePairList = coinoneMarketList.getLowerPairList();

        Set<String> coinonePairSet = new HashSet<>(coinonePairList);

        String tickerUrlwithParams = coinoneTickerUrl;
        CoinoneTicker tickerData = restClient.get()
                .uri(tickerUrlwithParams)
                .retrieve()
                .body(CoinoneTicker.class);
        List<CoinoneTickerInfo> coinoneTickerInfos = tickerData.getTickers();

        List<CoinoneTickerInfo> filteredTickerInfos = coinoneTickerInfos.stream()
                .filter(coinoneTickerInfo -> coinonePairSet.contains(coinoneTickerInfo.getTargetCurrency()))
                .collect(Collectors.toList());

        CoinoneDto coinoneDto = null;
        MarketDataList<CoinoneDto> coinoneMarketDataList = null;

        try{

            List<CoinoneDto> marketDataList = new ArrayList<>();

            for(CoinoneTickerInfo ticker : filteredTickerInfos){
                BigDecimal signedChangeRate = ticker.getLast()
                        .divide(ticker.getFirst(), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .subtract(new BigDecimal("100"))
                        .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

                double signedChangeRateInt = signedChangeRate.doubleValue();
                String rateChange;
                if(signedChangeRateInt > 0){
                    rateChange = "RISE";
                }else if(signedChangeRateInt < 0){
                    rateChange = "FALL";
                }else{
                    rateChange = "EVEN";
                }

                coinoneDto = new CoinoneDto(ticker.getTargetCurrency().toUpperCase(), ticker.getQuoteVolume(), signedChangeRate, null, null, ticker.getFirst(), ticker.getLast(), rateChange, ticker.getQuoteVolume());
                marketDataList.add(coinoneDto);
            }

            coinoneMarketDataList = new MarketDataList<>(marketDataList);
            if(coinoneMarketDataList.getMarketDataList() != null && !coinoneMarketDataList.getMarketDataList().isEmpty()) {
                this.coinoneMarketDataList = coinoneMarketDataList;
            }
            else{
                throw new IllegalArgumentException("Upbit Market List is null");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void setCoinoneMarketList() throws IOException{

        List<String> marketList = coinoneMarketListProvider.getMarketListWithTicker();

        MarketList<CoinoneCryptoDto> marketDtoList = new MarketList<>(new ArrayList<CoinoneCryptoDto>());

        for(String market : marketList){
            String replacedMarket = market.replace("KRW-", "");
            marketDtoList.getCryptoDtoList().add(new CoinoneCryptoDto(replacedMarket, "KRW"));
        }

        this.coinoneMarketList = marketDtoList;

    }

}
