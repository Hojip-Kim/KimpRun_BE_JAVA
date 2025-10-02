package kimp.cmc.vo;

public class GetAllExchangeInfoPageDataVo {

    private final int page;
    private final int size;

    public GetAllExchangeInfoPageDataVo(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
