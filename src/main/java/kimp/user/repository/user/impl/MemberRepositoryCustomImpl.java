package kimp.user.repository.user.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kimp.user.entity.Member;
import kimp.user.entity.QMember;
import kimp.user.entity.QMemberWithdraw;
import kimp.user.entity.QUserAgent;
import kimp.user.entity.QBannedCount;
import kimp.user.entity.QProfile;
import kimp.user.entity.QActivityRank;
import kimp.user.entity.QSeedMoneyRange;
import kimp.user.entity.QOauth;
import kimp.user.repository.user.MemberRepositoryCustom;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    public MemberRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    
    @Override
    public Optional<Member> findActiveMemberByEmailOptimized(String email) {
        QMember member = QMember.member;
        QMemberWithdraw memberWithdraw = QMemberWithdraw.memberWithdraw;
        QUserAgent userAgent = QUserAgent.userAgent;
        QBannedCount bannedCount = QBannedCount.bannedCount;
        QProfile profile = QProfile.profile;
        QActivityRank activityRank = QActivityRank.activityRank;
        QSeedMoneyRange seedMoneyRange = QSeedMoneyRange.seedMoneyRange;
        QOauth oauth = QOauth.oauth;
        
        Member result = queryFactory
                .selectFrom(member)
                .leftJoin(member.role).fetchJoin()
                .leftJoin(member.MemberWithdraw, memberWithdraw).fetchJoin()
                .leftJoin(member.memberAgent, userAgent).fetchJoin()
                .leftJoin(userAgent.bannedCount, bannedCount).fetchJoin()
                .leftJoin(member.profile, profile).fetchJoin()
                .leftJoin(profile.activityRank, activityRank).fetchJoin()
                .leftJoin(profile.seedRange, seedMoneyRange).fetchJoin()
                .leftJoin(member.oauth, oauth).fetchJoin()
                .where(member.email.eq(email)
                       .and(member.isActive.eq(true)))
                .fetchOne();
        
        return Optional.ofNullable(result);
    }
}