package kimp.chat.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatLogRequestDto {

    private Integer page;
    private Integer size;

    public ChatLogRequestDto(){}
    public ChatLogRequestDto(Integer page, Integer size){
        this.page = page;
        this.size = size;
    }}
