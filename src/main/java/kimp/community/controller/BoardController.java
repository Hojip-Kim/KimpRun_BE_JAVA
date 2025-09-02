package kimp.community.controller;

import kimp.common.dto.PageRequestDto;
import kimp.community.dto.board.request.CreateBoardRequestDto;
import kimp.community.dto.board.request.RequestBoardPin;
import kimp.community.dto.board.request.UpdateBoardRequestDto;
import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.dto.board.response.BoardWithCommentResponseDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.service.BoardService;
import kimp.community.service.BoardPacadeService;
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

    public BoardController(BoardService boardService, BoardPacadeService boardPacadeService) {
        this.boardService = boardService;
        this.boardPacadeService = boardPacadeService;
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

        BoardResponseDto result = this.boardPacadeService.updateBoardDto(customUserDetails.getId(), boardId, updateBoardRequestDto);
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
    public ApiResponse<Boolean> likeBoard(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody long boardId){

        CustomUserDetails customUserDetails = (CustomUserDetails)UserDetails;

        Boolean isCompleted = boardPacadeService.likeBoardById(boardId, customUserDetails.getId());
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

}
