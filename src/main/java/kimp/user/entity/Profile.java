package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;

@Entity
@Table(name = "profile")
@Getter
public class Profile extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

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

    public Profile(Member member, String imageUrl, SeedMoneyRange seedRange, ActivityRank activityRank) {
        this.member = member;
        this.imageUrl = imageUrl;
        this.seedRange = seedRange;
        this.activityRank = activityRank;
    }
}
