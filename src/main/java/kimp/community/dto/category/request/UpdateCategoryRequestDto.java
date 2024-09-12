package kimp.community.dto.category.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class UpdateCategoryRequestDto {

    private Long categoryId;

    private String categoryName;

    public UpdateCategoryRequestDto(Long categoryId, String categoryName) {
        if(categoryId == null || categoryId < 0){
            throw new IllegalArgumentException("categoryId invalid");
        }

        if (categoryName.isBlank()) {
            throw new IllegalArgumentException("categoryName is blank");
        } else {
            this.categoryName = categoryName;
        }

    }
}



