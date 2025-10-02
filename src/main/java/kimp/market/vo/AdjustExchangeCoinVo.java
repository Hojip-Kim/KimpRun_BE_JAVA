package kimp.market.vo;

import kimp.market.dto.coin.request.AdjustExchangeCoinDto;

public class AdjustExchangeCoinVo {

    private final AdjustExchangeCoinDto adjustExchangeCoinDto;

    public AdjustExchangeCoinVo(AdjustExchangeCoinDto adjustExchangeCoinDto) {
        this.adjustExchangeCoinDto = adjustExchangeCoinDto;
    }

    public AdjustExchangeCoinDto getAdjustExchangeCoinDto() {
        return adjustExchangeCoinDto;
    }
}
