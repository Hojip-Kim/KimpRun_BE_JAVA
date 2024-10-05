package kimp.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;

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
    private String role;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Oauth oauth;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserAgent userAgent;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserWithdraw userWithdraw;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Profile profile;


    public User(){
        if(this.role == null){
            this.role = "USER";
        }
    }

    public User(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
        if(this.role == null){
            this.role = "USER";
        }
    }

    public User(String loginId, String password, String email) {
        this.loginId = loginId;
        this.password = password;
        if (this.role == null) {
            this.role = "USER";
        }
        this.email = email;
    }

}
