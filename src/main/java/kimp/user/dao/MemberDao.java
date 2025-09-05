package kimp.user.dao;

import kimp.user.entity.Member;
import kimp.user.entity.MemberRole;

public interface MemberDao {

    public Member findMemberById(Long id);
    public Member findMemberByIdWithProfile(Long id);
    public Member findMemberByEmail(String email);
    public Member findMemberByOAuthProviderId(String provider, String providerId);
    
    public Member findActiveMemberById(Long id);
    public Member findActiveMemberByIdWithProfile(Long id);
    public Member findActiveMemberForNicknameUpdate(Long id);
    public Member findActiveMemberByEmail(String email);
    public Member findActiveMemberByOAuthProviderId(String provider, String providerId);
    
    public Member createMember(String email, String nickname, String password, MemberRole role);
    public Member updateMember(Member member, String newHashedPassword);
    public Member updateNickname(Member member);
    public Boolean deleteMember(Long id);

    public boolean isExistsByNickname(String name);
    
    /**
     * 로그인용 최적화된 Member 조회 (모든 연관 엔티티를 한 번에 fetch)
     */
    public Member findActiveMemberByEmailOptimized(String email);
}
