package kimp.user.enums;

import lombok.Getter;

@Getter
public enum CdnBanTime {
    // 1회 - 1일
    FIRST(1, 60*60*24L),
    // 2회 - 7일
    SECOND(2, 60*60*24*7L),
    // 3회 - 30일
    THIRD(3, 60*60*24*30L);

    private final Integer bannedCount;
    private final Long time;

    CdnBanTime(Integer bannedCount, Long time) {
        this.bannedCount = bannedCount;
        this.time = time;
    }

    public static CdnBanTime getBanTime(Integer bannedCount) {
        for (CdnBanTime cdnBanTime : CdnBanTime.values()) {
            if (cdnBanTime.getBannedCount().equals(bannedCount)) {
                return cdnBanTime;
            }
        }
        return null;
    }
}
