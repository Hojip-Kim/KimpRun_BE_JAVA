package kimp.user.dao;

import kimp.user.entity.Member;
import kimp.user.entity.MemberWithdraw;

public interface MemberWithDrawDao {

    public MemberWithdraw createMemberWithDraw(Member member);

}
