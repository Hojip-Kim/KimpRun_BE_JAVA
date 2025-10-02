package kimp.chat.vo;

public class GetChatMessagesVo {

    private final int page;
    private final int size;

    public GetChatMessagesVo(int page, int size) {
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
