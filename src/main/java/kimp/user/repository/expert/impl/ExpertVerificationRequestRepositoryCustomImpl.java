package kimp.user.repository.expert.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kimp.user.entity.ExpertVerificationRequest;
import kimp.user.entity.QExpertVerificationRequest;
import kimp.user.enums.VerificationStatus;
import kimp.user.repository.expert.ExpertVerificationRequestRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpertVerificationRequestRepositoryCustomImpl implements ExpertVerificationRequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ExpertVerificationRequestRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.queryFactory = jpaQueryFactory;
    }


    @Override
    public Page<ExpertVerificationRequest> findExpertVerificationRequestByStatus(VerificationStatus status, Pageable pageable) {
        QExpertVerificationRequest qExpertVerificationRequest = QExpertVerificationRequest.expertVerificationRequest;

        List<ExpertVerificationRequest> content = queryFactory
                .selectFrom(qExpertVerificationRequest)
                .where(qExpertVerificationRequest.status.eq(status))
                .orderBy(qExpertVerificationRequest.registedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qExpertVerificationRequest.count())
                .from(qExpertVerificationRequest)
                .where(qExpertVerificationRequest.status.eq(status))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<ExpertVerificationRequest> findAllExpertVerificationRequest(Pageable pageable) {
        QExpertVerificationRequest qExpertVerificationRequest = QExpertVerificationRequest.expertVerificationRequest;

        List<ExpertVerificationRequest> content = queryFactory
                .selectFrom(qExpertVerificationRequest)
                .orderBy(qExpertVerificationRequest.registedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qExpertVerificationRequest.count())
                .from(qExpertVerificationRequest)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
