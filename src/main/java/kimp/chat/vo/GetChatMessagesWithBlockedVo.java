package kimp.chat.vo;

import java.util.List;

public class GetChatMessagesWithBlockedVo {

    private final int page;
    private final int size;
    private final List<String> blockedMembers;
    private final List<String> blockedGuests;

    public GetChatMessagesWithBlockedVo(int page, int size, List<String> blockedMembers, List<String> blockedGuests) {
        this.page = page;
        this.size = size;
        this.blockedMembers = blockedMembers;
        this.blockedGuests = blockedGuests;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public List<String> getBlockedMembers() {
        return blockedMembers;
    }

    public List<String> getBlockedGuests() {
        return blockedGuests;
    }
}
