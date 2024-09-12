package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;

@Entity
@Table(name = "profile")
@Getter
public class Profile extends TimeStamp {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String nickname;

    @Column(name="image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "seed_range_key", referencedColumnName = "seed_range_key")
    private SeedMoneyRange seedRange;

    @ManyToOne
    @JoinColumn(name = "activity_rank_key", referencedColumnName = "rank_key")
    private ActivityRank activityRank;

    public Profile() {
    }

    public Profile(User user, String nickname, String imageUrl, SeedMoneyRange seedRange, ActivityRank activityRank) {
        this.user = user;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.seedRange = seedRange;
        this.activityRank = activityRank;
    }
}
