package kimp.user.dao;

import kimp.user.entity.Member;

public interface MemberDao {

    public Member findMemberById(Long id);
    public Member findMemberByEmail(String email);
    public Member createMember(String email, String nickname, String password);
    public Member updateMember(Member member, String newHashedPassword);
    public Boolean deletemember(Long id);
}
