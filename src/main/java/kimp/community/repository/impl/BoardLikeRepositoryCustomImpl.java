package kimp.community.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kimp.community.entity.QBoard;
import kimp.community.entity.QBoardLike;
import kimp.community.entity.QBoardLikeCount;
import kimp.community.repository.BoardLikeRepositoryCustom;
import kimp.user.entity.QMember;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BoardLikeRepositoryCustomImpl implements BoardLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    QBoardLike boardLike = QBoardLike.boardLike;
    QBoard board = QBoard.board;
    QMember member = QMember.member;
    QBoardLikeCount boardLikeCount = QBoardLikeCount.boardLikeCount;

    public BoardLikeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.entityManager = em;
    }

    @Override
    public boolean existsByBoardIdAndMemberId(Long boardId, Long memberId) {
        Long count = queryFactory
                .select(boardLike.count())
                .from(boardLike)
                .where(boardLike.board.id.eq(boardId)
                        .and(boardLike.member.id.eq(memberId)))
                .fetchOne();
        
        return count != null && count > 0;
    }

    @Override
    @Transactional
    public void addLikeIfNotExists(Long boardId, Long memberId) {
        boolean exists = existsByBoardIdAndMemberId(boardId, memberId);
        
        if (!exists) {
            entityManager.createNativeQuery(
                "INSERT INTO board_like (board_id, member_id, registed_at, updated_at) " +
                "VALUES (?1, ?2, NOW(), NOW())")
                .setParameter(1, boardId)
                .setParameter(2, memberId)
                .executeUpdate();
            
            queryFactory
                .update(boardLikeCount)
                .set(boardLikeCount.likes, boardLikeCount.likes.add(1))
                .where(boardLikeCount.board.id.eq(boardId))
                .execute();
        }
    }
}