package kimp.cmc.controller;

import kimp.cmc.component.CoinMarketCapComponent;
import kimp.cmc.dto.common.coin.CmcApiDataDto;
import kimp.cmc.dto.common.coin.CmcCoinInfoDataMapDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDetailMapDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDto;
import kimp.cmc.dto.response.CmcCoinResponseDto;
import kimp.cmc.service.CmcCoinManageService;
import kimp.exception.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cmc")
public class CmcController {

    private final CmcCoinManageService cmcCoinManageService;
    private final CoinMarketCapComponent coinMarketCapComponent;

    public CmcController(CmcCoinManageService cmcCoinManageService, CoinMarketCapComponent coinMarketCapComponent) {
        this.cmcCoinManageService = cmcCoinManageService;
        this.coinMarketCapComponent = coinMarketCapComponent;
    }

    @GetMapping("/coin")
    public ApiResponse<CmcCoinResponseDto> getCoinDataByCoinId(
            @RequestParam("coinId") Long coinId
    ) {
        CmcCoinResponseDto result = this.cmcCoinManageService.findCmcCoinDataByCoinId(coinId);
        return ApiResponse.success(result);
    }

}
