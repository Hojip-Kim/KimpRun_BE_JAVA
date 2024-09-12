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
    @JoinColumn(name = "user_agent_id")
    private UserAgent agent;

    @Column
    private Integer count;

    public BannedCount() {
    }

    public BannedCount(Long id, UserAgent agent, Integer count) {
        this.id = id;
        this.agent = agent;
        this.count = count;
    }
}
