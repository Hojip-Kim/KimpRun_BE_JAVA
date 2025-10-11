package kimp.scrap.dto.internal.python;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Python 스크래핑 서비스 응답 DTO
 */
@Data
@NoArgsConstructor
public class PythonNoticeResponseDto {

    @JsonProperty("success")
    private boolean success;
    @JsonProperty("exchange")
    private String exchange;
    @JsonProperty("url")
    private String url;
    @JsonProperty("results")
    private List<PythonNoticeDto> results;
    @JsonProperty("count")
    private int count;
    @JsonProperty("error")
    private String error;
    @JsonProperty("total_found")
    private String totalFound;
    
    /**
     * 개별 공지사항 DTO
     */
    @Data
    @NoArgsConstructor
    public static class PythonNoticeDto {
        private String title;
        private String category;
        private String date;
        private String link;
        
        @JsonProperty("is_new")
        private String isNew;
    }
} 