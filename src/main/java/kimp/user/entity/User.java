package kimp.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;

@Entity
@Table(name= "user_table")
public class User extends TimeStamp {

    protected User(){}

    @Column(nullable = false)
    private String userId;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column()
    private String nickname;

    public User(String userId, String password, String nickname) {
        if(userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("Id가 비었습니다.");
        }
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
}
