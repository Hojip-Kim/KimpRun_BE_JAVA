package kimp.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;

@Entity
@Table(name= "\"user\"")
@Getter
public class User extends TimeStamp {

    @Column(nullable = false)
    private String login_id;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column()
    private String nickname;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Oauth oauth;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserAgent userAgent;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserWithdraw userWithdraw;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Profile profile;


    public User(){}

    public User(String login_id, String password, String nickname, Oauth oauth, UserAgent userAgent, UserWithdraw userWithdraw, Profile profile) {
        this.login_id = login_id;
        this.password = password;
        this.nickname = nickname;
        this.oauth = oauth;
        this.userAgent = userAgent;
        this.userWithdraw = userWithdraw;
        this.profile = profile;
    }
}
