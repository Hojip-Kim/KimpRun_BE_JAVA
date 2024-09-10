package kimp.community.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import kimp.common.entity.TimeStamp;
import lombok.Getter;

@Entity
@Getter
public class Category extends TimeStamp {

    @Column(nullable = false)
    public String category_name;

    public Category() {
    }

    public Category(String category_name) {
        this.category_name = category_name;
    }
}
