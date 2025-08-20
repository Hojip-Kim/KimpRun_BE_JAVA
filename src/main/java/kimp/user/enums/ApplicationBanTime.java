package kimp.user.enums;

import lombok.Getter;

@Getter
public enum ApplicationBanTime {
    // 1회 - 10분
    FIRST(1, 60*10L),
    // 2회 - 1시간
    SECOND(2, 60*60L),
    // 3회 - 6시간
    THIRD(3, 60*60*6L),
    // 4회 - 24시간
    FOURTH(4, 60*60*24L),
    // 5회 - 1주일
    FIFTH(5, 60*60*24*7L),
    // 6회 - 30일
    SIXTH(6, 60*60*24*30L);

    private final Integer bannedCount;
    private final Long time;

    ApplicationBanTime(Integer bannedCount, Long time) {
        this.bannedCount = bannedCount;
        this.time = time;
    }

    public static ApplicationBanTime getBanTime(Integer bannedCount) {
        for (ApplicationBanTime applicationBanTime : ApplicationBanTime.values()) {
            if (applicationBanTime.getBannedCount().equals(bannedCount)) {
                return applicationBanTime;
            }
        }
        return null;
    }
}
