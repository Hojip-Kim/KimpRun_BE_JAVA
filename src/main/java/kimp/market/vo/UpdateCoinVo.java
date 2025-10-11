package kimp.market.vo;

import kimp.market.dto.coin.request.UpdateCoinDto;

public class UpdateCoinVo {

    private final UpdateCoinDto updateCoinDto;

    public UpdateCoinVo(UpdateCoinDto updateCoinDto) {
        this.updateCoinDto = updateCoinDto;
    }

    public UpdateCoinDto getUpdateCoinDto() {
        return updateCoinDto;
    }
}
