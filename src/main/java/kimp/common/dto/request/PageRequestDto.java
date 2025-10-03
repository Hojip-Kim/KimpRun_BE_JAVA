package kimp.common.dto.request;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

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
            throw new KimprunException(KimprunExceptionEnum.INVALID_PAGE_PARAMETER_EXCEPTION, "Page number must be greater than or equal to 0", HttpStatus.BAD_REQUEST, "PageRequestDto.constructor");
        }
        if(isSizeHigherThanOne(size)) {
            this.size = size;
        }else{
            throw new KimprunException(KimprunExceptionEnum.INVALID_PAGE_PARAMETER_EXCEPTION, "Page size must be greater than or equal to 1", HttpStatus.BAD_REQUEST, "PageRequestDto.constructor");
        }
    }

    public boolean isPageHigherThanZero(Integer page){
        return page >= 0;
    }

    public boolean  isSizeHigherThanOne(Integer size){
        return size >= 1;
    }

}


