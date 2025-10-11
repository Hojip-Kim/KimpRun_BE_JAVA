package kimp.market.vo;

import kimp.market.dto.coin.request.UpdateContentCoinDto;

public class UpdateCoinContentVo {

    private final UpdateContentCoinDto updateContentCoinDto;

    public UpdateCoinContentVo(UpdateContentCoinDto updateContentCoinDto) {
        this.updateContentCoinDto = updateContentCoinDto;
    }

    public UpdateContentCoinDto getUpdateContentCoinDto() {
        return updateContentCoinDto;
    }
}
