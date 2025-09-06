package kimp.user.repository;

import kimp.user.entity.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {
    
    /**
     * 로그인용 최적화된 Member 조회 (모든 연관 엔티티를 한 번에 fetch)
     */
    Optional<Member> findActiveMemberByEmailOptimized(String email);
}