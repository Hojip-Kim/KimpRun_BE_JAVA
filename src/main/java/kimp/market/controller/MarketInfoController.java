package kimp.market.controller;

import kimp.exception.response.ApiResponse;
import kimp.market.dto.market.response.MarketDollarResponseDto;
import kimp.market.dto.market.response.MarketTetherResponseDto;
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
    public ApiResponse<MarketDollarResponseDto> getDollar() {
        MarketDollarResponseDto result = new MarketDollarResponseDto(marketInfoService.getDollarKRW());
        return ApiResponse.success(result);
    }

    @GetMapping("/tether")
    public ApiResponse<MarketTetherResponseDto> getTether(){
        MarketTetherResponseDto result = new MarketTetherResponseDto(marketInfoService.getTetherKRW());
        return ApiResponse.success(result);
    }
}
