package kimp.community.service;

import kimp.community.dto.batch.request.BatchHardDeleteRequest;
import kimp.community.dto.batch.response.BatchHardDeleteResponse;

/**
 * 배치 작업을 위한 서비스 인터페이스
 */
public interface BatchService {
    
    /**
     * Soft delete된 게시물과 댓글을 완전 삭제하는 배치 작업
     * 
     * @param request 배치 삭제 요청 정보
     * @return 배치 삭제 결과
     */
    BatchHardDeleteResponse executeHardDeleteBatch(BatchHardDeleteRequest request);
    
    /**
     * Soft delete된 항목들의 개수만 조회 (실제 삭제는 하지 않음)
     * 
     * @param request 조회 조건
     * @return 삭제 대상 개수 정보
     */
    BatchHardDeleteResponse countSoftDeletedItems(BatchHardDeleteRequest request);
}