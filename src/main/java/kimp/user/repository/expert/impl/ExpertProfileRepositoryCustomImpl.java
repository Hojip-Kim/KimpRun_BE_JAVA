package kimp.user.repository.expert.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kimp.user.entity.ExpertProfile;
import kimp.user.entity.QExpertProfile;
import kimp.user.repository.expert.ExpertProfileRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ExpertProfileRepositoryCustomImpl implements ExpertProfileRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ExpertProfileRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public Page<ExpertProfile> findExpertProfilePageByIsActive(Boolean isActive, Pageable pageable) {
        QExpertProfile qExpertProfile = QExpertProfile.expertProfile;

        List<ExpertProfile> content = queryFactory
                .selectFrom(qExpertProfile)
                .where(qExpertProfile.isActive.eq(isActive))
                .orderBy(qExpertProfile.articlesCount.desc(), qExpertProfile.followersCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qExpertProfile.count())
                .from(qExpertProfile)
                .where(qExpertProfile.isActive.eq(isActive))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    @Transactional
    public Page<ExpertProfile> findAllExpertProfilePage(Pageable pageable) {
        QExpertProfile qExpertProfile = QExpertProfile.expertProfile;

        List<ExpertProfile> content = queryFactory
                .selectFrom(qExpertProfile)
                .orderBy(qExpertProfile.articlesCount.desc(), qExpertProfile.followersCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qExpertProfile.count())
                .from(qExpertProfile)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
