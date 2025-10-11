package kimp.cmc.vo;

public class GetCoinsBySymbolContainingVo {

    private final String symbol;
    private final int page;
    private final int size;

    public GetCoinsBySymbolContainingVo(String symbol, int page, int size) {
        this.symbol = symbol;
        this.page = page;
        this.size = size;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
