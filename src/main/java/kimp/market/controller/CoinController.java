package kimp.market.controller;

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
    public ResponseEntity<CoinResponseDto> getCoinById(@PathVariable("id") long id) {
        CoinResponseDto response = coinService.getCoinByID(id);
        if(response == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{exchangeId}")
    public ResponseEntity<List<CoinResponseDto>> getCoinByExchangeId(@PathVariable("exchangeId") long exchangeId) {
        List<CoinResponseDto> response = coinService.getCoinsByExchangeId(exchangeId);
        if(response == null || response.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);

    }

    @PostMapping("/create")
    public ResponseEntity<CoinResponseDto> createCoin(@RequestBody CreateCoinDto createCoinDto) {
        CoinResponseDto response = coinService.createCoin(createCoinDto);
        if(response == null){
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update/all")
    public ResponseEntity<CoinResponseDto> updateAllCoinData(@RequestBody UpdateCoinDto updateCoinDto) {

        CoinResponseDto response = coinService.updateCoin(updateCoinDto);
        if(response == null){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update/content")
    public ResponseEntity<CoinResponseDto> updateCoinContent(@RequestBody UpdateContentCoinDto updateContentCoinDto) {
        CoinResponseDto response = coinService.updateContentCoin(updateContentCoinDto);
        if(response == null){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/add/exchange")
    public ResponseEntity<CoinResponseDto> addExchangeCoin(@RequestBody AdjustExchangeCoinDto adjustExchangeCoinDto) {
        CoinResponseDto response = coinService.addExchangeCoin(adjustExchangeCoinDto);
        if(response == null){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/delist/exchange")
    public ResponseEntity<Boolean> delistExchangeCoin(@RequestBody AdjustExchangeCoinDto adjustExchangeCoinDto) {
        coinService.deleteExchangeCoin(adjustExchangeCoinDto);

        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteCoin(@RequestBody DeleteCoinDto deleteCoinDto) {
        coinService.deleteCoin(deleteCoinDto);

        return ResponseEntity.ok(true);
    }


}
