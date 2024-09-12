package kimp.community.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import kimp.common.entity.TimeStamp;
import lombok.Getter;

@Entity
@Getter
public class Category extends TimeStamp {

    @Column(name="category_name",nullable = false)
    public String categoryName;

    public Category() {
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }


    public void updateCategoryName(String newCategoryName){
        if(newCategoryName.isBlank()){
            throw new IllegalArgumentException("update category name is blank.");
        }
        this.categoryName = newCategoryName;
    }

}
