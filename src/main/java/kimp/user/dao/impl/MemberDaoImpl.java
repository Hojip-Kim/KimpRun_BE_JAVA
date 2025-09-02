package kimp.user.dao.impl;

import kimp.user.dao.MemberDao;
import kimp.user.entity.Member;
import kimp.user.entity.MemberRole;
import kimp.user.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Repository
@Slf4j
public class MemberDaoImpl implements MemberDao {

    private final MemberRepository memberRepository;

    public MemberDaoImpl(MemberRepository memberRepository) {
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
    @Transactional(readOnly = true)
    public Member findMemberByIdWithProfile(Long id){
        Optional<Member> member = this.memberRepository.findByIdWithProfile(id);

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
    public Member findMemberByOAuthProviderId(String provider, String providerId){
        Optional<Member> member = this.memberRepository.findByOauthProviderAndOauthProviderId(provider, providerId);
        if(member.isEmpty()){
            return null;
        }
        return member.get();
    }

    @Override
    @Transactional
    public Member createMember(String email, String nickname, String password, MemberRole role){
//        Optional<member> member = this.memberRepository.findByEmail(email);
//        if (member.isPresent()) {
//            throw new IllegalArgumentException("member already exists");
//        }
        Member createdMember = new Member(email, nickname, password, role);
        return this.memberRepository.save(createdMember);
    }

    @Override
    public Member updateMember(Member member, String newHashedPassword) {
        member.updatePassword(newHashedPassword);

        return this.memberRepository.save(member);
    }

    @Override
    public Member findActiveMemberById(Long id){
        Optional<Member> member = this.memberRepository.findByIdAndIsActiveTrue(id);

        if(member.isEmpty()){
            throw new IllegalArgumentException("active member not found");
        }

        return member.get();
    }

    @Override
    @Transactional(readOnly = true)
    public Member findActiveMemberByIdWithProfile(Long id){
        Optional<Member> member = this.memberRepository.findByIdWithProfileAndIsActiveTrue(id);

        if(member.isEmpty()){
            throw new IllegalArgumentException("active member not found");
        }

        return member.get();
    }

    @Override
    public Member findActiveMemberByEmail(String email){
        Optional<Member> member = this.memberRepository.findByEmailAndIsActiveTrue(email);
        if(member.isEmpty()){
            return null;
        }
        return member.get();
    }

    @Override
    public Member findActiveMemberByOAuthProviderId(String provider, String providerId){
        Optional<Member> member = this.memberRepository.findByOauthProviderAndOauthProviderIdAndIsActiveTrue(provider, providerId);
        if(member.isEmpty()){
            return null;
        }
        return member.get();
    }

    @Override
    public Boolean deleteMember(Long id) {
        Member member = findMemberById(id);
        this.memberRepository.delete(member);

        Member findMember = findMemberById(id);
        if(findMember == null){
            return false;
        }

        return true;
    }

    @Override
    public boolean isExistsByNickname(String name) {

        return this.memberRepository.existsMemberByNickname(name);
    }
}
