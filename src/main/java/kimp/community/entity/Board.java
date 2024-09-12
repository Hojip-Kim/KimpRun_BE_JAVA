package kimp.community.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.user.entity.User;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class Board extends TimeStamp {

    @OneToOne(fetch = FetchType.EAGER)
    private Category category;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY )
    private User user;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Comment> comments;

    @Lob // 길이가 긴 문자열
    @Column(nullable = false)
    private String content;

    public Board() {

    }

    public Board(Category category, String title, User user, List<Comment> comments, String content) {
        this.category = category;
        this.title = title;
        this.user = user;
        this.comments = comments;
        this.content = content;
    }
}
