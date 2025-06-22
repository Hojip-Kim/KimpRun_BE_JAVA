package kimp.market.controller;

import kimp.market.Enum.MarketType;
import kimp.market.dto.market.response.CombinedMarketList;
import kimp.market.dto.market.response.CombinedMarketDataList;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.service.MarketService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/market")
@AllArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @GetMapping("/first/name")
    public CombinedMarketList getMarketList(@RequestParam("first") MarketType first, @RequestParam("second") MarketType second) throws IOException {
        if(first == null || second == null) {
            throw new IllegalArgumentException("Not have parameter");
        }

        return this.marketService.getMarketList(first, second);
    }


    @GetMapping("/first/single/data")
    public MarketDataList getFirstMarketDatas(@RequestParam("market") MarketType market) throws IOException {
        if(market == null) {
            throw new IllegalArgumentException("Not have parameter");
        }

        return marketService.getMarketDataList(market);
    }

    @GetMapping("/first/combine/data")
    public CombinedMarketDataList getCombinedMarketDatas(@RequestParam("first") MarketType first, @RequestParam("second") MarketType second) throws IOException {
        if(first == null || second == null) {
            throw new IllegalArgumentException("Not have parameter");
        }

        return marketService.getCombinedMarketDataList(first, second);
    }

    @GetMapping("/first/test")
    public CombinedMarketDataList test() throws IOException {
       return marketService.getCombinedMarketDataList(MarketType.UPBIT, MarketType.BINANCE);
    }
}
