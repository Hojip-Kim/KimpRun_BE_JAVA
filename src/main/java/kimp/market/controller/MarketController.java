package kimp.market.controller;

import kimp.market.dto.response.UpbitMarketList;
import kimp.market.service.BinanceService;
import kimp.market.service.UpbitService;
import kimp.websocket.dto.response.SimpleUpbitDto;
import kimp.websocket.dto.response.UpbitMarketDataList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/market")
public class MarketController {

    private final UpbitService upbitService;
    private final BinanceService binanceService;

    public MarketController(UpbitService upbitService, BinanceService binanceService) {
        this.upbitService = upbitService;
        this.binanceService = binanceService;
    }

    @GetMapping("/first/name")
    public UpbitMarketList getMarketData(){

        UpbitMarketList response = this.upbitService.getUpbitMarketData();
        if(response == null){
            throw new IllegalArgumentException("Not have response");
        }
        return response;
    }


    @GetMapping("/first/data")
    public UpbitMarketDataList getFirstMarketDatas(){

        return upbitService.getUpbitFirstMarketData();
    }
}
