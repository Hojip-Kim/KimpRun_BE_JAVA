package kimp.community.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.user.entity.Member;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "board")
public class Board extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CommentCount commentCount;

    @OneToOne(mappedBy = "board",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private BoardViews views;

    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private BoardLikeCount boardLikeCount;

    @Lob // 길이가 긴 문자열
    @Column(nullable = false)
    private String content;

    @Column
    private boolean isPin = false;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public Board() {

    }

    public Board( String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Board setMember(Member member){
        this.member = member;
        return this;
    }

    public Board addComment(Comment comment){
        this.comments.add(comment);
        return this;
    }

    public Board setViews(BoardViews boardViews){
        this.views = boardViews;
        return this;
    }

    public Board setBoardLikeCounts(BoardLikeCount boardLikeCount){
        this.boardLikeCount = boardLikeCount;
        return this;
    }

    public Board setCategory(Category category){
        this.category = category;
        return this;
    }

    public Board updateTitle(String title){
        if(title != null && !title.isEmpty()){
            this.title = title;
        }
        return this;
    }
    public Board updateContent(String content){
        if(content != null && !content.isEmpty()){
            this.content = content;
        }
        return this;
    }
    public Board setCommentCount(CommentCount commentCount){
        if(commentCount != null){
            this.commentCount = commentCount;
        }
        return this;
    }

    public void activePin(){
        this.isPin = true;
    }

    public void deactivePin(){
        this.isPin = false;
    }

    public void softDelete(){
        this.isDeleted = true;
    }

    public void restore(){
        this.isDeleted = false;
    }
}
