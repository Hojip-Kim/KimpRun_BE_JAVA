package kimp.community.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;

@Entity
@Getter
public class Category extends TimeStamp {

    @Column(name="category_name",nullable = false)
    public String categoryName;

    @OneToOne(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public BoardCount boardCount;

    public Category() {
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }


    public Category updateCategoryName(String newCategoryName){
        if(newCategoryName.isBlank()){
            throw new IllegalArgumentException("update category name is blank.");
        }
        this.categoryName = newCategoryName;

        return this;
    }

    public Category setBoardCount(BoardCount boardCount){
        if(boardCount == null){
            throw new IllegalArgumentException("boardCount must not be null");
        }

        this.boardCount = boardCount;

        return this;
    }

}
