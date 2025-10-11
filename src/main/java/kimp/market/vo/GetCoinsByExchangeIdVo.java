package kimp.market.vo;

public class GetCoinsByExchangeIdVo {

    private final long exchangeId;

    public GetCoinsByExchangeIdVo(long exchangeId) {
        this.exchangeId = exchangeId;
    }

    public long getExchangeId() {
        return exchangeId;
    }
}
