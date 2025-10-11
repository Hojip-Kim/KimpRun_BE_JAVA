package kimp.news.dto.internal.bloomingbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * BloomingBit API 최상위 응답 DTO
 * BloomingBit 뉴스 API의 전체 응답 구조
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloomingBitResponseDto {

    /** API 응답 메시지 정보 */
    @JsonProperty("message")
    private MessageDto message;

    /** API 응답 데이터 */
    @JsonProperty("data")
    private DataDto data;

    /**
     * API 응답 메시지 DTO
     * API 호출 결과의 상태와 메시지
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDto {
        /** 응답 성공 여부 */
        @JsonProperty("success")
        private Boolean success;

        /** 응답 상태 (예: "success", "error") */
        @JsonProperty("status")
        private String status;

        /** 응답 메시지 */
        @JsonProperty("message")
        private String message;

        /** HTTP 상태 코드 설명 */
        @JsonProperty("reasonPhrase")
        private String reasonPhrase;
    }

    /**
     * API 응답 데이터 DTO
     * 실제 뉴스 목록과 페이징 정보 포함
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataDto {
        /** 뉴스 목록 */
        @JsonProperty("content")
        private java.util.List<BloomingBitNewsDto> content;

        /** 페이징 정보 */
        @JsonProperty("paginate")
        private PaginateDto paginate;
    }

    /**
     * 페이징 정보 DTO
     * 뉴스 목록의 페이징 메타데이터
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginateDto {
        /** 페이지당 항목 수 */
        @JsonProperty("limit")
        private Integer limit;

        /** 페이지 시작 오프셋 */
        @JsonProperty("offset")
        private Integer offset;

        /** 전체 항목 수 */
        @JsonProperty("total")
        private Integer total;
    }
}
