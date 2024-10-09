package kimp.community.controller;

import kimp.community.dto.board.request.CreateBoardRequestDto;
import kimp.community.dto.board.request.UpdateBoardRequestDto;
import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.entity.Board;
import kimp.community.service.BoardService;
import kimp.community.service.BoardPacadeService;
import kimp.security.user.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
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
     * Long boardId;
     * Long userId;
     * String userNickName;
     * String title;
     * String content;
     * LocalDateTime createdAt;
     * LocalDateTime updatedAt;
     */
    @GetMapping("/boardId")
    private BoardResponseDto getBoard(@RequestParam("boardId") long boardId){
        if(boardId < 0){
            throw new IllegalArgumentException("categoryId and boardId must be non-negative");
        }

        Board board = boardService.getBoardById(boardId);

        return boardService.convertBoardToBoardResponseDto(board);
    }

    @GetMapping("/all/page")
    private List<BoardResponseDto> getAllCategoryBoards(@RequestParam("page") int page){
        if(page < 0){
            throw new IllegalArgumentException("page must greater than 0");
        }

        Page<Board> boardList = this.boardService.getBoardsByPage(page);

        return this.boardService.convertBoardPagesToBoardResponseDtos(boardList);
    }

    // 필요한 field : userId, user name, boardId, registed_at, boardTitle, boardViews, boardLikeCount
    @GetMapping("/{categoryId}/page")
    private List<BoardResponseDto> getBoardsPageWithPage(@RequestParam("categoryId") Long categoryId, @RequestParam("page") Integer page){
        if(categoryId < 0){
            throw new IllegalArgumentException("request parameter categoryId must be greater than 0");
        }

        Page<Board> boardList = boardPacadeService.getBoardPageWithCategoryId(categoryId, page);

        return boardPacadeService.convertBoardsToBoardResponseDtos(boardList);

    }

    @PostMapping("/{categoryId}/create")
    private BoardResponseDto createBoard(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("categoryId") long categoryId, @RequestBody CreateBoardRequestDto createBoardRequestDto) {

        if(categoryId < 0) {
            throw new IllegalArgumentException("request parameter categoryId must be greater than 0");
        }
        if(createBoardRequestDto == null){
            throw new IllegalArgumentException("request parameter createBoardRequestDto must not be null");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        Board board = this.boardPacadeService.createBoard(customUserDetails.getId(),categoryId, createBoardRequestDto);

        return this.boardPacadeService.convertBoardToBoardResponseDto(board);

    }

    @PatchMapping("/{boardId}")
    private BoardResponseDto updateBoard(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("boardId") long boardId, @RequestBody UpdateBoardRequestDto updateBoardRequestDto){
        if(boardId < 0){
            throw new IllegalArgumentException("boardId must greater than 0");
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        Board board = this.boardPacadeService.updateBoard(customUserDetails.getId(), boardId, updateBoardRequestDto);

        return this.boardPacadeService.convertBoardToBoardResponseDto(board);
    }

    @DeleteMapping("/{boardId}")
    private ResponseEntity<Void> deleteBoard(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("boardId") long boardId) {
        Boolean isDeleted = false;
        if(boardId < 0){
            throw new IllegalArgumentException("boardId must greater than 0");
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        isDeleted = this.boardPacadeService.deleteBoard(customUserDetails.getId(), boardId);

        if(isDeleted){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

}
