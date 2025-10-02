package kimp.user.service.impl;

import kimp.cdn.component.Cdn;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.AnnonyMousMemberDao;
import kimp.user.dto.request.*;
import kimp.user.dto.response.AnnonymousMemberResponseDto;
import kimp.user.entity.AnnonyMousMember;
import kimp.user.enums.BanType;
import kimp.user.service.AnnonyMousService;
import kimp.user.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@Slf4j
public class AnnonyMousServiceImpl implements AnnonyMousService {

    private final AnnonyMousMemberDao annonyMousMemberDao;
    private final Cdn cdn;

    public AnnonyMousServiceImpl(AnnonyMousMemberDao annonyMousMemberDao,@Qualifier("cloudflare") Cdn cdn) {
        this.annonyMousMemberDao = annonyMousMemberDao;
        this.cdn = cdn;
    }


    @Override
    public AnnonymousMemberResponseDto createAnnonymousMember(String uuid, String ip) {
        AnnonyMousMember annonymousMember = annonyMousMemberDao.createAnnonymousMember(uuid, ip);
        if(annonymousMember == null){
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "data process exception occurred.", HttpStatus.INTERNAL_SERVER_ERROR, "AnnonyMousServiceImpl.createAnnonymousMember");
        }
        LocalDateTime banStartTime = Instant.ofEpochMilli(annonymousMember.getBannedStartTime()).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime banEndTime = Instant.ofEpochMilli(annonymousMember.getBannedExpiryTime()).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        return new AnnonymousMemberResponseDto(annonymousMember.getMemberUuid(), annonymousMember.getMemberIp(), annonymousMember.getApplicationBannedCount(), annonymousMember.getCdnBannedCount(), annonymousMember.getIsBanned(), null, banStartTime, banEndTime);
    }

    @Override
    @Transactional
    public AnnonymousMemberResponseDto updateAnnonymousMemberIp(UpdateAnonNicknameVo vo) {
        String uuid = vo.getUuid();
        String ip = vo.getNickname();
        AnnonyMousMember annonymousMember = annonyMousMemberDao.getAnnonymousMemberByUuid(uuid);
        if(annonymousMember == null){
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "not have matched Uuid annonymousMember.", HttpStatus.INTERNAL_SERVER_ERROR, "AnnonyMousServiceImpl.updateAnnonymousMemberIp");
        }
        annonymousMember.updateMemberIp(ip);

        return null;
    }

    @Override
    public AnnonymousMemberResponseDto getAnnonymousMemberByUuidOrIp(GetAnnonymousMemberInfoVo vo) {
        AnnonymousMemberInfoRequestDto request = vo.getRequest();
        String memberIp = request.getIp();
        String memberUuid = request.getUuid();

        // 하나라도 null인 경우는 없음.
        if(memberIp == null || memberUuid == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION, "ip or uuid cannot be null", HttpStatus.BAD_REQUEST, "AnnonyMousServiceImpl.getAnnonymousMemberByUuidOrIp");
        }

        // 처음엔 uuid로 확인, uuid로 데이터베이스 확인하였을 때 존재하지않으면 uuid가 변조된 것.
        // -> ip로 탐색
        AnnonyMousMember annonymousMember;

        annonymousMember= annonyMousMemberDao.getAnnonymousMemberByUuid(memberUuid);

        // ip로 탐색
        if(annonymousMember == null){
            annonymousMember = annonyMousMemberDao.getAnnonymousMemberByIp(memberIp);
        }

        LocalDateTime banStartTime = Instant.ofEpochMilli(annonymousMember.getBannedStartTime()).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime banEndTime = Instant.ofEpochMilli(annonymousMember.getBannedExpiryTime()).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        String banType = annonymousMember.getBanType() == null ? null : annonymousMember.getBanType().getName();

        return new AnnonymousMemberResponseDto(annonymousMember.getMemberUuid(), annonymousMember.getMemberIp(), annonymousMember.getApplicationBannedCount(), annonymousMember.getCdnBannedCount(), annonymousMember.getIsBanned(), banType ,banStartTime, banEndTime);
    }

    @Override
    @Transactional
    public void applicationBanMember(ApplicationBanMemberVo vo) {
        ApplicationBanMemberRequestDto request = vo.getRequest();
        AnnonyMousMember annonyMousMember = annonyMousMemberDao.getAnnonymousMemberByUuid(request.getUuid());
        if(annonyMousMember == null) {
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "not have matched Uuid annonymousMember.", HttpStatus.INTERNAL_SERVER_ERROR, "AnnonyMousServiceImpl.applicationBanMember");
        }
        annonyMousMember.applicationBanned(BanType.APPLICATION);
    }

    @Override
    @Transactional
    public void applicationUnBanMember(ApplicationUnBanMemberVo vo) {
        ApplicationUnBanMemberRequestDto request = vo.getRequest();
        AnnonyMousMember annonyMousMember = annonyMousMemberDao.getAnnonymousMemberByUuid(request.getUuid());
        if(annonyMousMember == null) {
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "not have matched Uuid annonymousMember.", HttpStatus.INTERNAL_SERVER_ERROR, "AnnonyMousServiceImpl.applicationUnBanMember");
        }
        annonyMousMember.applicationUnBanned();
    }

    @Override
    @Transactional
    public void cdnBanMember(CdnBanMemberVo vo) {
        CdnBanMemberRequestDto request = vo.getRequest();
        AnnonyMousMember annonyMousMember = annonyMousMemberDao.getAnnonymousMemberByUuid(request.getUuid());
        if(annonyMousMember == null) {
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "not have matched Uuid annonymousMember.", HttpStatus.INTERNAL_SERVER_ERROR, "AnnonyMousServiceImpl.cdnBanMember");
        }
        annonyMousMember.cdnBanned(BanType.CDN);
        String cdnRuleId = cdn.requestIpBan(request.getIp(), "정책을 위반함.");
        annonyMousMember.setCfRuleId(cdnRuleId);
    }

    @Override
    @Transactional
    public void cdnUnBanMember(CdnUnbanMemberVo vo) {
        CdnUnbanMemberRequestDto request = vo.getRequest();
        AnnonyMousMember annonyMousMember = annonyMousMemberDao.getAnnonymousMemberByUuid(request.getUuid());
        if(annonyMousMember == null) {
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "not have matched Uuid annonymousMember.", HttpStatus.INTERNAL_SERVER_ERROR, "AnnonyMousServiceImpl.cdnUnBanMember");
        }

        annonyMousMember.cdnUnBanned();
        String cdnRuleId = annonyMousMember.getCfRuleId();
        if(cdnRuleId == null){
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "cdn rule id is null.", HttpStatus.INTERNAL_SERVER_ERROR, "AnnonyMousServiceImpl.cdnUnBanMember");
        }
        cdn.deleteCloudflareRule(cdnRuleId);
    }

    @Override
    public void deleteAnnonymousMember(String uuid) {
        annonyMousMemberDao.deleteAnnonymousMember(uuid);
    }
}
