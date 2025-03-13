package kimp.community.controller;

import kimp.community.dto.board.request.BoardInsertDto;
import kimp.community.dto.board.request.CreateBoardRequestDto;
import kimp.community.dto.board.request.RequestBoardPin;
import kimp.community.dto.board.request.UpdateBoardRequestDto;
import kimp.community.dto.board.response.AllBoardResponseDto;
import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.dto.board.response.BoardWithCommentResponseDto;
import kimp.community.dto.board.response.BoardWithCountResponseDto;
import kimp.community.entity.Board;
import kimp.community.service.BoardPerformanceService;
import kimp.community.service.BoardService;
import kimp.community.service.BoardPacadeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import kimp.security.user.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final BoardPacadeService boardPacadeService;
    private final BoardPerformanceService boardPerformanceService;

    public BoardController(BoardService boardService, BoardPacadeService boardPacadeService, BoardPerformanceService boardPerformanceService) {
        this.boardService = boardService;
        this.boardPacadeService = boardPacadeService;
        this.boardPerformanceService = boardPerformanceService;
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
    private BoardWithCommentResponseDto getBoard(@AuthenticationPrincipal UserDetails UserDetails, @RequestParam("boardId") long boardId, @RequestParam("commentPage") int commentPage){
        if(boardId < 0){
            throw new IllegalArgumentException("categoryId and boardId must be non-negative");
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        long memberId;

        if(UserDetails == null){
            memberId = -1;
        }else{
            memberId = customUserDetails.getId();
        }



        BoardWithCommentResponseDto board = boardPacadeService.getBoardByIdWithCommentPage(memberId,boardId, commentPage);

        return board;
    }

    @GetMapping("/all/page")
    private AllBoardResponseDto getAllCategoryBoards(@RequestParam("page") int page){
        if(page < 1){
            throw new IllegalArgumentException("page must greater than 1");
        }

        Page<Board> boardList = this.boardService.getBoardsByPage(page-1);
        Long boardCount = this.boardService.getBoardsCount();

        return this.boardService.convertBoardPagesToAllBoardResponseDtos(boardList, boardCount);
    }

    // 필요한 field : memberId, member name, boardId, registed_at, boardTitle, boardViews, boardLikeCount
    @GetMapping("/{categoryId}/{page}")
    private BoardWithCountResponseDto getBoardsPageWithPage(@PathVariable("categoryId") Long categoryId, @PathVariable("page") Integer page){
        if(categoryId < 0){
            throw new IllegalArgumentException("request parameter categoryId must be greater than 0");
        }
        if(page < 1){
            throw new IllegalArgumentException("page must greater than 1");
        }
        Page<Board> boardList = boardPacadeService.getBoardPageWithCategoryId(categoryId, page-1);
        List<BoardResponseDto> boardDtos = boardPacadeService.convertBoardsToBoardResponseDtos(boardList);

        Integer boardCount = boardPacadeService.getBoardCountByCategoryId(categoryId);


        return new BoardWithCountResponseDto(boardDtos, boardCount);

    }

    @PostMapping("/{categoryId}/create")
    private void createBoard(@AuthenticationPrincipal UserDetails UserDetails, @PathVariable("categoryId") long categoryId, @RequestBody CreateBoardRequestDto createBoardRequestDto) {

        if(categoryId < 0) {
            throw new IllegalArgumentException("request parameter categoryId must be greater than 0");
        }
        if(createBoardRequestDto == null){
            throw new IllegalArgumentException("request parameter createBoardRequestDto must not be null");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        BoardInsertDto boardInsertDto = new BoardInsertDto(customUserDetails.getId(), categoryId, createBoardRequestDto.getTitle(), createBoardRequestDto.getContent());

        this.boardPerformanceService.enqueueBoardQueue(boardInsertDto);

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
    private BoardResponseDto updateBoard(@AuthenticationPrincipal UserDetails UserDetails, @PathVariable("boardId") long boardId, @RequestBody UpdateBoardRequestDto updateBoardRequestDto){
        if(boardId < 0){
            throw new IllegalArgumentException("boardId must greater than 0");
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        Board board = this.boardPacadeService.updateBoard(customUserDetails.getId(), boardId, updateBoardRequestDto);

        return this.boardPacadeService.convertBoardToBoardResponseDto(board);
    }

    @DeleteMapping("/{boardId}")
    private ResponseEntity<Void> deleteBoard(@AuthenticationPrincipal UserDetails UserDetails, @PathVariable("boardId") long boardId) {
        Boolean isDeleted = false;
        if(boardId < 0){
            throw new IllegalArgumentException("boardId must greater than 0");
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        isDeleted = this.boardPacadeService.deleteBoard(customUserDetails.getId(), boardId);

        if(isDeleted){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR')")
    @PatchMapping("/activate")
    private ResponseEntity<Boolean> activateBoardsPin(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody RequestBoardPin requestBoardPin) {

        this.boardService.activatePinWithBoard(requestBoardPin.getBoardIds());

        return ResponseEntity.ok(true);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR')")
    @PatchMapping("/deActivate")
    private ResponseEntity<Boolean> deActivateBoardsPin(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody RequestBoardPin requestBoardPin) {

        this.boardService.deactivatePinWithBoard(requestBoardPin.getBoardIds());

        return ResponseEntity.ok(true);

    }

    @PatchMapping("/like")
    private ResponseEntity<Void> likeBoard(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody long boardId){

        CustomUserDetails customUserDetails = (CustomUserDetails)UserDetails;

        Boolean isCompleted = boardPacadeService.likeBoardById(boardId, customUserDetails.getId());

        if(!isCompleted){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

}
