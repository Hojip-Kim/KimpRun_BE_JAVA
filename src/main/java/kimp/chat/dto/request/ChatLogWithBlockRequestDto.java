package kimp.chat.dto.request;

import kimp.common.dto.PageRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ChatLogWithBlockRequestDto extends PageRequestDto {
    
    private List<String> blockedMembers;
    private List<String> blockedGuests;

    public ChatLogWithBlockRequestDto(int page, int size, List<String> blockedMembers, List<String> blockedGuests) {
        super(page, size);
        this.blockedMembers = blockedMembers;
        this.blockedGuests = blockedGuests;
    }
}