package kimp.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.user.enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name= "member")
@Getter
@NoArgsConstructor
public class Member extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique=true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    // 유저 이름
    // oauth를통해 받아온 유저는 name이 이곳으로 들어감.
    @Column(nullable = true)
    private String name;

    // nickname은 중복 불가 (고유성)
    @Column(nullable = false, unique=true)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private MemberRole role;

    @Column(name="is_active", nullable = false)
    private boolean isActive = true;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Oauth oauth;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAgent memberAgent;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private MemberWithdraw MemberWithdraw;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Follow> following = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Follow> followers = new ArrayList<>();

    public Member(String email, String nickname, String password, MemberRole role) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
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

    public Member grantRole(MemberRole role){
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

    public void reActivate(){
        this.isActive = true;
    }

    public Member setName(String name) {
        this.name = name;
        return this;
    }
}
