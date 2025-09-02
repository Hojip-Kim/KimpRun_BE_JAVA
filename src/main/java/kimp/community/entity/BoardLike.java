package kimp.community.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.user.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_like", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"board_id", "member_id"}))
@Getter
@NoArgsConstructor
public class BoardLike extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public BoardLike(Board board, Member member) {
        this.board = board;
        this.member = member;
    }
}