package kimp.cmc.vo;

public class GetCoinDataByCoinIdVo {

    private final Long coinId;

    public GetCoinDataByCoinIdVo(Long coinId) {
        this.coinId = coinId;
    }

    public Long getCoinId() {
        return coinId;
    }
}
