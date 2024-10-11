package kimp.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.user.enums.UserRole;
import lombok.Getter;

import java.util.List;

@Entity
@Table(name= "user_table")
@Getter
public class User extends TimeStamp {

    @Column(name="login_id",nullable = false, unique=true)
    private String loginId;

    @Column(nullable = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Oauth oauth;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserAgent userAgent;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserWithdraw userWithdraw;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Board> boards;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments;

    public User(){
        if(this.role == null){
            this.role = UserRole.USER;
        }
    }

    public User(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
        if(this.role == null){
            this.role = UserRole.USER;
        }
    }

    public User(String loginId, String password, String email) {
        this.loginId = loginId;
        this.password = password;
        if (this.role == null) {
            this.role = UserRole.USER;
        }
        this.email = email;
    }

    public User addBoard(Board board){
        this.boards.add(board);
        return this;
    }

    public User addComment(Comment comment){
        this.comments.add(comment);
        return this;
    }

    public User updatePassword(String password){
        this.password = password;
        return this;
    }

    public User grantRole(UserRole role){
        if(this.role == null){
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.role = role;
        return this;
    }

}
