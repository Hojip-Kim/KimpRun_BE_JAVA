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

    @Column
    private String image_url;

    @ManyToOne
    @JoinColumn(name = "seed_range_key", referencedColumnName = "seed_range_key")
    private SeedMoneyRange seed_range;

    @ManyToOne
    @JoinColumn(name = "activity_rank_key", referencedColumnName = "rank_key")
    private ActivityRank activity_rank;

    public Profile() {
    }

    public Profile(User user, String nickname, String image_url, SeedMoneyRange seed_range, ActivityRank activity_rank) {
        this.user = user;
        this.nickname = nickname;
        this.image_url = image_url;
        this.seed_range = seed_range;
        this.activity_rank = activity_rank;
    }
}
