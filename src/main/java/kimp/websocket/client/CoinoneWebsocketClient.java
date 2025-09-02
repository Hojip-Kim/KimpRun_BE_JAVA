package kimp.websocket.client;

import jakarta.annotation.PostConstruct;
import kimp.market.components.impl.market.Coinone;
import kimp.market.dto.coin.common.market.CoinoneDto;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.controller.MarketDataStompController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


// 향후 스트림데이터 제공할 수도 있으므로 임의로 만들어놓은 CoinoneWebsocketClient
// 현재는 컴포넌트형태로만 제공하고, 추후 코인원 내부사정 달라지면 리팩토링
@Slf4j
@Component
public class CoinoneWebsocketClient {
    private final MarketDataStompController marketDataStompController;
    private final Coinone coinone;

    public CoinoneWebsocketClient(MarketDataStompController marketDataStompController, Coinone coinone) {
        this.marketDataStompController = marketDataStompController;
        this.coinone = coinone;
    }

    @PostConstruct
    public void init(){
        inputDataToHashMap();
    }

    @Scheduled(fixedRate = 3000, scheduler = "marketDataTaskScheduler")
    public void inputDataToHashMap() {
        MarketDataList<CoinoneDto> coinoneMarketDataList = coinone.getMarketDataList();
        List<CoinoneDto> coinoneDtoList = coinoneMarketDataList.getMarketDataList();

        for(CoinoneDto coinoneDto : coinoneDtoList){
            marketDataStompController.inputDataToHashMap(coinoneDto);
        }
    }

}
