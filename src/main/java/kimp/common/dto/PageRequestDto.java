package kimp.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequestDto {

    private Integer page;
    private Integer size;

    public PageRequestDto(){}
    public PageRequestDto(Integer page, Integer size){
        if(isPageHigherThanZero(page)) {
            this.page = page;
        }else{
            throw new IllegalArgumentException("page must higher than Zero");
        }
        if(isSizeHigherThanOne(size)) {
            this.size = size;
        }else{
            throw new IllegalArgumentException("size must higher than One");
        }
    }

    public boolean isPageHigherThanZero(Integer page){
        return page >= 0;
    }

    public boolean  isSizeHigherThanOne(Integer size){
        return size >= 1;
    }

}


