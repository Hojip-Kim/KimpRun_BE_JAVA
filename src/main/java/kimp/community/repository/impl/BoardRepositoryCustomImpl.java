package kimp.community.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.entity.*;
import kimp.community.repository.BoardRepositoryCustom;
import kimp.user.entity.QMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BoardRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // QueryDSL Q클래스 인스턴스
    QBoard board = QBoard.board;
    QMember member = QMember.member;
    QCategory category = QCategory.category;
    QBoardViews boardViews = QBoardViews.boardViews;
    QBoardLikeCount boardLikeCount = QBoardLikeCount.boardLikeCount;
    QCommentCount commentCount = QCommentCount.commentCount;
    QComment comment = QComment.comment;

    @Override
    @Transactional(readOnly = true)
    public Page<Board> findByCategoryWithFetchJoinOrderByRegistedAtDesc(Category categoryParam, Pageable pageable) {
        // 메인 쿼리: fetch join으로 모든 연관관계를 한 번에 로딩
        List<Board> results = queryFactory
                .selectFrom(board)
                .leftJoin(board.member, member).fetchJoin()
                .leftJoin(board.category, category).fetchJoin()
                .leftJoin(board.views, boardViews).fetchJoin()
                .leftJoin(board.boardLikeCount, boardLikeCount).fetchJoin()
                .leftJoin(board.commentCount, commentCount).fetchJoin()
                .where(board.category.eq(categoryParam)
                    .and(member.isActive.eq(true))
                    .and(board.isDeleted.eq(false)))
                .orderBy(board.registedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        Long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .leftJoin(board.member, member)
                .where(board.category.eq(categoryParam)
                    .and(member.isActive.eq(true))
                    .and(board.isDeleted.eq(false)))
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount != null ? totalCount : 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Board> findAllWithFetchJoinOrderByRegistedAtDesc(Pageable pageable) {
        // 메인 쿼리: fetch join으로 모든 연관관계를 한 번에 로딩
        List<Board> results = queryFactory
                .selectFrom(board)
                .leftJoin(board.member, member).fetchJoin()
                .leftJoin(board.category, category).fetchJoin()
                .leftJoin(board.views, boardViews).fetchJoin()
                .leftJoin(board.boardLikeCount, boardLikeCount).fetchJoin()
                .leftJoin(board.commentCount, commentCount).fetchJoin()
                .where(member.isActive.eq(true))
                .orderBy(board.registedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        Long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .leftJoin(board.member, member)
                .where(member.isActive.eq(true))
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount != null ? totalCount : 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> findBoardDtosByCategoryOrderByRegistedAtDesc(Category categoryParam, Pageable pageable) {
        // DTO 직접 조회로 N+1 문제 완전 해결
        List<BoardResponseDto> results = queryFactory
                .select(Projections.constructor(BoardResponseDto.class,
                        board.id,
                        member.id,
                        category.id,
                        category.categoryName,
                        member.nickname,
                        board.title,
                        board.content, // full content - will be summarized in service layer
                        boardViews.views,
                        boardLikeCount.likes,
                        board.registedAt,
                        board.updatedAt,
                        commentCount.counts,
                        board.isPin
                ))
                .from(board)
                .leftJoin(board.member, member)
                .leftJoin(board.category, category)
                .leftJoin(board.views, boardViews)
                .leftJoin(board.boardLikeCount, boardLikeCount)
                .leftJoin(board.commentCount, commentCount)
                .where(board.category.eq(categoryParam)
                    .and(member.isActive.eq(true))
                    .and(board.isDeleted.eq(false)))
                .orderBy(board.registedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        Long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .leftJoin(board.member, member)
                .where(board.category.eq(categoryParam)
                    .and(member.isActive.eq(true))
                    .and(board.isDeleted.eq(false)))
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount != null ? totalCount : 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> findAllBoardDtosOrderByRegistedAtDesc(Pageable pageable) {
        // DTO 직접 조회로 N+1 문제 완전 해결
        List<BoardResponseDto> results = queryFactory
                .select(Projections.constructor(BoardResponseDto.class,
                        board.id,
                        member.id,
                        category.id,
                        category.categoryName,
                        member.nickname,
                        board.title,
                        board.content, // full content - will be summarized in service layer
                        boardViews.views,
                        boardLikeCount.likes,
                        board.registedAt,
                        board.updatedAt,
                        commentCount.counts,
                        board.isPin
                ))
                .from(board)
                .leftJoin(board.member, member)
                .leftJoin(board.category, category)
                .leftJoin(board.views, boardViews)
                .leftJoin(board.boardLikeCount, boardLikeCount)
                .leftJoin(board.commentCount, commentCount)
                .where(member.isActive.eq(true))
                .orderBy(board.registedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        Long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .leftJoin(board.member, member)
                .where(member.isActive.eq(true))
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount != null ? totalCount : 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> findBoardDtosByCategoryIdOrderByRegistedAtDesc(Long categoryId, Pageable pageable) {
        // DTO 직접 조회로 N+1 문제 완전 해결
        List<BoardResponseDto> results = queryFactory
                .select(Projections.constructor(BoardResponseDto.class,
                        board.id,
                        member.id,
                        category.id,
                        category.categoryName,
                        member.nickname,
                        board.title,
                        board.content, // full content - will be summarized in service layer
                        boardViews.views,
                        boardLikeCount.likes,
                        board.registedAt,
                        board.updatedAt,
                        commentCount.counts,
                        board.isPin
                ))
                .from(board)
                .leftJoin(board.member, member)
                .leftJoin(board.category, category)
                .leftJoin(board.views, boardViews)
                .leftJoin(board.boardLikeCount, boardLikeCount)
                .leftJoin(board.commentCount, commentCount)
                .where(board.category.id.eq(categoryId)
                    .and(member.isActive.eq(true))
                    .and(board.isDeleted.eq(false)))
                .orderBy(board.registedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        Long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .leftJoin(board.member, member)
                .where(board.category.id.eq(categoryId)
                    .and(member.isActive.eq(true))
                    .and(board.isDeleted.eq(false)))
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount != null ? totalCount : 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> findAllBoardDtosWithPinnedFirstOrderByRegistedAtDesc(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int maxPinnedCount = Math.min(10, pageSize);
        
        // 1. 공지사항 조회 (isPin = true, 최대 10개)
        List<BoardResponseDto> pinnedBoards = queryFactory
                .select(Projections.constructor(BoardResponseDto.class,
                        board.id,
                        member.id,
                        category.id,
                        category.categoryName,
                        member.nickname,
                        board.title,
                        board.content,
                        boardViews.views,
                        boardLikeCount.likes,
                        board.registedAt,
                        board.updatedAt,
                        commentCount.counts,
                        board.isPin
                ))
                .from(board)
                .leftJoin(board.member, member)
                .leftJoin(board.category, category)
                .leftJoin(board.views, boardViews)
                .leftJoin(board.boardLikeCount, boardLikeCount)
                .leftJoin(board.commentCount, commentCount)
                .where(board.isPin.eq(true)
                    .and(member.isActive.eq(true))
                    .and(board.isDeleted.eq(false)))
                .orderBy(board.registedAt.desc())
                .limit(maxPinnedCount)
                .fetch();

        // 2. 일반 게시물 조회 (isPin = false, 나머지 공간 채움)
        int normalBoardCount = pageSize - pinnedBoards.size();
        List<BoardResponseDto> normalBoards = queryFactory
                .select(Projections.constructor(BoardResponseDto.class,
                        board.id,
                        member.id,
                        category.id,
                        category.categoryName,
                        member.nickname,
                        board.title,
                        board.content,
                        boardViews.views,
                        boardLikeCount.likes,
                        board.registedAt,
                        board.updatedAt,
                        commentCount.counts,
                        board.isPin
                ))
                .from(board)
                .leftJoin(board.member, member)
                .leftJoin(board.category, category)
                .leftJoin(board.views, boardViews)
                .leftJoin(board.boardLikeCount, boardLikeCount)
                .leftJoin(board.commentCount, commentCount)
                .where(board.isPin.eq(false)
                    .and(member.isActive.eq(true))
                    .and(board.isDeleted.eq(false)))
                .orderBy(board.registedAt.desc())
                .limit(normalBoardCount)
                .fetch();

        // 3. 결과 합치기 (공지사항 먼저, 그 다음 일반 게시물)
        List<BoardResponseDto> results = new java.util.ArrayList<>();
        results.addAll(pinnedBoards);
        results.addAll(normalBoards);

        // 전체 게시물 수 (페이지네이션을 위해)
        Long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .leftJoin(board.member, member)
                .where(member.isActive.eq(true))
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount != null ? totalCount : 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> findBoardDtosByCategoryIdWithPinnedFirstOrderByRegistedAtDesc(Long categoryId, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int maxPinnedCount = Math.min(10, pageSize);
        
        // 1. 전체에서 공지사항 조회 (isPin = true, 최대 10개) - 카테고리 제한 없음
        List<BoardResponseDto> pinnedBoards = queryFactory
                .select(Projections.constructor(BoardResponseDto.class,
                        board.id,
                        member.id,
                        category.id,
                        category.categoryName,
                        member.nickname,
                        board.title,
                        board.content,
                        boardViews.views,
                        boardLikeCount.likes,
                        board.registedAt,
                        board.updatedAt,
                        commentCount.counts,
                        board.isPin
                ))
                .from(board)
                .leftJoin(board.member, member)
                .leftJoin(board.category, category)
                .leftJoin(board.views, boardViews)
                .leftJoin(board.boardLikeCount, boardLikeCount)
                .leftJoin(board.commentCount, commentCount)
                .where(board.isPin.eq(true) // 카테고리 조건 제거 - 전체에서 공지사항 조회
                    .and(member.isActive.eq(true))
                    .and(board.isDeleted.eq(false)))
                .orderBy(board.registedAt.desc())
                .limit(maxPinnedCount)
                .fetch();

        // 2. 카테고리별 일반 게시물 조회 (isPin = false, 나머지 공간 채움)
        int normalBoardCount = pageSize - pinnedBoards.size();
        List<BoardResponseDto> normalBoards = queryFactory
                .select(Projections.constructor(BoardResponseDto.class,
                        board.id,
                        member.id,
                        category.id,
                        category.categoryName,
                        member.nickname,
                        board.title,
                        board.content,
                        boardViews.views,
                        boardLikeCount.likes,
                        board.registedAt,
                        board.updatedAt,
                        commentCount.counts,
                        board.isPin
                ))
                .from(board)
                .leftJoin(board.member, member)
                .leftJoin(board.category, category)
                .leftJoin(board.views, boardViews)
                .leftJoin(board.boardLikeCount, boardLikeCount)
                .leftJoin(board.commentCount, commentCount)
                .where(board.category.id.eq(categoryId).and(board.isPin.eq(false))
                    .and(member.isActive.eq(true))
                    .and(board.isDeleted.eq(false)))
                .orderBy(board.registedAt.desc())
                .limit(normalBoardCount)
                .fetch();

        // 3. 결과 합치기 (공지사항 먼저, 그 다음 일반 게시물)
        List<BoardResponseDto> results = new java.util.ArrayList<>();
        results.addAll(pinnedBoards);
        results.addAll(normalBoards);

        // 카테고리별 전체 게시물 수
        Long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .leftJoin(board.member, member)
                .where(board.category.id.eq(categoryId)
                    .and(member.isActive.eq(true))
                    .and(board.isDeleted.eq(false)))
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount != null ? totalCount : 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> findBoardDtosByMemberOrderByRegistedAtDesc(Long memberId, Pageable pageable) {
        // DTO 직접 조회로 N+1 문제 완전 해결
        List<BoardResponseDto> results = queryFactory
                .select(Projections.constructor(BoardResponseDto.class,
                        board.id,
                        member.id,
                        category.id,
                        category.categoryName,
                        member.nickname,
                        board.title,
                        board.content, // full content - will be summarized in service layer
                        boardViews.views,
                        boardLikeCount.likes,
                        board.registedAt,
                        board.updatedAt,
                        commentCount.counts,
                        board.isPin
                ))
                .from(board)
                .leftJoin(board.member, member)
                .leftJoin(board.category, category)
                .leftJoin(board.views, boardViews)
                .leftJoin(board.boardLikeCount, boardLikeCount)
                .leftJoin(board.commentCount, commentCount)
                .where(board.member.id.eq(memberId)
                    .and(member.isActive.eq(true))
                    .and(board.isDeleted.eq(false)))
                .orderBy(board.registedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        Long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .leftJoin(board.member, member)
                .where(board.member.id.eq(memberId)
                    .and(member.isActive.eq(true))
                    .and(board.isDeleted.eq(false)))
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount != null ? totalCount : 0L);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countSoftDeletedBoardsBeforeDate(LocalDateTime beforeDate, Pageable pageable) {
        Long count = queryFactory
                .select(board.count())
                .from(board)
                .where(board.isDeleted.eq(true)
                    .and(board.updatedAt.before(beforeDate)))
                .limit(pageable.getPageSize())
                .fetchOne();
        
        return count != null ? count : 0L;
    }
    
    @Override
    @Transactional
    public long deleteSoftDeletedBoardsBeforeDate(LocalDateTime beforeDate, Pageable pageable) {
        // 삭제할 ID들 조회
        List<Long> idsToDelete = queryFactory
                .select(board.id)
                .from(board)
                .where(board.isDeleted.eq(true)
                    .and(board.updatedAt.before(beforeDate)))
                .limit(pageable.getPageSize())
                .fetch();
        
        if (idsToDelete.isEmpty()) {
            return 0L;
        }
        
        // 연관된 엔티티들 먼저 삭제 (외래키 제약조건 고려)
        // 1. CommentLikeCount 삭제 (Comment 삭제 전)
        queryFactory
                .delete(QCommentLikeCount.commentLikeCount)
                .where(QCommentLikeCount.commentLikeCount.comment.board.id.in(idsToDelete))
                .execute();
        
        // 2. Comment 삭제
        queryFactory
                .delete(comment)
                .where(comment.board.id.in(idsToDelete))
                .execute();
        
        // 3. BoardViews 삭제
        queryFactory
                .delete(boardViews)
                .where(boardViews.board.id.in(idsToDelete))
                .execute();
        
        // 4. BoardLikeCount 삭제
        queryFactory
                .delete(boardLikeCount)
                .where(boardLikeCount.board.id.in(idsToDelete))
                .execute();
        
        // 5. CommentCount 삭제
        queryFactory
                .delete(commentCount)
                .where(commentCount.board.id.in(idsToDelete))
                .execute();
        
        // 6. Board 삭제
        long deletedCount = queryFactory
                .delete(board)
                .where(board.id.in(idsToDelete))
                .execute();
        
        return deletedCount;
    }
}