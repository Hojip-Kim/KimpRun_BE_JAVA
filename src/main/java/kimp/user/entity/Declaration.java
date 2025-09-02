package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 신고를 통해 어드민(관리자)가 수동으로 어플리케이션 밴을 먹이거나, cdn단 트래픽 밴을 먹일 수 있음.
// toMember의 uuid와 채팅 기록을 통해 비로그인 멤버 유추 가능(채팅에서 ip확인 가능 - 밴 가능)
// 물론, ip는 유동적이기때문에 뚫고 들어올 수 있음. 허나, 현재는 ip밴을 수동으로 하고, 추후 uuid 등을 통해 프론트엔드에서 자체적으로 redirection 기능 추가 예정
@Entity
@Table(name = "declaration")
@Getter
@NoArgsConstructor
public class Declaration extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // uuid형태로 들어오면 비로그인, id형태로 들어오면 로그인
    @Column(nullable = false)
    private String fromMember;

    @Column(nullable = true)
    private String fromMemberIp;

    // uuid형태로 들어오면 비로그인, id형태로 들어오면 로그인
    @Column(nullable = false)
    private String toMember;

    @Column(nullable = true)
    private String reason;

    public Declaration(String fromMember, String fromMemberIp, String toMember, String reason) {
        this.fromMember = fromMember;
        this.fromMemberIp = fromMemberIp;
        this.toMember = toMember;
        this.reason = reason;
    }
}
