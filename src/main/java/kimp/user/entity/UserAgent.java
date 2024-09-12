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

    @Column(name="is_banned")
    private Boolean isBanned;

    @OneToOne(mappedBy = "agent")
    private BannedCount bannedCount;


    public UserAgent() {
    }

    public UserAgent(User user, String ip, Boolean isBanned, BannedCount bannedCount) {
        this.user = user;
        this.ip = ip;
        this.isBanned = isBanned;
        this.bannedCount = bannedCount;
    }
}
