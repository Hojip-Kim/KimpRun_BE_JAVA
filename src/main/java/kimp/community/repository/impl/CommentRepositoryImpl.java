package kimp.community.repository.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.community.repository.CommentRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static kimp.community.entity.QComment.comment;
import static kimp.user.entity.QMember.member;
import static kimp.community.entity.QCommentLikeCount.commentLikeCount;
import static kimp.community.entity.QBoard.board;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public Page<Comment> findByBoardWithMemberFetchJoin(Board board, Pageable pageable) {
        // Fetch Join을 사용하여 Comment, Member, CommentLikeCount를 한 번에 조회
        JPAQuery<Comment> query = queryFactory
                .selectFrom(comment)
                .leftJoin(comment.member, member).fetchJoin()
                .leftJoin(comment.likeCount, commentLikeCount).fetchJoin()
                .where(comment.board.eq(board))
                .orderBy(getOrderSpecifiers(pageable));
        
        // 전체 개수 조회
        Long total = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.board.eq(board))
                .fetchOne();
        
        // 페이징 적용
        List<Comment> comments = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        return new PageImpl<>(comments, pageable, total != null ? total : 0L);
    }
    
    @Override
    public Page<Comment> findByMemberIdWithAllFetchOrderByRegistedAtDesc(Long memberId, Pageable pageable) {
        // Fetch Join을 사용하여 Comment, Member, Board, CommentLikeCount를 한 번에 조회
        JPAQuery<Comment> query = queryFactory
                .selectFrom(comment)
                .leftJoin(comment.member, member).fetchJoin()
                .leftJoin(comment.board, board).fetchJoin()
                .leftJoin(comment.likeCount, commentLikeCount).fetchJoin()
                .where(comment.member.id.eq(memberId)
                    .and(comment.member.isActive.eq(true))
                    .and(comment.isDeleted.eq(false)))
                .orderBy(comment.registedAt.desc());
        
        // 전체 개수 조회 (fetchCount() deprecated 해결)
        Long total = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.member.id.eq(memberId)
                    .and(comment.member.isActive.eq(true))
                    .and(comment.isDeleted.eq(false)))
                .fetchOne();
        
        // 페이징 적용
        List<Comment> comments = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        return new PageImpl<>(comments, pageable, total != null ? total : 0L);
    }
    
    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[]{comment.registedAt.asc()};
        }
        
        return pageable.getSort().stream()
                .map(order -> {
                    String property = order.getProperty();
                    boolean isAscending = order.isAscending();
                    
                    switch (property) {
                        case "registedAt":
                            return isAscending ? comment.registedAt.asc() : comment.registedAt.desc();
                        case "updatedAt":
                            return isAscending ? comment.updatedAt.asc() : comment.updatedAt.desc();
                        case "id":
                            return isAscending ? comment.id.asc() : comment.id.desc();
                        default:
                            return comment.registedAt.asc();
                    }
                })
                .toArray(OrderSpecifier[]::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countSoftDeletedCommentsBeforeDate(LocalDateTime beforeDate, Pageable pageable) {
        Long count = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.isDeleted.eq(true)
                    .and(comment.updatedAt.before(beforeDate)))
                .limit(pageable.getPageSize())
                .fetchOne();
        
        return count != null ? count : 0L;
    }
    
    @Override
    @Transactional
    public long deleteSoftDeletedCommentsBeforeDate(LocalDateTime beforeDate, Pageable pageable) {
        // 먼저 삭제할 ID들을 조회
        List<Long> idsToDelete = queryFactory
                .select(comment.id)
                .from(comment)
                .where(comment.isDeleted.eq(true)
                    .and(comment.updatedAt.before(beforeDate)))
                .limit(pageable.getPageSize())
                .fetch();
        
        if (idsToDelete.isEmpty()) {
            return 0L;
        }
        
        // 연관된 엔티티들 먼저 삭제 (외래키 제약조건 고려)
        // 1. CommentLikeCount 삭제
        queryFactory
                .delete(commentLikeCount)
                .where(commentLikeCount.comment.id.in(idsToDelete))
                .execute();
        
        // 2. Comment 삭제
        long deletedCount = queryFactory
                .delete(comment)
                .where(comment.id.in(idsToDelete))
                .execute();
        
        return deletedCount;
    }
}