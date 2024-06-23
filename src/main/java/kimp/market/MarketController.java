package kimp.market;

import kimp.market.dto.response.UpbitMarketList;
import kimp.market.service.BinanceService;
import kimp.market.service.UpbitService;
import kimp.websocket.dto.response.SimpleUpbitDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/market")
public class MarketController {

    private final UpbitService upbitService;
    private final BinanceService binanceService;

    public MarketController(UpbitService upbitService, BinanceService binanceService) {
        this.upbitService = upbitService;
        this.binanceService = binanceService;
    }

    @GetMapping("/data")
    public UpbitMarketList getMarketData(@RequestParam(name = "market") String market){
        if (market == null || market.isEmpty())
        {throw new IllegalArgumentException("Not have Params");}

        UpbitMarketList response = this.upbitService.getUpbitMarketData();
        if(response == null){
            throw new IllegalArgumentException("Not have response");
        }
        return this.upbitService.getUpbitMarketData();
    }

    @GetMapping("/first")
    public Map<String, SimpleUpbitDto> getFirstMarketData(){

        return upbitService.getUpbitFirstMarketData();
    }
}
