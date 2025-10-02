package kimp.cmc.vo;

public class GetAllCoinInfoPageDataVo {

    private final int page;
    private final int size;

    public GetAllCoinInfoPageDataVo(int page, int size) {
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
