package kimp.community.dto.batch.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Soft delete된 게시물과 댓글을 완전 삭제하기 위한 배치 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchHardDeleteRequest {
    
    /**
     * 이 날짜 이전에 soft delete된 항목들만 완전 삭제
     * 예: 30일 전 이전에 삭제된 항목들
     */
    @NotNull(message = "삭제 기준 날짜는 필수입니다")
    private LocalDateTime beforeDate;
    
    /**
     * 한 번에 처리할 최대 항목 수 (성능 제한)
     * 기본값: 1000개
     */
    @Min(value = 1, message = "배치 크기는 1 이상이어야 합니다")
    private Integer batchSize = 1000;
    
    /**
     * 게시물만 삭제할지 여부
     * false면 게시물과 댓글 모두 삭제
     */
    private boolean boardOnly = false;
    
    /**
     * 댓글만 삭제할지 여부  
     * false면 게시물과 댓글 모두 삭제
     */
    private boolean commentOnly = false;
    
    /**
     * 실제 삭제 여부 (false면 삭제될 항목 수만 조회)
     * 안전장치로 사용
     */
    private boolean executeDelete = false;
}