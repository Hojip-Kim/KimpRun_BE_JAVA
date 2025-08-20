package kimp.user.scheduler.impl;

import kimp.cdn.component.Cdn;
import kimp.user.dao.AnnonyMousMemberDao;
import kimp.user.entity.AnnonyMousMember;
import kimp.user.enums.BanType;
import kimp.user.scheduler.AnnonymousScheduler;
import kimp.webhook.slack.SlackComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class AnnonymousSchedulerImpl implements AnnonymousScheduler {

    private final Cdn cdn;
    private final AnnonyMousMemberDao annoymousMemberDao;
    private final SlackComponent slackComponent;

    public AnnonymousSchedulerImpl(Cdn cdn, AnnonyMousMemberDao annoymousMemberDao, SlackComponent slackComponent) {
        this.cdn = cdn;
        this.annoymousMemberDao = annoymousMemberDao;
        this.slackComponent = slackComponent;
    }

    /**
     * 매 시간 정각마다 실행 시키는 스케쥴러
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireBans() {
        log.info("Starting expireBans scheduler task");
        
        try {
            long now = System.currentTimeMillis();
            List<AnnonyMousMember> bannedMembers = annoymousMemberDao.getAllAnnonymousMemberBeforeExpireTime(now);
            
            log.info("{}명의 밴 유저 찾기 완료", bannedMembers.size());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (AnnonyMousMember bannedMember : bannedMembers) {
                try {
                    unbanAnnonyMousMember(bannedMember.getBanType(), bannedMember);
                    successCount++;
                    log.debug("성공적으로 밴 풀기 완료: {} annonymous member id", bannedMember.getId());
                } catch (Exception e) {
                    failureCount++;
                    String errorMessage = String.format("밴 풀기 실패 AnnonyMous member ID: %d, BanType: %s",
                        bannedMember.getId(), bannedMember.getBanType());
                    
                    log.error(errorMessage, e);
                    
                    try {
                        slackComponent.sendSchedulerFailureMessage("AnnonymousScheduler.expireBans", errorMessage, e);
                    } catch (Exception slackException) {
                        log.error("Failed to send Slack notification for scheduler failure", slackException);
                    }
                }
            }
            
            log.info("ExpireBans scheduler completed. Success: {}, Failures: {}", successCount, failureCount);
            
            if (failureCount > 0) {
                String summaryMessage = String.format("ExpireBans scheduler completed with %d failures out of %d total members", 
                    failureCount, bannedMembers.size());
                try {
                    slackComponent.sendErrorMessage(summaryMessage, "AnnonymousScheduler.expireBans");
                } catch (Exception slackException) {
                    log.error("Failed to send Slack summary notification", slackException);
                }
            }
            
        } catch (Exception e) {
            String errorMessage = "Critical failure in expireBans scheduler";
            log.error(errorMessage, e);
            
            try {
                slackComponent.sendSchedulerFailureMessage("AnnonymousScheduler.expireBans", errorMessage, e);
            } catch (Exception slackException) {
                log.error("Failed to send critical Slack notification for scheduler failure", slackException);
            }
            
            throw e;
        }
    }

    private void unbanAnnonyMousMember(BanType banType, AnnonyMousMember bannedMember) {
        // 애플리케이션 밴이면
        if(BanType.APPLICATION.equals(banType)){
            bannedMember.applicationUnBanned();
        }else {
            // CDN 밴이면
            cdn.deleteCloudflareRule(bannedMember.getCfRuleId());
            // cdn에서 밴이 완료되었는지 정상 확인 후 밴 풀어주는 로직 추후 진행하여야 함.
            bannedMember.cdnUnBanned();
        }
    }

}
