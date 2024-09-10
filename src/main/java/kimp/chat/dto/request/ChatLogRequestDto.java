package kimp.chat.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatLogRequestDto {

    private int page;
    private int size;

    public ChatLogRequestDto(){}
    public ChatLogRequestDto(int page, int size){
        this.page = page;
        this.size = size;
    }}
