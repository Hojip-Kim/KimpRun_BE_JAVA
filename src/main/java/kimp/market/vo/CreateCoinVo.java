package kimp.market.vo;

import kimp.market.dto.coin.request.CreateCoinDto;

public class CreateCoinVo {

    private final CreateCoinDto createCoinDto;

    public CreateCoinVo(CreateCoinDto createCoinDto) {
        this.createCoinDto = createCoinDto;
    }

    public CreateCoinDto getCreateCoinDto() {
        return createCoinDto;
    }
}
