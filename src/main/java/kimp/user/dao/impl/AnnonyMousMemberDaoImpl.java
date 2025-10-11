package kimp.user.dao.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.AnnonyMousMemberDao;
import kimp.user.entity.AnnonyMousMember;
import kimp.user.repository.user.AnnonyMousMemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class AnnonyMousMemberDaoImpl implements AnnonyMousMemberDao {

    private final AnnonyMousMemberRepository annonyMousMemberRepository;

    public AnnonyMousMemberDaoImpl(AnnonyMousMemberRepository annonyMousMemberRepository) {
        this.annonyMousMemberRepository = annonyMousMemberRepository;
    }


    @Override
    public AnnonyMousMember createAnnonymousMember(String uuid, String ip) {
        try {
            AnnonyMousMember annonymousMember = new AnnonyMousMember(uuid, ip);
            AnnonyMousMember createdAnnoynousMember = annonyMousMemberRepository.save(annonymousMember);
            return createdAnnoynousMember;
        }catch(Exception e){
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "data process exception occurred.", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public AnnonyMousMember getAnnonymousMemberByUuid(String uuid) {
        try {
            Optional<AnnonyMousMember> annonyMousMember = annonyMousMemberRepository.findAnonymousMemberByMemberUuid(uuid);
            if(annonyMousMember.isPresent()){
                return annonyMousMember.get();
            }
        }catch(Exception e){
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "data process exception occurred.", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return null;
    }

    @Override
    public AnnonyMousMember getAnnonymousMemberByIp(String ip) {
        try {
            Optional<AnnonyMousMember> annonyMousMember = annonyMousMemberRepository.findAnonymousMemberByMemberIp(ip);
            if(annonyMousMember.isPresent()){
                return annonyMousMember.get();
            }
        }catch(Exception e){
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "data process exception occurred.", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAnnonymousMember(String uuid) {
        try {
            Optional<AnnonyMousMember> annonyMousMember = annonyMousMemberRepository.findAnonymousMemberByMemberUuid(uuid);
            if(annonyMousMember.isPresent()){
                annonyMousMemberRepository.delete(annonyMousMember.get());
            }
        }catch(Exception e){
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "data process exception occurred.", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public List<AnnonyMousMember> getAllAnnonymousMember() {
        try {
            List<AnnonyMousMember> annonyMousMembers = annonyMousMemberRepository.findAll();
            return annonyMousMembers;
        } catch (Exception e) {
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "data process exception occurred.", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<AnnonyMousMember> getAllAnnonymousMemberBeforeExpireTime(Long expireTime) {
        try {
            List<AnnonyMousMember> annonyMousMembers = annonyMousMemberRepository.findAllByBannedExpiryTimeBeforeAndIsBannedTrue(expireTime);
            if(annonyMousMembers != null && annonyMousMembers.size() > 0){
                return annonyMousMembers;
            }else {
                return List.of();
            }
        }catch (Exception e){
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "data process exception occurred.", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
