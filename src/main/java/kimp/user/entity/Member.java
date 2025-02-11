package kimp.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.user.enums.UserRole;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name= "member")
@Getter
public class Member extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique=true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name="is_active", nullable = false)
    private boolean isActive = true;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonIgnore
    private Oauth oauth;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private UserAgent memberAgent;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private MemberWithdraw MemberWithdraw;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private Profile profile;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    public Member(){
        this.role = UserRole.USER;
    }

    public Member(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = UserRole.USER;
    }

    public Member(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = UserRole.USER;

    }

    public Member updateNickname(String newNickname){
        if(nickname.equals(newNickname)){
            throw new IllegalArgumentException("nickname is the same as the old nickname");
        }else if(newNickname.isEmpty()){
            throw new IllegalArgumentException("nickname is empty");
        }
        this.nickname = newNickname;
        return this;
    }

    public Member addBoard(Board board){
        this.boards.add(board);
        return this;
    }

    public Member addComment(Comment comment){
        this.comments.add(comment);
        return this;
    }

    public Member updatePassword(String password){
        this.password = password;
        return this;
    }

    public Member grantRole(UserRole role){
        if(this.role == null){
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.role = role;
        return this;
    }

    public Member setOauth(Oauth oauth){
        this.oauth = oauth;
        if (oauth.getMember() != this) { // Oauth간 무한 재귀호출 방지
            oauth.setMember(this);
        }
        return this;
    }

    public void deActivate(){
        this.isActive = false;
    }

}
