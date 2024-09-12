package kimp.community.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import kimp.common.entity.TimeStamp;
import kimp.user.entity.User;
import lombok.Getter;

@Entity
@Getter
public class Comment extends TimeStamp {


    @Column(nullable = false)
    public String content;

    @ManyToOne
    public User user;

    @ManyToOne
    public Board board;

    public Comment() {
    }

    public Comment(String content, User user, Board board) {
        this.content = content;
        this.user = user;
        this.board = board;
    }
}
