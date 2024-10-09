package kimp.community.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.user.entity.User;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
@Table(name = "board")
public class Board extends TimeStamp {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToOne(mappedBy = "board",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private BoardViews views;

    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private BoardLikeCount boardLikeCount;

    @Lob // 길이가 긴 문자열
    @Column(nullable = false)
    private String content;

    public Board() {

    }

    public Board( String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Board setUser(User user){
        this.user = user;
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
}
