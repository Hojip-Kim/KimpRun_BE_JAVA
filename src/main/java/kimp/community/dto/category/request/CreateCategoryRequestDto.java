package kimp.community.dto.category.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CreateCategoryRequestDto {

    private String categoryName;

    public CreateCategoryRequestDto(String categoryName){
        if(categoryName.isBlank()){
            throw new IllegalArgumentException("categoryName is blank");
        }else{
            this.categoryName = categoryName;
        }
    }

}
