package kimp.community.service.impl;

import kimp.community.dto.batch.request.BatchHardDeleteRequest;
import kimp.community.dto.batch.response.BatchHardDeleteResponse;
import kimp.community.repository.BoardRepository;
import kimp.community.repository.CommentRepository;
import kimp.community.service.BatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 배치 작업 서비스 구현체
 * Soft delete된 데이터의 완전 삭제를 담당
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BatchServiceImpl implements BatchService {
    
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    
    @Override
    @Transactional(readOnly = true)
    public BatchHardDeleteResponse countSoftDeletedItems(BatchHardDeleteRequest request) {
        log.info("소프트 삭제된 항목 개수 조회 시작 - 기준 날짜: {}", request.getBeforeDate());
        
        long boardCount = 0;
        long commentCount = 0;
        
        if (!request.isCommentOnly()) {
            boardCount = boardRepository.countSoftDeletedBoardsBeforeDate(
                request.getBeforeDate(), 
                PageRequest.of(0, request.getBatchSize())
            );
            log.debug("소프트 삭제된 게시물 개수: {}", boardCount);
        }
        
        if (!request.isBoardOnly()) {
            commentCount = commentRepository.countSoftDeletedCommentsBeforeDate(
                request.getBeforeDate(),
                PageRequest.of(0, request.getBatchSize())
            );
            log.debug("소프트 삭제된 댓글 개수: {}", commentCount);
        }
        
        log.info("소프트 삭제된 항목 개수 조회 완료 - 게시물: {}, 댓글: {}", boardCount, commentCount);
        
        return BatchHardDeleteResponse.dryRun(boardCount, commentCount, request.getBeforeDate());
    }
    
    @Override
    @Transactional
    public BatchHardDeleteResponse executeHardDeleteBatch(BatchHardDeleteRequest request) {
        LocalDateTime startTime = LocalDateTime.now();
        log.info("배치 완전 삭제 작업 시작 - 기준 날짜: {}, 배치 크기: {}", 
                request.getBeforeDate(), request.getBatchSize());
        
        long totalDeletedBoards = 0;
        long totalDeletedComments = 0;
        
        try {
            // 댓글 먼저 삭제 (외래키 제약조건 때문에)
            if (!request.isBoardOnly()) {
                totalDeletedComments = deleteCommentsInBatches(request);
                log.info("댓글 배치 삭제 완료 - 삭제된 댓글 수: {}", totalDeletedComments);
            }
            
            // 게시물 삭제
            if (!request.isCommentOnly()) {
                totalDeletedBoards = deleteBoardsInBatches(request);
                log.info("게시물 배치 삭제 완료 - 삭제된 게시물 수: {}", totalDeletedBoards);
            }
            
            LocalDateTime endTime = LocalDateTime.now();
            log.info("배치 완전 삭제 작업 완료 - 총 삭제된 항목: {} (게시물: {}, 댓글: {})", 
                    totalDeletedBoards + totalDeletedComments, totalDeletedBoards, totalDeletedComments);
            
            return BatchHardDeleteResponse.success(
                totalDeletedBoards, 
                totalDeletedComments, 
                startTime, 
                endTime, 
                request.getBeforeDate()
            );
            
        } catch (Exception e) {
            log.error("배치 삭제 작업 중 오류 발생", e);
            throw new RuntimeException("배치 삭제 작업 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 댓글을 배치 단위로 완전 삭제
     */
    private long deleteCommentsInBatches(BatchHardDeleteRequest request) {
        long totalDeleted = 0;
        Pageable pageable = PageRequest.of(0, request.getBatchSize());
        
        while (true) {
            long deletedInThisBatch = commentRepository.deleteSoftDeletedCommentsBeforeDate(
                request.getBeforeDate(), 
                pageable
            );
            
            totalDeleted += deletedInThisBatch;
            log.debug("댓글 배치 삭제 - 현재 배치에서 삭제된 수: {}, 총 삭제 수: {}", 
                    deletedInThisBatch, totalDeleted);
            
            // 더 이상 삭제할 댓글이 없으면 종료
            if (deletedInThisBatch == 0) {
                break;
            }
            
            // 배치 크기만큼 삭제되지 않았으면 마지막 배치이므로 종료
            if (deletedInThisBatch < request.getBatchSize()) {
                break;
            }
        }
        
        return totalDeleted;
    }
    
    /**
     * 게시물을 배치 단위로 완전 삭제
     */
    private long deleteBoardsInBatches(BatchHardDeleteRequest request) {
        long totalDeleted = 0;
        Pageable pageable = PageRequest.of(0, request.getBatchSize());
        
        while (true) {
            long deletedInThisBatch = boardRepository.deleteSoftDeletedBoardsBeforeDate(
                request.getBeforeDate(), 
                pageable
            );
            
            totalDeleted += deletedInThisBatch;
            log.debug("게시물 배치 삭제 - 현재 배치에서 삭제된 수: {}, 총 삭제 수: {}", 
                    deletedInThisBatch, totalDeleted);
            
            // 더 이상 삭제할 게시물이 없으면 종료
            if (deletedInThisBatch == 0) {
                break;
            }
            
            // 배치 크기만큼 삭제되지 않았으면 마지막 배치이므로 종료
            if (deletedInThisBatch < request.getBatchSize()) {
                break;
            }
        }
        
        return totalDeleted;
    }
}