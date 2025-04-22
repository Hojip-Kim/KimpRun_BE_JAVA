package kimp.market.controller;

import kimp.market.dto.market.response.CombinedMarketList;
import kimp.market.dto.market.response.CombinedMarketDataList;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.service.MarketService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/market")
@AllArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @GetMapping("/first/name")
    public CombinedMarketList getMarketList(@RequestParam("first") String first, @RequestParam("second") String second) throws IOException {
        if(first == null || second == null || first.isEmpty() || second.isEmpty()) {
            throw new IllegalArgumentException("Not have parameter");
        }

        return this.marketService.getMarketList(first, second);
    }


    @GetMapping("/first/single/data")
    public MarketDataList getFirstMarketDatas(@RequestParam("market") String market) throws IOException {
        if(market == null || market.isEmpty()) {
            throw new IllegalArgumentException("Not have parameter");
        }

        return marketService.getMarketDataList(market);
    }

    @GetMapping("/first/combine/data")
    public CombinedMarketDataList getCombinedMarketDatas(@RequestParam("first") String first, @RequestParam("second") String second) throws IOException {
        if(first == null || second == null || first.isEmpty() || second.isEmpty()) {
            throw new IllegalArgumentException("Not have parameter");
        }

        return marketService.getCombinedMarketDataList(first, second);
    }

    @GetMapping("/first/test")
    public CombinedMarketDataList test() throws IOException {
       return marketService.getCombinedMarketDataList("upbit", "binance");
    }
}
