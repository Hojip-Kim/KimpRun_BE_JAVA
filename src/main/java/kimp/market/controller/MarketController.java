package kimp.market.controller;

import kimp.exception.response.ApiResponse;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.market.Enum.MarketType;
import kimp.market.dto.market.response.CombinedMarketList;
import kimp.market.dto.market.response.CombinedMarketDataList;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.service.MarketService;
import kimp.market.vo.GetCombinedMarketDataListVo;
import kimp.market.vo.GetMarketDataListVo;
import kimp.market.vo.GetMarketListVo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/market")
@AllArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @GetMapping("/first/name")
    public ApiResponse<CombinedMarketList> getMarketList(@RequestParam("first") MarketType first, @RequestParam("second") MarketType second) throws IOException {
        if(first == null || second == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Market type parameters cannot be null", HttpStatus.BAD_REQUEST, "MarketController.getMarketList");
        }

        GetMarketListVo vo = new GetMarketListVo(first, second);
        CombinedMarketList result = this.marketService.getMarketListFromDatabase(vo);
        return ApiResponse.success(result);
    }

    @GetMapping("/first/single/data")
    public ApiResponse<MarketDataList> getFirstMarketDatas(@RequestParam("market") MarketType market) throws IOException {
        if(market == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Market type parameter cannot be null", HttpStatus.BAD_REQUEST, "MarketController.getFirstMarketDatas");
        }

        GetMarketDataListVo vo = new GetMarketDataListVo(market);
        MarketDataList result = marketService.getMarketDataList(vo);
        return ApiResponse.success(result);
    }

    @GetMapping("/first/combine/data")
    public ApiResponse<CombinedMarketDataList> getCombinedMarketDatas(@RequestParam("first") MarketType first, @RequestParam("second") MarketType second) throws IOException {
        if(first == null || second == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Market type parameters cannot be null", HttpStatus.BAD_REQUEST, "MarketController.getCombinedMarketDatas");
        }

        GetCombinedMarketDataListVo vo = new GetCombinedMarketDataListVo(first, second);
        CombinedMarketDataList result = marketService.getCombinedMarketDataList(vo);
        return ApiResponse.success(result);
    }

    @GetMapping("/first/test")
    public ApiResponse<CombinedMarketDataList> test() throws IOException {
       GetCombinedMarketDataListVo vo = new GetCombinedMarketDataListVo(MarketType.UPBIT, MarketType.BINANCE);
       CombinedMarketDataList result = marketService.getCombinedMarketDataList(vo);
       return ApiResponse.success(result);
    }
}
