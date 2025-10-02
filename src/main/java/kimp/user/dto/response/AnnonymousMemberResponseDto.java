package kimp.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
