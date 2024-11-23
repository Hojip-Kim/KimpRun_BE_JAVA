package kimp.market.controller;

import kimp.market.dto.response.MarketDollarResponseDto;
import kimp.market.service.MarketInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/marketInfo")
public class MarketInfoController {

    private final MarketInfoService marketInfoService;

    public MarketInfoController(MarketInfoService marketInfoService) {
        this.marketInfoService = marketInfoService;
    }


    @GetMapping("/dollar")
    public MarketDollarResponseDto getDollar() {
        return new MarketDollarResponseDto(marketInfoService.getDollarKRW());
    }
}
