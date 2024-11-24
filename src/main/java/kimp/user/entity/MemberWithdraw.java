package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;


@Entity
@Table(name = "member_withdraw")
@Getter
public class MemberWithdraw extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="member_id")
    private Member member;

    @Column(name="is_withdraw")
    private Boolean isWithdraw = false;

    public MemberWithdraw() {}

    public MemberWithdraw(Member member) {
        this.member = member;
    }
}
