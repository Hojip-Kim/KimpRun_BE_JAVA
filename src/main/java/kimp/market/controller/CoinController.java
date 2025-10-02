package kimp.market.controller;

import kimp.exception.response.ApiResponse;
import kimp.market.dto.coin.request.*;
import kimp.market.dto.coin.response.CoinResponseDto;
import kimp.market.service.CoinService;
import kimp.market.vo.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coin")
public class CoinController {
    private final CoinService coinService;

    public CoinController(CoinService coinService) {
        this.coinService = coinService;
    }

    @GetMapping("/{id}")
    public ApiResponse<CoinResponseDto> getCoinById(@PathVariable("id") long id) {
        GetCoinByIdVo vo = new GetCoinByIdVo(id);
        CoinResponseDto response = coinService.getCoinByID(vo);
        return ApiResponse.success(response);
    }

    @GetMapping("/exchange/{exchangeId}")
    public ApiResponse<List<CoinResponseDto>> getCoinByExchangeId(@PathVariable("exchangeId") long exchangeId) {
        GetCoinsByExchangeIdVo vo = new GetCoinsByExchangeIdVo(exchangeId);
        List<CoinResponseDto> response = coinService.getCoinsByExchangeId(vo);
        return ApiResponse.success(response);
    }

    @PostMapping("/create")
    public ApiResponse<CoinResponseDto> createCoin(@RequestBody CreateCoinDto createCoinDto) {
        CreateCoinVo vo = new CreateCoinVo(createCoinDto);
        CoinResponseDto response = coinService.createCoin(vo);
        return ApiResponse.success(response);
    }

    @PatchMapping("/update/all")
    public ApiResponse<CoinResponseDto> updateAllCoinData(@RequestBody UpdateCoinDto updateCoinDto) {
        UpdateCoinVo vo = new UpdateCoinVo(updateCoinDto);
        CoinResponseDto response = coinService.updateCoin(vo);
        return ApiResponse.success(response);
    }

    @PatchMapping("/update/content")
    public ApiResponse<CoinResponseDto> updateCoinContent(@RequestBody UpdateContentCoinDto updateContentCoinDto) {
        UpdateCoinContentVo vo = new UpdateCoinContentVo(updateContentCoinDto);
        CoinResponseDto response = coinService.updateContentCoin(vo);
        return ApiResponse.success(response);
    }

    @PatchMapping("/add/exchange")
    public ApiResponse<CoinResponseDto> addExchangeCoin(@RequestBody AdjustExchangeCoinDto adjustExchangeCoinDto) {
        AdjustExchangeCoinVo vo = new AdjustExchangeCoinVo(adjustExchangeCoinDto);
        CoinResponseDto response = coinService.addExchangeCoin(vo);
        return ApiResponse.success(response);
    }

    @PatchMapping("/delist/exchange")
    public ApiResponse<Boolean> delistExchangeCoin(@RequestBody AdjustExchangeCoinDto adjustExchangeCoinDto) {
        AdjustExchangeCoinVo vo = new AdjustExchangeCoinVo(adjustExchangeCoinDto);
        coinService.deleteExchangeCoin(vo);
        return ApiResponse.success(true);
    }

    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteCoin(@RequestBody DeleteCoinDto deleteCoinDto) {
        DeleteCoinVo vo = new DeleteCoinVo(deleteCoinDto);
        coinService.deleteCoin(vo);
        return ApiResponse.success(true);
    }


}
