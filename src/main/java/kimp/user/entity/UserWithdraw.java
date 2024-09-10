package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;


@Entity
@Table(name = "user_withdraw")
@Getter
public class UserWithdraw extends TimeStamp {

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column
    private Boolean is_withdraw;

    public UserWithdraw() {}

    public UserWithdraw(User user, Boolean is_withdraw) {
        this.user = user;
        this.is_withdraw = is_withdraw;
    }
}
