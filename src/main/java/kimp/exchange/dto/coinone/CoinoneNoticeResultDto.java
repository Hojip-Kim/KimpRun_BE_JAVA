package kimp.exchange.dto.coinone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoinoneNoticeResultDto {
    private int id;
    private String card_category;
    private String get_absolute_url;

    @JsonProperty("created_by")
    private CoinoneCreatedByDto createBy;

    @JsonProperty("flagged_content")
    private CoinoneFlaggedContentDto flaggedContent;

    private String vote_type_by_current_user;
    private int content_type;
    private String board;
    private int vote_count;
    private String title;
    private String summary;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    private String thumbnail;

    @JsonProperty("thumbnail_original")
    private String thumbnailOriginal;

    @JsonProperty("view_count")
    private int viewCount;

    @JsonProperty("comment_count")
    private int commentCount;

    @JsonProperty("is_edited")
    private boolean isEdited;

    @JsonProperty("notice_type")
    private String noticeType;

    @JsonProperty("is_visible")
    private boolean isVisible;

    @JsonProperty("visible_type")
    private int visibleType;

    @JsonProperty("notice_type_num")
    private int noticeTypeNum;

    @JsonProperty("is_regular_event")
    private boolean isRegularEvent;

    @JsonProperty("event_start_datetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventStartDateTime;

    @JsonProperty("event_end_datetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventEndDateTime;

    private boolean pin;

    @JsonProperty("short_link")
    private String shortLink;

    @JsonProperty("short_link_id")
    private String shortLinkId;
}
