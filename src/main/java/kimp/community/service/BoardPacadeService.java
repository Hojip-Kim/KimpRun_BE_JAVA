package kimp.community.service;

import kimp.common.dto.PageRequestDto;
import kimp.community.dto.board.request.CreateBoardRequestDto;
import kimp.community.dto.board.request.UpdateBoardRequestDto;
import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.dto.board.response.BoardWithCommentResponseDto;
import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.entity.*;
import org.springframework.data.domain.PageImpl;
import kimp.community.repository.BoardRepository;
import kimp.community.repository.BoardLikeRepository;
import kimp.community.service.impl.CategoryServiceImpl;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.entity.Member;
import kimp.user.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardPacadeService {

    private final BoardService boardService;
    private final MemberService memberService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final CommentPacadeService commentPacadeService;

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;

    public BoardPacadeService(BoardService boardService, MemberService memberService, CategoryService categoryService, CommentService commentService, CommentPacadeService commentPacadeService, BoardRepository boardRepository, BoardLikeRepository boardLikeRepository) {
        this.boardService = boardService;
        this.memberService = memberService;
        this.categoryService = categoryService;
        this.commentService = commentService;
        this.commentPacadeService = commentPacadeService;
        this.boardRepository = boardRepository;
        this.boardLikeRepository = boardLikeRepository;
    }


    @Transactional
    public Board createBoard(Long memberId, Long categoryId, CreateBoardRequestDto createBoardRequestDto) {

        Member member = memberService.getMemberEntityById(memberId);

        Category category = categoryService.getCategoryByID(categoryId);

        Board board = boardService.createBoard(createBoardRequestDto);
        BoardViews boardViews = boardService.createBoardViews(board);
        BoardLikeCount boardLikeCount = boardService.createBoardLikeCount(board);
        CommentCount commentCount = commentService.createCommentCount(board);

        board.setMember(member).setCategory(category).setViews(boardViews).setBoardLikeCounts(boardLikeCount).setCommentCount(commentCount);

        member.addBoard(board);

        category.getBoardCount().viewCounts();

        return board;
    }

    // 게시글 조회
    // board를 Id로 찾은 후 comment list와 함께 response
    @Transactional
    public BoardWithCommentResponseDto getBoardByIdWithCommentPage(long memberId ,Long requestBoardId, int page) {
        Board board = boardService.getBoardById(requestBoardId);

        if(memberId == -1 || board.getMember().getId() != memberId ) {
            board.getViews().viewCount();
        }
        Page<Comment> comments = commentPacadeService.getComments(requestBoardId, page);

        List<ResponseCommentDto> commentDtos =  commentService.converCommentsToResponseDtoList(comments.getContent());

        return new BoardWithCommentResponseDto(board.getId(), board.getMember().getId(),board.getCategory().getId(),board.getCategory().getCategoryName(), board.getMember().getNickname(), board.getTitle(), board.getContent(), board.getViews().getViews(), board.getBoardLikeCount().getLikes(), board.getRegistedAt(), board.getUpdatedAt(), commentDtos, board.getCommentCount().getCounts(), board.isPin() );

    }

    @Transactional
    public BoardResponseDto updateBoard(Long memberId, Long boardId, UpdateBoardRequestDto updateBoardRequestDto) {
        Board board = boardService.getBoardById(boardId);
        if(!board.getMember().getId().equals(memberId)) {
            throw new KimprunException(KimprunExceptionEnum.AUTHENTICATION_REQUIRED_EXCEPTION, "User not authorized to update this board: " + boardId, HttpStatus.UNAUTHORIZED, "BoardPacadeService.updateBoard");
        }
        boardService.updateBoard(board, updateBoardRequestDto);
        return convertBoardToBoardResponseDto(board);

    }

    public Boolean deleteBoard(Long memberId, Long boardId){
        Board board = boardService.getBoardById(boardId);
        if(!board.getMember().getId().equals(memberId)) {
            throw new KimprunException(KimprunExceptionEnum.AUTHENTICATION_REQUIRED_EXCEPTION, "User not authorized to delete this board: " + boardId, HttpStatus.UNAUTHORIZED, "BoardPacadeService.deleteBoard");
        }

        return boardService.deleteBoard(board);
    }


    // 15개씩 카테고리에 대한 게시글 불러오기
    public Page<Board> getBoardPageWithCategoryId(Long categoryId, PageRequestDto pageRequestDto) {
        Category category = categoryService.getCategoryByID(categoryId);

        PageRequest pageRequest = PageRequest.of(pageRequestDto.getPage()-1, pageRequestDto.getSize());

        Page<Board> boardPages = boardService.getBoardsPageByCategory(category, pageRequest);

        return boardPages;
    }

    // DTO 직접 조회로 N+1 문제 완전 해결
    public Page<BoardResponseDto> getBoardDtoPageWithCategoryId(Long categoryId, PageRequestDto pageRequestDto) {
        // Category 조회를 피하고 categoryId를 직접 사용하도록 수정
        PageRequest pageRequest = PageRequest.of(pageRequestDto.getPage()-1, pageRequestDto.getSize());
        
        Page<BoardResponseDto> dtoPage;
        
        // 첫 페이지(page=1)인 경우 공지사항 우선 조회
        if (pageRequestDto.getPage() == 1) {
            dtoPage = boardRepository.findBoardDtosByCategoryIdWithPinnedFirstOrderByRegistedAtDesc(categoryId, pageRequest);
        } else {
            dtoPage = boardRepository.findBoardDtosByCategoryIdOrderByRegistedAtDesc(categoryId, pageRequest);
        }
        
        // Content 요약 처리
        List<BoardResponseDto> summarizedDtos = dtoPage.getContent().stream()
                .map(dto -> createSummarizedBoardDto(dto))
                .toList();
        
        return new PageImpl<>(summarizedDtos, pageRequest, dtoPage.getTotalElements());
    }

    public Page<BoardResponseDto> getAllBoardDtoPage(PageRequestDto pageRequestDto) {
        PageRequest pageRequest = PageRequest.of(pageRequestDto.getPage()-1, pageRequestDto.getSize());
        
        Page<BoardResponseDto> dtoPage;
        
        // 첫 페이지(page=1)인 경우 공지사항 우선 조회
        if (pageRequestDto.getPage() == 1) {
            dtoPage = boardRepository.findAllBoardDtosWithPinnedFirstOrderByRegistedAtDesc(pageRequest);
        } else {
            dtoPage = boardRepository.findAllBoardDtosOrderByRegistedAtDesc(pageRequest);
        }
        
        // Content 요약 처리
        List<BoardResponseDto> summarizedDtos = dtoPage.getContent().stream()
                .map(dto -> createSummarizedBoardDto(dto))
                .toList();
        
        return new PageImpl<>(summarizedDtos, pageRequest, dtoPage.getTotalElements());
    }
    
    private BoardResponseDto createSummarizedBoardDto(BoardResponseDto original) {
        String summarizedContent = summaryContent(original.getContent());
        return new BoardResponseDto(
            original.getBoardId(),
            original.getMemberId(), 
            original.getCategoryId(),
            original.getCategoryName(),
            original.getMemberNickName(),
            original.getTitle(),
            summarizedContent, // 요약된 content
            original.getBoardViewsCount(),
            original.getBoardLikesCount(),
            original.getCreatedAt(),
            original.getUpdatedAt(),
            original.getCommentsCount(),
            original.getIsPin()
        );
    }



    public BoardResponseDto convertBoardToBoardResponseDto(Board board){
        return new BoardResponseDto(board.getId(), board.getMember().getId(),board.getCategory().getId(),board.getCategory().getCategoryName(), board.getMember().getNickname(), board.getTitle(), board.getContent(),board.getViews().getViews(), board.getBoardLikeCount().getLikes(), board.getRegistedAt(), board.getUpdatedAt(), board.getCommentCount().getCounts(), board.isPin());
    }

    public String summaryContent(String content) {
        if (content.length() > 100) {
            return content.substring(0, 100) + "...";
        }
        return content;
    }



    public BoardResponseDto convertBoardToBoardResponseDtoWithSummaryContent(Board board){

        String summaryContent = summaryContent(board.getContent());

        return new BoardResponseDto(board.getId(), board.getMember().getId(),board.getCategory().getId(),board.getCategory().getCategoryName(), board.getMember().getNickname(), board.getTitle(), summaryContent, board.getViews().getViews(), board.getBoardLikeCount().getLikes(), board.getRegistedAt(), board.getUpdatedAt(), board.getCommentCount().getCounts(), board.isPin());
    }

    public List<BoardResponseDto> convertBoardsToBoardResponseDtos(Page<Board> boards){
        return boards.stream()
                .map(board -> convertBoardToBoardResponseDtoWithSummaryContent(board))
                .collect(Collectors.toList());
    }

    public Long getBoardCountByCategoryId(long categoryId){
        Category category = ((CategoryServiceImpl)this.categoryService).getCategoryByIDWithBoardCount(categoryId);
        return category.getBoardCount().getCounts().longValue();
    }


    @Transactional
    public Comment createComment(long memberId, long boardId, RequestCreateCommentDto requestCreateCommentDto) {
        Board board = boardService.getBoardById(boardId);
        Member member = memberService.getMemberEntityById(memberId);
        Comment comment = commentService.createComment(member, board, requestCreateCommentDto);
        CommentLikeCount commentLikeCount = commentService.createCommentLikeCount(comment);
        comment.setCommentLikeCount(commentLikeCount);
        member.addComment(comment);
        board.addComment(comment);
        board.getCommentCount().addCount();

        return comment;
    }

    public Page<Comment> getComments(long boardId, int page) {
        Board board = boardService.getBoardById(boardId);
        Page<Comment> comments = commentService.getCommentByBoard(board, page);

        return comments;

    }

    @Transactional
    public Boolean likeBoardById(Long boardId, Long memberId) {
        if (boardLikeRepository.existsByBoardIdAndMemberId(boardId, memberId)) {
            return false;
        }
        
        boardLikeRepository.addLikeIfNotExists(boardId, memberId);
        return true;
    }

    public Page<BoardResponseDto> getBoardsByMember(Long memberId, PageRequestDto pageRequestDto) {
        Member member = memberService.getMemberEntityById(memberId);
        PageRequest pageRequest = PageRequest.of(pageRequestDto.getPage()-1, pageRequestDto.getSize());
        
        Page<BoardResponseDto> dtoPage = boardRepository.findBoardDtosByMemberOrderByRegistedAtDesc(memberId, pageRequest);
        
        List<BoardResponseDto> summarizedDtos = dtoPage.getContent().stream()
                .map(dto -> createSummarizedBoardDto(dto))
                .toList();
        
        return new PageImpl<>(summarizedDtos, pageRequest, dtoPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<ResponseCommentDto> getCommentsByMember(Long memberId, PageRequestDto pageRequestDto) {
        PageRequest pageRequest = PageRequest.of(pageRequestDto.getPage()-1, pageRequestDto.getSize());
        
        Page<Comment> comments = commentService.getCommentsByMemberIdWithAllFetch(memberId, pageRequest);
        
        List<ResponseCommentDto> commentDtos = comments.getContent().stream()
                .map(comment -> new ResponseCommentDto(
                    comment.getId(),
                    comment.getBoard().getId(),
                    comment.getBoard().getTitle(),
                    comment.getMember().getId(), // comment 작성자 ID
                    comment.getMember().getNickname(),
                    comment.getContent(),
                    comment.getLikeCount() != null ? comment.getLikeCount().getLikes() : 0,
                    comment.getRegistedAt(),
                    comment.getUpdatedAt()
                ))
                .toList();
        
        return new PageImpl<>(commentDtos, pageRequest, comments.getTotalElements());
    }

    @Transactional
    public void softDeleteBoard(Long memberId, Long boardId) {
        Board board = boardService.getBoardById(boardId);
        if (!board.getMember().getId().equals(memberId)) {
            throw new KimprunException(KimprunExceptionEnum.AUTHENTICATION_REQUIRED_EXCEPTION, 
                "User not authorized to delete this board: " + boardId, HttpStatus.UNAUTHORIZED, "BoardPacadeService.softDeleteBoard");
        }
        
        board.softDelete();
        boardRepository.save(board);
    }

    // DTO 반환 메소드들 (Controller용)
    @Transactional
    public BoardResponseDto createBoardDto(Long memberId, Long categoryId, CreateBoardRequestDto createBoardRequestDto) {
        Board board = createBoard(memberId, categoryId, createBoardRequestDto);
        return convertBoardToBoardResponseDto(board);
    }

    // CommentController에서 사용하는 DTO 메소드들
    public Page<ResponseCommentDto> getCommentsDto(Long boardId, int page) {
        Board board = boardService.getBoardById(boardId);
        Page<Comment> comments = commentService.getCommentByBoard(board, page);
        List<ResponseCommentDto> commentDtos = commentService.converCommentsToResponseDtoList(comments.getContent());
        PageRequest pageRequest = PageRequest.of(page, 30); // CommentServiceImpl에서 사용하는 페이지 사이즈
        return new PageImpl<>(commentDtos, pageRequest, comments.getTotalElements());
    }

    @Transactional
    public ResponseCommentDto createCommentDto(long memberId, Long boardId, RequestCreateCommentDto requestCreateCommentDto) {
        Comment comment = createComment(memberId, boardId, requestCreateCommentDto);
        return commentService.convertCommentToResponseDto(comment);
    }
}
