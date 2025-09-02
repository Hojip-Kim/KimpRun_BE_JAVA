package kimp.notice.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kimp.notice.entity.Notice;
import kimp.notice.repository.NoticeRepositoryCustom;
import kimp.market.Enum.MarketType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kimp.notice.entity.QNotice.notice;
import static kimp.exchange.entity.QExchange.exchange;
import static kimp.cmc.entity.exchange.QCmcExchange.cmcExchange;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Notice> findAllNoticesByMarketTypeWithFetch(MarketType marketType) {
        return queryFactory
            .selectFrom(notice)
            .join(notice.exchange, exchange).fetchJoin()
            .leftJoin(exchange.cmcExchange, cmcExchange).fetchJoin()
            .where(exchange.market.eq(marketType))
            .orderBy(notice.date.desc())
            .fetch();
    }
    
    @Override
    public Page<Notice> findAllByOrderByDateDescWithFetch(Pageable pageable) {
        List<Notice> content = queryFactory
            .selectFrom(notice)
            .join(notice.exchange, exchange).fetchJoin()
            .leftJoin(exchange.cmcExchange, cmcExchange).fetchJoin()
            .orderBy(notice.date.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
            
        Long total = queryFactory
            .select(notice.count())
            .from(notice)
            .join(notice.exchange, exchange)
            .fetchOne();
            
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}