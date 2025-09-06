package kimp.cmc.controller;

import kimp.cmc.component.CoinMarketCapComponent;
import kimp.cmc.dto.response.CmcCoinInfoResponseDto;
import kimp.cmc.dto.response.CmcCoinResponseDto;
import kimp.cmc.dto.response.CmcExchangeInfoResponseDto;
import kimp.cmc.service.CmcCoinManageService;
import kimp.cmc.service.CmcExchangeManageService;
import kimp.common.dto.PageRequestDto;
import kimp.exception.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cmc")
public class CmcController {

    private final CmcCoinManageService cmcCoinManageService;
    private final CmcExchangeManageService cmcExchangeManageService;
    private final CoinMarketCapComponent coinMarketCapComponent;

    public CmcController(CmcCoinManageService cmcCoinManageService, CmcExchangeManageService cmcExchangeManageService, CoinMarketCapComponent coinMarketCapComponent) {
        this.cmcCoinManageService = cmcCoinManageService;
        this.cmcExchangeManageService = cmcExchangeManageService;
        this.coinMarketCapComponent = coinMarketCapComponent;
    }

    @GetMapping("/coin")
    public ApiResponse<CmcCoinResponseDto> getCoinDataByCoinId(
            @RequestParam("coinId") Long coinId
    ) {
        CmcCoinResponseDto result = this.cmcCoinManageService.findCmcCoinDataByCoinId(coinId);
        return ApiResponse.success(result);
    }

    @GetMapping("/coin/all")
    public ApiResponse<Page<CmcCoinInfoResponseDto>> getAllCoinInfoPageData(
            @ModelAttribute PageRequestDto pageRequestDto
    ) {
        Page<CmcCoinInfoResponseDto> result = this.cmcCoinManageService.findAllCoinInfoDtosOrderByRank(pageRequestDto);
        return ApiResponse.success(result);
    }

    @GetMapping("/exchange/all")
    public ApiResponse<Page<CmcExchangeInfoResponseDto>> getAllExchangeInfoPageData(
            @ModelAttribute PageRequestDto pageRequestDto
    ) {
        Page<CmcExchangeInfoResponseDto> result = this.cmcExchangeManageService.findAllExchangesOrderBySpotVolume(pageRequestDto);
        return ApiResponse.success(result);
    }
    
    @GetMapping("/coin/{symbol}")
    public ApiResponse<Page<CmcCoinInfoResponseDto>> getCoinsBySymbolContaining(
            @PathVariable("symbol") String symbol,
            @ModelAttribute PageRequestDto pageRequestDto
    ) {
        Page<CmcCoinInfoResponseDto> result = this.cmcCoinManageService.findCoinsBySymbolContaining(symbol, pageRequestDto);
        return ApiResponse.success(result);
    }

}
