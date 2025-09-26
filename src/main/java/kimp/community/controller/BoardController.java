package kimp.community.controller;

import kimp.common.dto.PageRequestDto;
import kimp.community.dto.board.request.BoardLikeRequest;
import kimp.community.dto.board.request.CreateBoardRequestDto;
import kimp.community.dto.board.request.RequestBoardPin;
import kimp.community.dto.board.request.UpdateBoardRequestDto;
import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.dto.board.response.BoardWithCommentResponseDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.dto.batch.request.BatchHardDeleteRequest;
import kimp.community.dto.batch.response.BatchHardDeleteResponse;
import kimp.community.service.BoardService;
import kimp.community.service.BoardPacadeService;
import kimp.community.service.BatchService;
import kimp.exception.response.ApiResponse;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import kimp.security.user.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final BoardPacadeService boardPacadeService;
    private final BatchService batchService;

    public BoardController(BoardService boardService, BoardPacadeService boardPacadeService, BatchService batchService) {
        this.boardService = boardService;
        this.boardPacadeService = boardPacadeService;
        this.batchService = batchService;
    }


    /**
     * @param
     * boardId : long type
     * @return
    Long boardId,
    Long memberId,
    String memberNickName,
    String title,
    String content,
    Integer boardViewsCount,
    Integer boardLikesCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<ResponseCommentDto> comments
     */
    @GetMapping()
    public ApiResponse<BoardWithCommentResponseDto> getBoard(@AuthenticationPrincipal UserDetails UserDetails, @RequestParam("boardId") long boardId, @RequestParam("commentPage") int commentPage){
        if(boardId < 0){
            throw new KimprunException(KimprunExceptionEnum.INVALID_ID_PARAMETER_EXCEPTION, "Board ID must be non-negative", HttpStatus.BAD_REQUEST, "BoardController.getBoard");
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        long memberId;

        if(UserDetails == null){
            memberId = -1;
        }else{
            memberId = customUserDetails.getId();
        }

        BoardWithCommentResponseDto board = boardPacadeService.getBoardByIdWithCommentPage(memberId,boardId, commentPage);
        return ApiResponse.success(board);
    }

    // 필요한 field : memberId, member name, boardId, registed_at, boardTitle, boardViews, boardLikeCount
    @GetMapping("/{categoryId}")
    public ApiResponse<Page<BoardResponseDto>> getBoardsPageWithPage(@PathVariable("categoryId") Long categoryId, @ModelAttribute PageRequestDto pageRequestDto){
        if(categoryId < 0){
            throw new KimprunException(KimprunExceptionEnum.INVALID_ID_PARAMETER_EXCEPTION, "Category ID must be greater than or equal to 0", HttpStatus.BAD_REQUEST, "BoardController.getBoardsPageWithPage");
        }
        if(pageRequestDto.getPage() < 1){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PAGE_PARAMETER_EXCEPTION, "Page number must be greater than 0", HttpStatus.BAD_REQUEST, "BoardController.getBoardsPageWithPage");
        }

        Page<BoardResponseDto> boardDtoPage;
        // 카테고리가 1이면 (즉, 전체 카테고리면)
        if(categoryId == 1) {
            boardDtoPage = this.boardPacadeService.getAllBoardDtoPage(pageRequestDto);
        }else {
            boardDtoPage = boardPacadeService.getBoardDtoPageWithCategoryId(categoryId, pageRequestDto);
        }
        return ApiResponse.success(boardDtoPage);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR', 'INFLUENCER', 'USER')")
    @PostMapping("/{categoryId}/create")
    public ApiResponse<BoardResponseDto> createBoard(@AuthenticationPrincipal UserDetails UserDetails, @PathVariable("categoryId") long categoryId, @RequestBody CreateBoardRequestDto createBoardRequestDto) {

        if(categoryId < 0) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_ID_PARAMETER_EXCEPTION, "Category ID must be greater than or equal to 0", HttpStatus.BAD_REQUEST, "BoardController.createBoard");
        }
        if(createBoardRequestDto == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "CreateBoardRequestDto cannot be null", HttpStatus.BAD_REQUEST, "BoardController.createBoard");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        BoardResponseDto result = this.boardPacadeService.createBoardDto(customUserDetails.getId(), categoryId, createBoardRequestDto);
        
        return ApiResponse.success(result);
    }

//    @PostMapping("/{categoryId}/create")
//    private BoardResponseDto createBoard(@AuthenticationPrincipal UserDetails UserDetails, @PathVariable("categoryId") long categoryId, @RequestBody CreateBoardRequestDto createBoardRequestDto) {
//
//        if(categoryId < 0) {
//            throw new IllegalArgumentException("request parameter categoryId must be greater than 0");
//        }
//        if(createBoardRequestDto == null){
//            throw new IllegalArgumentException("request parameter createBoardRequestDto must not be null");
//        }
//
//        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;
//
//        Board board = this.boardPacadeService.createBoard(customUserDetails.getId(),categoryId, createBoardRequestDto);
//
//        return this.boardPacadeService.convertBoardToBoardResponseDto(board);
//
//    }

    @PatchMapping("/{boardId}")
    public ApiResponse<BoardResponseDto> updateBoard(@AuthenticationPrincipal UserDetails UserDetails, @PathVariable("boardId") long boardId, @RequestBody UpdateBoardRequestDto updateBoardRequestDto){
        if(boardId < 0){
            throw new KimprunException(KimprunExceptionEnum.INVALID_ID_PARAMETER_EXCEPTION, "Board ID must be greater than or equal to 0", HttpStatus.BAD_REQUEST, "BoardController.updateBoard");
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        BoardResponseDto result = this.boardPacadeService.updateBoard(customUserDetails.getId(), boardId, updateBoardRequestDto);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{boardId}")
    public ApiResponse<Boolean> deleteBoard(@AuthenticationPrincipal UserDetails UserDetails, @PathVariable("boardId") long boardId) {
        if(boardId < 0){
            throw new KimprunException(KimprunExceptionEnum.INVALID_ID_PARAMETER_EXCEPTION, "Board ID must be greater than or equal to 0", HttpStatus.BAD_REQUEST, "BoardController.deleteBoard");
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        Boolean isDeleted = this.boardPacadeService.deleteBoard(customUserDetails.getId(), boardId);
        return ApiResponse.success(isDeleted);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR')")
    @PatchMapping("/activate")
    public ApiResponse<Boolean> activateBoardsPin(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody RequestBoardPin requestBoardPin) {

        this.boardService.activatePinWithBoard(requestBoardPin.getBoardIds());
        return ApiResponse.success(true);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR')")
    @PatchMapping("/deActivate")
    public ApiResponse<Boolean> deActivateBoardsPin(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody RequestBoardPin requestBoardPin) {

        this.boardService.deactivatePinWithBoard(requestBoardPin.getBoardIds());
        return ApiResponse.success(true);
    }

    @PatchMapping("/like")
    public ApiResponse<Boolean> likeBoard(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody BoardLikeRequest boardLikeRequest){

        CustomUserDetails customUserDetails = (CustomUserDetails)UserDetails;

        Boolean isCompleted = boardPacadeService.likeBoardById(boardLikeRequest.getBoardId(), customUserDetails.getId());
        return ApiResponse.success(isCompleted);
    }

    @GetMapping("/member/{memberId}")
    public ApiResponse<Page<BoardResponseDto>> getBoardsByMember(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        if (memberId < 0) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_ID_PARAMETER_EXCEPTION, 
                "Member ID must be greater than or equal to 0", HttpStatus.BAD_REQUEST, "BoardController.getBoardsByMember");
        }
        if (page < 1) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PAGE_PARAMETER_EXCEPTION, 
                "Page number must be greater than 0", HttpStatus.BAD_REQUEST, "BoardController.getBoardsByMember");
        }

        PageRequestDto pageRequestDto = new PageRequestDto(page, size);
        Page<BoardResponseDto> boardDtoPage = boardPacadeService.getBoardsByMember(memberId, pageRequestDto);
        return ApiResponse.success(boardDtoPage);
    }

    @GetMapping("/member/{memberId}/comments")
    public ApiResponse<Page<ResponseCommentDto>> getCommentsByMember(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        if (memberId < 0) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_ID_PARAMETER_EXCEPTION, 
                "Member ID must be greater than or equal to 0", HttpStatus.BAD_REQUEST, "BoardController.getCommentsByMember");
        }
        if (page < 1) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PAGE_PARAMETER_EXCEPTION, 
                "Page number must be greater than 0", HttpStatus.BAD_REQUEST, "BoardController.getCommentsByMember");
        }

        PageRequestDto pageRequestDto = new PageRequestDto(page, size);
        Page<ResponseCommentDto> commentDtoPage = boardPacadeService.getCommentsByMember(memberId, pageRequestDto);
        return ApiResponse.success(commentDtoPage);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR', 'INFLUENCER', 'USER')")
    @DeleteMapping("/{boardId}/soft")
    public ApiResponse<Void> softDeleteBoard(@AuthenticationPrincipal UserDetails userDetails, @PathVariable long boardId) {
        if (boardId < 0) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_ID_PARAMETER_EXCEPTION, 
                "Board ID must be greater than or equal to 0", HttpStatus.BAD_REQUEST, "BoardController.softDeleteBoard");
        }
        
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        boardPacadeService.softDeleteBoard(customUserDetails.getId(), boardId);
        return ApiResponse.success(null);
    }

    /**
     * 소프트 삭제된 게시물 및 댓글 일괄 하드 삭제 (관리자 전용)
     * executeDelete=false로 설정하면 실제 삭제 없이 삭제 예정 개수만 조회하기 가능.
     * 
     * @param request 배치 삭제 요청 정보 (기준 날짜, 배치 크기, 실행 여부)
     * @return 배치 삭제 결과 (삭제된 게시물/댓글 수, 처리 시간 등)
     */
    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR')")
    @PostMapping("/batch/hard-delete")
    public ApiResponse<BatchHardDeleteResponse> batchHardDelete(
            @AuthenticationPrincipal UserDetails userDetails, 
            @RequestBody BatchHardDeleteRequest request) {
        
        if (request == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "배치 삭제 요청 정보가 없습니다.", HttpStatus.BAD_REQUEST, "BoardController.batchHardDelete");
        }
        
        if (request.getBeforeDate() == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                "기준 날짜가 필요합니다.", HttpStatus.BAD_REQUEST, "BoardController.batchHardDelete");
        }
        
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        
        log.info("배치 하드 삭제 요청 - 관리자: {}, 기준 날짜: {}, 배치 크기: {}, 실행 여부: {}", 
            customUserDetails.getUsername(), request.getBeforeDate(), 
            request.getBatchSize(), request.isExecuteDelete());
        
        BatchHardDeleteResponse response = request.isExecuteDelete() 
            ? batchService.executeHardDeleteBatch(request)
            : batchService.countSoftDeletedItems(request);
        
        log.info("배치 하드 삭제 완료 - 삭제된 게시물: {}개, 삭제된 댓글: {}개, 처리 시간: {}ms", 
            response.getDeletedBoardCount(), response.getDeletedCommentCount(), response.getProcessingTimeMs());
        
        return ApiResponse.success(response);
    }

}
