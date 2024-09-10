package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "user_agent")
@Getter
public class UserAgent extends TimeStamp {

    @OneToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String ip;

    @Column
    private Boolean is_banned;

    @OneToOne(mappedBy = "agent")
    private BannedCount bannedCount;


    public UserAgent() {
    }

    public UserAgent(User user, String ip, Boolean is_banned, BannedCount bannedCount) {
        this.user = user;
        this.ip = ip;
        this.is_banned = is_banned;
        this.bannedCount = bannedCount;
    }
}
