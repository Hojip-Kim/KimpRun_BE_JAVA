package kimp.user.dao.impl;

import kimp.user.dao.MemberWithDrawDao;
import kimp.user.entity.Member;
import kimp.user.entity.MemberWithdraw;
import kimp.user.repository.MemberWithDrawRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MemberWithDrawDaoImpl implements MemberWithDrawDao {

    private final MemberWithDrawRepository memberWithDrawRepository;

    public MemberWithDrawDaoImpl(MemberWithDrawRepository memberWithDrawRepository) {
        this.memberWithDrawRepository = memberWithDrawRepository;
    }

    @Override
    public MemberWithdraw createMemberWithDraw(Member member) {
        if(member == null) {
            throw new IllegalArgumentException("member is null");
        }

        MemberWithdraw memberWithdraw = new MemberWithdraw(member);

        return memberWithDrawRepository.save(memberWithdraw);
    }
}
