package kimp.community.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.user.entity.Member;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Cacheable
@Entity
@Getter
public class Category extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="category_name",nullable = false, unique = true)
    public String categoryName;

    @OneToOne(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public BoardCount boardCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    public Member member;

    public Category() {
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }


    public Category updateCategoryName(String newCategoryName){
        if(newCategoryName.isBlank()){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Category name cannot be blank", HttpStatus.BAD_REQUEST, "Category.updateCategoryName");
        }
        this.categoryName = newCategoryName;

        return this;
    }

    public Category setBoardCount(BoardCount boardCount){
        if(boardCount == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "BoardCount cannot be null", HttpStatus.BAD_REQUEST, "Category.setBoardCount");
        }

        this.boardCount = boardCount;

        return this;
    }

    public Category setMember(Member member){
        if(member == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Member cannot be null", HttpStatus.BAD_REQUEST, "Category.setMember");
        }
        this.member = member;
        return this;
    }

}
