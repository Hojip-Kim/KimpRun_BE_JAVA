package kimp.market.controller;

import kimp.market.dto.response.CombinedMarketList;
import kimp.market.dto.response.MarketDataList;
import kimp.market.dto.response.MarketList;
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
    public CombinedMarketList getMarketList() throws IOException {

        return this.marketService.getMarketList();
    }


    @GetMapping("/first/data")
    public MarketDataList getFirstMarketDatas(@RequestParam String market) throws IOException {

        return marketService.getMarketDataList(market);
    }

    @GetMapping("/first/test")
    public void test() throws IOException {
        marketService.getMarkets();
    }
}
