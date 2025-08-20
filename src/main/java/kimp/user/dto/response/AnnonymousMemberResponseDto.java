package kimp.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AnnonymousMemberResponseDto {

    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("application_banned_count")
    private Integer applicationBannedCount;
    @JsonProperty("cdn_banned_count")
    private Integer cdnBannedCount;
    @JsonProperty("is_banned")
    private Boolean isBanned;
    @JsonProperty("ban_type")
    private String banType;
    @JsonProperty("banned_start_time")
    private LocalDateTime banStartTime;
    @JsonProperty("banned_end_time")
    private LocalDateTime banEndTime;

    public AnnonymousMemberResponseDto(String uuid, String ip, Integer applicationBannedCount, Integer cdnBannedCount, Boolean isBanned, String banType, LocalDateTime banStartTime, LocalDateTime banEndTime) {
        this.uuid = uuid;
        this.ip = ip;
        this.applicationBannedCount = applicationBannedCount;
        this.cdnBannedCount = cdnBannedCount;
        this.isBanned = isBanned;
        this.banType = banType;
        this.banStartTime = banStartTime;
        this.banEndTime = banEndTime;
    }
}
