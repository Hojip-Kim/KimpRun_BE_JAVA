package kimp.chat.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_tracking")
@Getter
@NoArgsConstructor
public class ChatTracking extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = true)
    private String uuid;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "member_id", nullable = true)
    private Long memberId;

    @Column(name = "is_authenticated", nullable = false)
    private Boolean isAuthenticated;

    public ChatTracking(String uuid, String nickname, Long memberId, Boolean isAuthenticated) {
        this.uuid = uuid;
        this.nickname = nickname;
        this.memberId = memberId;
        this.isAuthenticated = isAuthenticated;
    }

    public ChatTracking updateNickname(String newNickname) {
        this.nickname = newNickname;
        return this;
    }
}