package kimp.cmc.controller;

import kimp.cmc.component.CoinMarketCapComponent;
import kimp.cmc.dto.common.coin.CmcApiDataDto;
import kimp.cmc.dto.common.coin.CmcCoinInfoDataMapDto;
import kimp.cmc.dto.common.coin.CmcCoinMapDataDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDetailMapDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDto;
import kimp.cmc.service.CmcCoinManageService;
import kimp.exception.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/test1")
    public ApiResponse<List<CmcCoinMapDataDto>> getCoinMapFromCMCTest() {
        List<CmcCoinMapDataDto> result = coinMarketCapComponent.getCoinMapFromCMC(1, 5000);
        return ApiResponse.success(result);
    }

    @GetMapping("/test2")
    public ApiResponse<List<CmcApiDataDto>> getLatestCoinInfoFromCMCTest() {
        List<CmcApiDataDto> result = coinMarketCapComponent.getLatestCoinInfoFromCMC(1, 5000);
        return ApiResponse.success(result);
    }

    @GetMapping("/test3")
    public ApiResponse<CmcCoinInfoDataMapDto> getCmcCoinInfosTest() {
        CmcCoinInfoDataMapDto result = coinMarketCapComponent.getCmcCoinInfos(new ArrayList<>( List.of(1,2,3)));
        return ApiResponse.success(result);
    }

    @GetMapping("/test4")
    public ApiResponse<List<CmcExchangeDto>> getExchangeMapTest() {
        List<CmcExchangeDto> result = coinMarketCapComponent.getExchangeMap(1, 5000);
        return ApiResponse.success(result);
    }

    @GetMapping("/test5")
    public ApiResponse<CmcExchangeDetailMapDto> getExchangeInfo() {
        CmcExchangeDetailMapDto result = coinMarketCapComponent.getExchangeInfo(List.of(1, 2, 3));
        return ApiResponse.success(result);
    }

}
