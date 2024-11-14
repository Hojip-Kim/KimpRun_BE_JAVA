package kimp.user.dao.impl;

import kimp.user.dao.MemberDao;
import kimp.user.entity.Member;
import kimp.user.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Repository
@Slf4j
public class UserDaoImpl implements MemberDao {

    private final MemberRepository memberRepository;

    public UserDaoImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Member findMemberById(Long id){
        Optional<Member> member =  this.memberRepository.findById(id);

        if(member.isEmpty()){
            throw new IllegalArgumentException("member not found");
        }

        return member.get();
    }

    @Override
    public Member findMemberByEmail(String email){
        Optional<Member> member = this.memberRepository.findByEmail(email);
        if(member.isEmpty()){
            return null;
        }
        return member.get();
    }

    @Override
    @Transactional
    public Member createMember(String email, String nickname, String password){
//        Optional<member> member = this.memberRepository.findByEmail(email);
//        if (member.isPresent()) {
//            throw new IllegalArgumentException("member already exists");
//        }
        Member createdMember = new Member(email, nickname, password);
        return this.memberRepository.save(createdMember);
    }

    @Override
    public Member updateMember(Member member, String newHashedPassword) {
        member.updatePassword(newHashedPassword);

        return this.memberRepository.save(member);
    }

    @Override
    public Boolean deletemember(Long id) {
        Member member = findMemberById(id);
        this.memberRepository.delete(member);

        Member findMember = findMemberById(id);
        if(findMember == null){
            return false;
        }

        return true;
    }
}
