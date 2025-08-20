package kimp.user.dao;

import kimp.user.entity.Member;
import kimp.user.entity.MemberRole;

public interface MemberDao {

    public Member findMemberById(Long id);
    public Member findMemberByEmail(String email);
    public Member findMemberByOAuthProviderId(String provider, String providerId);
    public Member createMember(String email, String nickname, String password, MemberRole role);
    public Member updateMember(Member member, String newHashedPassword);
    public Boolean deleteMember(Long id);
}
