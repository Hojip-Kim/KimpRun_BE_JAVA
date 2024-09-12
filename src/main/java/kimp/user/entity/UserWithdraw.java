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

    @Column(name="is_withdraw")
    private Boolean isWithdraw;

    public UserWithdraw() {}

    public UserWithdraw(User user, Boolean isWithdraw) {
        this.user = user;
        this.isWithdraw = isWithdraw;
    }
}
