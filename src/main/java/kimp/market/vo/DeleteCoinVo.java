package kimp.market.vo;

import kimp.market.dto.coin.request.DeleteCoinDto;

public class DeleteCoinVo {

    private final DeleteCoinDto deleteCoinDto;

    public DeleteCoinVo(DeleteCoinDto deleteCoinDto) {
        this.deleteCoinDto = deleteCoinDto;
    }

    public DeleteCoinDto getDeleteCoinDto() {
        return deleteCoinDto;
    }
}
