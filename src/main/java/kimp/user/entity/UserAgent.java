package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;

@Entity
@Table(name = "member_agent")
@Getter
public class UserAgent extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = true)
    private String ip = null;

    @Column(name="is_banned")
    private Boolean isBanned = false;

    @OneToOne(mappedBy = "memberAgent")
    private BannedCount bannedCount;


    public UserAgent() {
    }

    public UserAgent(Member member) {
        this.member = member;
    }

    public UserAgent setMember(Member member) {
        this.member = member;
        return this;
    }

    public UserAgent setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public UserAgent setBanned(BannedCount bannedCount) {
        this.bannedCount = bannedCount;
        return this;
    }

    public void Banned() {
        this.isBanned = true;
    }

    public UserAgent setBannedCount(BannedCount bannedCount) {
        this.bannedCount = bannedCount;
        return this;
    }

    public void unBanned() {
        this.isBanned = false;
    }
}
