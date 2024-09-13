package kimp.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;

@Entity
@Table(name= "\"user\"")
@Getter
public class User extends TimeStamp {

    @Column(name="login_id",nullable = false)
    private String loginId;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
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

    public User(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }


}
