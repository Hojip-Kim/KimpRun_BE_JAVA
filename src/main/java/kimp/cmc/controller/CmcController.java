package kimp.cmc.controller;

import kimp.cmc.dto.response.CmcCoinInfoResponseDto;
import kimp.cmc.dto.response.CmcCoinResponseDto;
import kimp.cmc.dto.response.CmcExchangeInfoResponseDto;
import kimp.cmc.service.CmcCoinManageService;
import kimp.cmc.service.CmcExchangeManageService;
import kimp.cmc.vo.GetAllCoinInfoPageDataVo;
import kimp.cmc.vo.GetAllExchangeInfoPageDataVo;
import kimp.cmc.vo.GetCoinDataByCoinIdVo;
import kimp.cmc.vo.GetCoinsBySymbolContainingVo;
import kimp.common.dto.request.PageRequestDto;
import kimp.exception.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cmc")
public class CmcController {

    private final CmcCoinManageService cmcCoinManageService;
    private final CmcExchangeManageService cmcExchangeManageService;

    public CmcController(CmcCoinManageService cmcCoinManageService, CmcExchangeManageService cmcExchangeManageService) {
        this.cmcCoinManageService = cmcCoinManageService;
        this.cmcExchangeManageService = cmcExchangeManageService;
    }

    @GetMapping("/coin")
    public ApiResponse<CmcCoinResponseDto> getCoinDataByCoinId(
            @RequestParam("coinId") Long coinId
    ) {
        GetCoinDataByCoinIdVo vo = new GetCoinDataByCoinIdVo(coinId);
        CmcCoinResponseDto result = this.cmcCoinManageService.findCmcCoinDataByCoinId(vo);
        return ApiResponse.success(result);
    }

    @GetMapping("/coin/all")
    public ApiResponse<Page<CmcCoinInfoResponseDto>> getAllCoinInfoPageData(
            @ModelAttribute PageRequestDto pageRequestDto
    ) {
        GetAllCoinInfoPageDataVo vo = new GetAllCoinInfoPageDataVo(pageRequestDto.getPage(), pageRequestDto.getSize());
        Page<CmcCoinInfoResponseDto> result = this.cmcCoinManageService.findAllCoinInfoDtosOrderByRank(vo);
        return ApiResponse.success(result);
    }

    @GetMapping("/exchange/all")
    public ApiResponse<Page<CmcExchangeInfoResponseDto>> getAllExchangeInfoPageData(
            @ModelAttribute PageRequestDto pageRequestDto
    ) {
        GetAllExchangeInfoPageDataVo vo = new GetAllExchangeInfoPageDataVo(pageRequestDto.getPage(), pageRequestDto.getSize());
        Page<CmcExchangeInfoResponseDto> result = this.cmcExchangeManageService.findAllExchangesOrderBySpotVolume(vo);
        return ApiResponse.success(result);
    }
    
    @GetMapping("/coin/{symbol}")
    public ApiResponse<Page<CmcCoinInfoResponseDto>> getCoinsBySymbolContaining(
            @PathVariable("symbol") String symbol,
            @ModelAttribute PageRequestDto pageRequestDto
    ) {
        GetCoinsBySymbolContainingVo vo = new GetCoinsBySymbolContainingVo(symbol, pageRequestDto.getPage(), pageRequestDto.getSize());
        Page<CmcCoinInfoResponseDto> result = this.cmcCoinManageService.findCoinsBySymbolContaining(vo);
        return ApiResponse.success(result);
    }

}
