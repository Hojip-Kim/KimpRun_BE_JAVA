package kimp.community.dto.batch.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 배치 완전 삭제 결과 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchHardDeleteResponse {
    
    /**
     * 삭제된 게시물 수
     */
    private long deletedBoardCount;
    
    /**
     * 삭제된 댓글 수
     */
    private long deletedCommentCount;
    
    /**
     * 전체 삭제된 항목 수
     */
    private long totalDeletedCount;
    
    /**
     * 삭제 처리 시작 시간
     */
    private LocalDateTime startTime;
    
    /**
     * 삭제 처리 완료 시간
     */
    private LocalDateTime endTime;
    
    /**
     * 처리 소요 시간 (밀리초)
     */
    private long processingTimeMs;
    
    /**
     * 실제 삭제 수행 여부
     */
    private boolean executed;
    
    /**
     * 처리 상태 메시지
     */
    private String message;
    
    /**
     * 삭제 대상 기준 날짜
     */
    private LocalDateTime criteriaDate;
    
    public static BatchHardDeleteResponse dryRun(long boardCount, long commentCount, LocalDateTime criteriaDate) {
        return BatchHardDeleteResponse.builder()
                .deletedBoardCount(boardCount)
                .deletedCommentCount(commentCount)
                .totalDeletedCount(boardCount + commentCount)
                .executed(false)
                .criteriaDate(criteriaDate)
                .message("삭제 예정 항목 조회 완료 (실제 삭제 미실행)")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .processingTimeMs(0)
                .build();
    }
    
    public static BatchHardDeleteResponse success(long boardCount, long commentCount, 
                                                LocalDateTime startTime, LocalDateTime endTime, 
                                                LocalDateTime criteriaDate) {
        return BatchHardDeleteResponse.builder()
                .deletedBoardCount(boardCount)
                .deletedCommentCount(commentCount)
                .totalDeletedCount(boardCount + commentCount)
                .executed(true)
                .criteriaDate(criteriaDate)
                .message("배치 삭제 완료")
                .startTime(startTime)
                .endTime(endTime)
                .processingTimeMs(java.time.Duration.between(startTime, endTime).toMillis())
                .build();
    }
}