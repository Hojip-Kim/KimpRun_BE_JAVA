package kimp.market.service;

import kimp.market.dto.coin.common.ChangeCoinDto;
import kimp.market.dto.coin.request.*;
import kimp.market.dto.coin.response.CoinResponseDto;
import kimp.market.dto.coin.response.CoinResponseWithMarketTypeDto;

import java.util.List;

public interface CoinService {

    public CoinResponseWithMarketTypeDto getCoinByID(long id);

    public CoinResponseDto createCoin(CreateCoinDto createCoinDto);

    // Binance / Upbit 스케줄러를통해 지워지거나 새로 생긴 코인을 추가하거나 지웁니다.
    public void createWithDeleteCoin(ChangeCoinDto changeCoinDto);

    public List<CoinResponseDto> createCoinBulk(List<CreateCoinDto> createCoinDtos);

    public List<CoinResponseDto> getCoinsByExchangeId(long exchangeId);

    public CoinResponseDto updateContentCoin(UpdateContentCoinDto updateCoinDto);

    public CoinResponseDto addExchangeCoin(AdjustExchangeCoinDto addExchangeCoin);

    public void deleteExchangeCoin(AdjustExchangeCoinDto deleteExchangeCoin);

    public CoinResponseDto updateCoin(UpdateCoinDto updateCoinDto);

    // 조심해서 사용. exchange의 코인 하나만 지우는것이 아닌, 완전히 지우고 해당 코인을 갖고있는 exchange의 코인을 다 지움.
    public void deleteCoin(DeleteCoinDto deleteCoinDto);
}
