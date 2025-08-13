package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.user.enums.ApplicationBanTime;
import kimp.user.enums.BanType;
import kimp.user.enums.CdnBanTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Hojip-Kim
 * @version 1.0
 * @description Annonymous Member Entity, 로그인하지않은 유저에 대한 처리를 위한 엔티티
 * @since 2025-08-13
 */

@Entity
@Table(name = "annonymous_member",
        uniqueConstraints =
        @UniqueConstraint(name = "uk_annonymous_member_uuid_ip",
        columnNames = {"member_uuid", "member_ip"}))
@Getter
@NoArgsConstructor
public class AnnonyMousMember extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="member_uuid", unique = true)
    private String memberUuid;

    @Column(name="member_ip")
    private String memberIp;

    @Column(name="is_banned")
    private Boolean isBanned = false;

    @Column(name="banned_start_time")
    private Long bannedStartTime;

    // 1회 - 10분, 2회 - 1시간, 3회 - 6시간, 4회 - 24시간, 5회 - 1주일, 6회 - 한달, 7회 - 영구정지
    @Column(name="application_banned_count")
    private Integer applicationBannedCount = 0;

    // 1회 - 1일, 2회 - 7일, 3회 - 30일, 4회 - 영구정지
    @Column(name="cdn_banned_count")
    private Integer cdnBannedCount = 0;

    // -1이면 영구밴, 0이면 초기화
    @Column(name="banned_expiry_time")
    private Long bannedExpiryTime;

    @Column(name="ban_type")
    @Enumerated(EnumType.STRING)
    private BanType banType;

    @Column(name="cf_rule_id")
    private String cfRuleId;

    public AnnonyMousMember(String memberUuid, String memberIp) {
        this.memberUuid = memberUuid;
        this.memberIp = memberIp;
    }

    public AnnonyMousMember updateMemberIp(String memberIp) {
        this.memberIp = memberIp;
        return this;
    }

    /**
     * @param banType ban type (어플리케이션 / CDN)
     *                여기서는 어플리케이션 밴을 의미 (트래픽은 허용, 애플리케이션 단 쓰기작업 불가, 읽기작업 제한)
     * @return void
     */
    public void applicationBanned(BanType banType) {
        if(banType != BanType.APPLICATION){
            throw new IllegalArgumentException("banType is not APPLICATION");
        }
        this.isBanned = true;
        this.banType = banType;
        this.applicationBannedCount++;

        // 밴 시작시간 지정
        this.bannedStartTime = System.currentTimeMillis();
        // 밴 7회 영구정지 (어플리케이션)
        if(applicationBannedCount > 6) {
            this.bannedExpiryTime = -1L;
            return;
        }
        // 밴 종료시간 지정
        this.bannedExpiryTime = this.bannedStartTime + ApplicationBanTime.getBanTime(this.applicationBannedCount).getTime();
    }

    /**
     * @return void
     */
    public void applicationUnBanned() {
        this.isBanned = false;
        this.banType = null;
        this.bannedStartTime = null;
        this.bannedExpiryTime = null;
    }

    /**
     * @param banType ban type (어플리케이션 / CDN)
     *                여기서는 CDN 밴을 의미 (트래픽 조차 비허용)
     * @return void
     *
     * cdn 밴이 활성화 되면 cdn측으로 api요청을 보낸 후 해당 유저 지정 시간동안 트래픽차단
     */
    public void cdnBanned(BanType banType) {
        if(banType != BanType.CDN){
            throw new IllegalArgumentException("banType is not CDN");
        }
        this.isBanned = true;
        this.banType = banType;
        this.cdnBannedCount++;
        Long cdnBanTime = CdnBanTime.getBanTime(this.cdnBannedCount).getTime();
        this.bannedStartTime = System.currentTimeMillis();
        this.bannedExpiryTime = this.bannedStartTime + cdnBanTime;

    }

    /**
     * @return void
     */
    public void cdnUnBanned() {
        this.isBanned = false;
        this.banType = null;
        this.bannedStartTime = null;
        this.bannedExpiryTime = null;
    }

    /**
     * 관리자 단 어플리케이션 밴 초기화
     */
    public void resetApplicationBan() {
        this.applicationBannedCount = 0;
        this.bannedExpiryTime = null;
        this.bannedStartTime = null;
        this.banType = null;
        this.isBanned = false;
    }

    /**
     * 관리자 단 CDN 밴 초기화
     */
    public void resetCdnBan() {
        this.cdnBannedCount = 0;
        this.bannedExpiryTime = null;
        this.bannedStartTime = null;
        this.banType = null;
        this.isBanned = false;
    }

    /**
     * 어플리케이션 밴 카운트 초기화
     * 밴 관련 문제사항이 없는 유저 한정으로 어플리케이션 밴 카운트 초기화
     */
    public void resetApplicationBannedCount() {
        this.applicationBannedCount = 0;
    }
    /**
     * CDN 밴 카운트 초기화
     * 밴 관련 문제사항이 없는 유저 한정으로 CDN 밴 카운트 초기화
     */
    public void resetCdnBannedCount() {
        this.cdnBannedCount = 0;
    }

    public void setCfRuleId(String cfRuleId) {
        this.cfRuleId = cfRuleId;
    }

    public void deleteCfRuleId() {
        this.cfRuleId = null;
    }

}
