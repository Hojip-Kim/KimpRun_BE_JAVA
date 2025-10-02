package kimp.community.service;

import kimp.common.dto.PageRequestDto;
import kimp.community.dto.board.request.CreateBoardRequestDto;
import kimp.community.dto.board.request.UpdateBoardRequestDto;
import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.dto.board.response.BoardWithCommentResponseDto;
import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.entity.*;
import kimp.community.vo.*;
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
    public BoardWithCommentResponseDto getBoardByIdWithCommentPage(GetBoardVo vo) {
        Board board = boardService.getBoardById(vo.getBoardId());

        if(vo.getMemberId() == -1 || board.getMember().getId() != vo.getMemberId() ) {
            board.getViews().viewCount();
        }
        // 삭제된 댓글도 포함하여 모든 댓글 조회
        Page<Comment> comments = commentPacadeService.getCommentsWithDeleted(vo.getBoardId(), vo.getCommentPage());

        List<ResponseCommentDto> commentDtos =  commentService.converCommentsToResponseDtoList(comments.getContent());

        return BoardWithCommentResponseDto.builder()
                .boardId(board.getId())
                .memberId(board.getMember().getId())
                .categoryId(board.getCategory().getId())
                .categoryName(board.getCategory().getCategoryName())
                .memberNickName(board.getMember().getNickname())
                .title(board.getTitle())
                .content(board.getContent())
                .boardViewsCount(board.getViews().getViews())
                .boardLikesCount(board.getBoardLikeCount().getLikes())
                .createdAt(board.getRegistedAt())
                .updatedAt(board.getUpdatedAt())
                .comments(commentDtos)
                .commentsCount(board.getCommentCount().getCounts())
                .isPin(board.isPin())
                .build();

    }

    @Transactional
    public BoardResponseDto updateBoard(UpdateBoardVo vo) {
        Board board = boardService.getBoardById(vo.getBoardId());
        if(!board.getMember().getId().equals(vo.getMemberId())) {
            throw new KimprunException(KimprunExceptionEnum.AUTHENTICATION_REQUIRED_EXCEPTION, "User not authorized to update this board: " + vo.getBoardId(), HttpStatus.UNAUTHORIZED, "BoardPacadeService.updateBoard");
        }
        boardService.updateBoard(board, vo.getUpdateBoardRequestDto());
        return convertBoardToBoardResponseDto(board);

    }

    public Boolean deleteBoard(DeleteBoardVo vo){
        Board board = boardService.getBoardById(vo.getBoardId());
        if(!board.getMember().getId().equals(vo.getMemberId())) {
            throw new KimprunException(KimprunExceptionEnum.AUTHENTICATION_REQUIRED_EXCEPTION, "User not authorized to delete this board: " + vo.getBoardId(), HttpStatus.UNAUTHORIZED, "BoardPacadeService.deleteBoard");
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
    public Page<BoardResponseDto> getBoardDtoPageWithCategoryId(GetBoardsPageVo vo) {
        // Category 조회를 피하고 categoryId를 직접 사용하도록 수정
        PageRequest pageRequest = PageRequest.of(vo.getPageRequestDto().getPage()-1, vo.getPageRequestDto().getSize());

        Page<BoardResponseDto> dtoPage;

        // 첫 페이지(page=1)인 경우 공지사항 우선 조회
        if (vo.getPageRequestDto().getPage() == 1) {
            dtoPage = boardRepository.findBoardDtosByCategoryIdWithPinnedFirstOrderByRegistedAtDesc(vo.getCategoryId(), pageRequest);
        } else {
            dtoPage = boardRepository.findBoardDtosByCategoryIdOrderByRegistedAtDesc(vo.getCategoryId(), pageRequest);
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
        return BoardResponseDto.builder()
                .boardId(original.getBoardId())
                .memberId(original.getMemberId())
                .categoryId(original.getCategoryId())
                .categoryName(original.getCategoryName())
                .memberNickName(original.getMemberNickName())
                .title(original.getTitle())
                .content(summarizedContent)
                .boardViewsCount(original.getBoardViewsCount())
                .boardLikesCount(original.getBoardLikesCount())
                .createdAt(original.getCreatedAt())
                .updatedAt(original.getUpdatedAt())
                .commentsCount(original.getCommentsCount())
                .isPin(original.getIsPin())
                .build();
    }



    public BoardResponseDto convertBoardToBoardResponseDto(Board board){
        return BoardResponseDto.builder()
                .boardId(board.getId())
                .memberId(board.getMember().getId())
                .categoryId(board.getCategory().getId())
                .categoryName(board.getCategory().getCategoryName())
                .memberNickName(board.getMember().getNickname())
                .title(board.getTitle())
                .content(board.getContent())
                .boardViewsCount(board.getViews().getViews())
                .boardLikesCount(board.getBoardLikeCount().getLikes())
                .createdAt(board.getRegistedAt())
                .updatedAt(board.getUpdatedAt())
                .commentsCount(board.getCommentCount().getCounts())
                .isPin(board.isPin())
                .build();
    }

    public String summaryContent(String content) {
        if (content.length() > 100) {
            return content.substring(0, 100) + "...";
        }
        return content;
    }



    public BoardResponseDto convertBoardToBoardResponseDtoWithSummaryContent(Board board){

        String summaryContent = summaryContent(board.getContent());

        return BoardResponseDto.builder()
                .boardId(board.getId())
                .memberId(board.getMember().getId())
                .categoryId(board.getCategory().getId())
                .categoryName(board.getCategory().getCategoryName())
                .memberNickName(board.getMember().getNickname())
                .title(board.getTitle())
                .content(summaryContent)
                .boardViewsCount(board.getViews().getViews())
                .boardLikesCount(board.getBoardLikeCount().getLikes())
                .createdAt(board.getRegistedAt())
                .updatedAt(board.getUpdatedAt())
                .commentsCount(board.getCommentCount().getCounts())
                .isPin(board.isPin())
                .build();
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
    public Boolean likeBoardById(LikeBoardVo vo) {
        if (boardLikeRepository.existsByBoardIdAndMemberId(vo.getBoardId(), vo.getMemberId())) {
            return false;
        }

        boardLikeRepository.addLikeIfNotExists(vo.getBoardId(), vo.getMemberId());
        return true;
    }

    public Page<BoardResponseDto> getBoardsByMember(GetBoardsByMemberVo vo) {
        Member member = memberService.getMemberEntityById(vo.getMemberId());
        PageRequest pageRequest = PageRequest.of(vo.getPageRequestDto().getPage()-1, vo.getPageRequestDto().getSize());

        Page<BoardResponseDto> dtoPage = boardRepository.findBoardDtosByMemberOrderByRegistedAtDesc(vo.getMemberId(), pageRequest);

        List<BoardResponseDto> summarizedDtos = dtoPage.getContent().stream()
                .map(dto -> createSummarizedBoardDto(dto))
                .toList();

        return new PageImpl<>(summarizedDtos, pageRequest, dtoPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<ResponseCommentDto> getCommentsByMember(GetCommentsByMemberVo vo) {
        PageRequest pageRequest = PageRequest.of(vo.getPageRequestDto().getPage()-1, vo.getPageRequestDto().getSize());

        Page<Comment> comments = commentService.getCommentsByMemberIdWithAllFetch(vo.getMemberId(), pageRequest);

        List<ResponseCommentDto> commentDtos = comments.getContent().stream()
                .map(comment -> ResponseCommentDto.builder()
                    .id(comment.getId())
                    .boardId(comment.getBoard().getId())
                    .boardTitle(comment.getBoard().getTitle())
                    .memberId(comment.getMember().getId())
                    .nickName(comment.getMember().getNickname())
                    .content(comment.getContent())
                    .likes(comment.getLikeCount() != null ? comment.getLikeCount().getLikes() : 0)
                    .createdAt(comment.getRegistedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .build())
                .toList();

        return new PageImpl<>(commentDtos, pageRequest, comments.getTotalElements());
    }

    @Transactional
    public void softDeleteBoard(DeleteBoardVo vo) {
        Board board = boardService.getBoardById(vo.getBoardId());
        if (!board.getMember().getId().equals(vo.getMemberId())) {
            throw new KimprunException(KimprunExceptionEnum.AUTHENTICATION_REQUIRED_EXCEPTION,
                "User not authorized to delete this board: " + vo.getBoardId(), HttpStatus.UNAUTHORIZED, "BoardPacadeService.softDeleteBoard");
        }

        board.softDelete();
        boardRepository.save(board);
    }

    // DTO 반환 메소드들 (Controller용)
    @Transactional
    public BoardResponseDto createBoardDto(CreateBoardVo vo) {
        Board board = createBoard(vo.getMemberId(), vo.getCategoryId(), vo.getCreateBoardRequestDto());
        return convertBoardToBoardResponseDto(board);
    }

    // CommentController에서 사용하는 DTO 메소드들
    public Page<ResponseCommentDto> getCommentsDto(GetCommentsVo vo) {
        Board board = boardService.getBoardById(vo.getBoardId());
        Page<Comment> comments = commentService.getCommentByBoard(board, vo.getPage());
        List<ResponseCommentDto> commentDtos = commentService.converCommentsToResponseDtoList(comments.getContent());
        PageRequest pageRequest = PageRequest.of(vo.getPage(), 30); // CommentServiceImpl에서 사용하는 페이지 사이즈
        return new PageImpl<>(commentDtos, pageRequest, comments.getTotalElements());
    }

    @Transactional
    public ResponseCommentDto createCommentDto(CreateCommentVo vo) {
        Comment comment = createComment(vo.getMemberId(), vo.getBoardId(), vo.getRequestCreateCommentDto());
        return commentService.convertCommentToResponseDto(comment);
    }
}
