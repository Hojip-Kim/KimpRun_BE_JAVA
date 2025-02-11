package kimp.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "banned_count")
@Getter
public class BannedCount {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_agent_id", nullable = false)
    private UserAgent memberAgent;

    @Column
    private Integer count = 0;

    public BannedCount() {
    }

    public BannedCount( UserAgent agent) {
        this.memberAgent = agent;
    }
}
