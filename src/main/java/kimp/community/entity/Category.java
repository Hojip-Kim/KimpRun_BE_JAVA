package kimp.community.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.user.entity.Member;
import lombok.Getter;

@Cacheable
@Entity
@Getter
public class Category extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="category_name",nullable = false)
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

    public Category setMember(Member member){
        if(member == null){
            throw new IllegalArgumentException("member must not be null");
        }
        this.member = member;
        return this;
    }

}
