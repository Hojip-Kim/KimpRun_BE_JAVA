package kimp.market.controller;

import kimp.exception.response.ApiResponse;
import kimp.market.dto.coin.request.*;
import kimp.market.dto.coin.response.CoinResponseDto;
import kimp.market.service.CoinService;
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
        CoinResponseDto response = coinService.getCoinByID(id);
        return ApiResponse.success(response);
    }

    @GetMapping("/exchange/{exchangeId}")
    public ApiResponse<List<CoinResponseDto>> getCoinByExchangeId(@PathVariable("exchangeId") long exchangeId) {
        List<CoinResponseDto> response = coinService.getCoinsByExchangeId(exchangeId);
        return ApiResponse.success(response);
    }

    @PostMapping("/create")
    public ApiResponse<CoinResponseDto> createCoin(@RequestBody CreateCoinDto createCoinDto) {
        CoinResponseDto response = coinService.createCoin(createCoinDto);
        return ApiResponse.success(response);
    }

    @PatchMapping("/update/all")
    public ApiResponse<CoinResponseDto> updateAllCoinData(@RequestBody UpdateCoinDto updateCoinDto) {

        CoinResponseDto response = coinService.updateCoin(updateCoinDto);
        return ApiResponse.success(response);
    }

    @PatchMapping("/update/content")
    public ApiResponse<CoinResponseDto> updateCoinContent(@RequestBody UpdateContentCoinDto updateContentCoinDto) {
        CoinResponseDto response = coinService.updateContentCoin(updateContentCoinDto);
        return ApiResponse.success(response);
    }

    @PatchMapping("/add/exchange")
    public ApiResponse<CoinResponseDto> addExchangeCoin(@RequestBody AdjustExchangeCoinDto adjustExchangeCoinDto) {
        CoinResponseDto response = coinService.addExchangeCoin(adjustExchangeCoinDto);
        return ApiResponse.success(response);
    }

    @PatchMapping("/delist/exchange")
    public ApiResponse<Boolean> delistExchangeCoin(@RequestBody AdjustExchangeCoinDto adjustExchangeCoinDto) {
        coinService.deleteExchangeCoin(adjustExchangeCoinDto);
        return ApiResponse.success(true);
    }

    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteCoin(@RequestBody DeleteCoinDto deleteCoinDto) {
        coinService.deleteCoin(deleteCoinDto);
        return ApiResponse.success(true);
    }


}
