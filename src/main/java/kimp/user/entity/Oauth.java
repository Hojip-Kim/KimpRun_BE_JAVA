package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;

@Entity
@Table(name = "oauth")
@Getter
public class Oauth extends TimeStamp {
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column
    private String access_token;

    @Column
    private String refresh_token;

    public Oauth() {
    }

    public Oauth(User user, String access_token, String refresh_token) {
        this.user = user;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }
}
