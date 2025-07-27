package unit.kimp.community.controller;

import kimp.community.controller.BoardController;
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
import kimp.exception.KimprunException;
import kimp.exception.response.ApiResponse;
import kimp.security.user.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {

    @InjectMocks
    private BoardController boardController;

    @Mock
    private BoardService boardService;

    @Mock
    private BoardPacadeService boardPacadeService;

    @Mock
    private BoardPerformanceService boardPerformanceService;

    @Mock
    private CustomUserDetails customUserDetails;

    private Board mockBoard;
    private BoardResponseDto mockBoardResponseDto;
    private BoardWithCommentResponseDto mockBoardWithCommentResponseDto;
    private AllBoardResponseDto mockAllBoardResponseDto;
    private BoardWithCountResponseDto mockBoardWithCountResponseDto;

    @BeforeEach
    void setUp() {
        mockBoard = new Board();
        mockBoardResponseDto = new BoardResponseDto();
        mockBoardWithCommentResponseDto = new BoardWithCommentResponseDto(
            1L, 1L, 1L, "Test Category", "Test User", "Test Title", "Test Content",
            0, 0, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), 0
        );
        mockAllBoardResponseDto = new AllBoardResponseDto();
        mockBoardWithCountResponseDto = new BoardWithCountResponseDto(new ArrayList<>(), 0);

        when(customUserDetails.getId()).thenReturn(1L);
    }

    @Test
    @DisplayName("게시글 조회 성공 (인증된 사용자)")
    void shouldReturnBoardWithCommentForAuthenticatedUser() {
        // Arrange
        when(boardPacadeService.getBoardByIdWithCommentPage(anyLong(), anyLong(), anyInt())).thenReturn(mockBoardWithCommentResponseDto);

        // Act
        ApiResponse<BoardWithCommentResponseDto> response = boardController.getBoard(customUserDetails, 1L, 1);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockBoardWithCommentResponseDto, response.getData());
        verify(boardPacadeService, times(1)).getBoardByIdWithCommentPage(1L, 1L, 1);
    }

    @Test
    @DisplayName("게시글 조회 성공 (인증되지 않은 사용자)")
    void shouldReturnBoardWithCommentForUnauthenticatedUser() {
        // Arrange
        when(boardPacadeService.getBoardByIdWithCommentPage(anyLong(), anyLong(), anyInt())).thenReturn(mockBoardWithCommentResponseDto);

        // Act
        ApiResponse<BoardWithCommentResponseDto> response = boardController.getBoard(null, 1L, 1);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockBoardWithCommentResponseDto, response.getData());
        verify(boardPacadeService, times(1)).getBoardByIdWithCommentPage(-1L, 1L, 1);
    }

    @Test
    @DisplayName("게시글 조회 실패: 유효하지 않은 게시글 ID")
    void shouldThrowExceptionWhenGetBoardWithInvalidBoardId() {
        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> boardController.getBoard(customUserDetails, -1L, 1));
        assertEquals("Board ID must be non-negative", exception.getTrace());
    }

    @Test
    @DisplayName("모든 카테고리 게시글 페이지 조회 성공")
    void shouldReturnAllCategoryBoardsPage() {
        // Arrange
        when(boardService.getBoardsByPage(anyInt())).thenReturn(new PageImpl<>(Arrays.asList(mockBoard), Pageable.ofSize(10), 1));
        when(boardService.getBoardsCount()).thenReturn(1L);
        when(boardService.convertBoardPagesToAllBoardResponseDtos(any(), anyLong())).thenReturn(mockAllBoardResponseDto);

        // Act
        ApiResponse<AllBoardResponseDto> response = boardController.getAllCategoryBoards(1);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockAllBoardResponseDto, response.getData());
        verify(boardService, times(1)).getBoardsByPage(0);
        verify(boardService, times(1)).getBoardsCount();
        verify(boardService, times(1)).convertBoardPagesToAllBoardResponseDtos(any(), anyLong());
    }

    @Test
    @DisplayName("모든 카테고리 게시글 페이지 조회 실패: 유효하지 않은 페이지 번호")
    void shouldThrowExceptionWhenGetAllCategoryBoardsWithInvalidPage() {
        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> boardController.getAllCategoryBoards(0));
        assertEquals("Page number must be greater than 0", exception.getTrace());
    }

    @Test
    @DisplayName("카테고리별 게시글 페이지 조회 성공")
    void shouldReturnBoardsPageWithCategoryId() {
        // Arrange
        when(boardPacadeService.getBoardPageWithCategoryId(anyLong(), anyInt())).thenReturn(new PageImpl<>(Arrays.asList(mockBoard), Pageable.ofSize(10), 1));
        when(boardPacadeService.convertBoardsToBoardResponseDtos(any())).thenReturn(new ArrayList<>());
        when(boardPacadeService.getBoardCountByCategoryId(anyLong())).thenReturn(1);

        // Act
        ApiResponse<BoardWithCountResponseDto> response = boardController.getBoardsPageWithPage(1L, 1);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        verify(boardPacadeService, times(1)).getBoardPageWithCategoryId(1L, 0);
        verify(boardPacadeService, times(1)).convertBoardsToBoardResponseDtos(any());
        verify(boardPacadeService, times(1)).getBoardCountByCategoryId(1L);
    }

    @Test
    @DisplayName("카테고리별 게시글 페이지 조회 실패: 유효하지 않은 카테고리 ID")
    void shouldThrowExceptionWhenGetBoardsPageWithInvalidCategoryId() {
        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> boardController.getBoardsPageWithPage(-1L, 1));
        assertEquals("Category ID must be greater than or equal to 0", exception.getTrace());
    }

    @Test
    @DisplayName("카테고리별 게시글 페이지 조회 실패: 유효하지 않은 페이지 번호")
    void shouldThrowExceptionWhenGetBoardsPageWithInvalidPage() {
        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> boardController.getBoardsPageWithPage(1L, 0));
        assertEquals("Page number must be greater than 0", exception.getTrace());
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void shouldCreateBoardSuccessfully() {
        // Arrange
        CreateBoardRequestDto createBoardRequestDto = new CreateBoardRequestDto("Test Title", "Test Content", "test_image.png");

        // Act
        ApiResponse<Void> response = boardController.createBoard(customUserDetails, 1L, createBoardRequestDto);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertNull(response.getData());
        verify(boardPerformanceService, times(1)).enqueueBoardQueue(any(BoardInsertDto.class));
    }

    @Test
    @DisplayName("게시글 생성 실패: 유효하지 않은 카테고리 ID")
    void shouldThrowExceptionWhenCreateBoardWithInvalidCategoryId() {
        // Arrange
        CreateBoardRequestDto createBoardRequestDto = new CreateBoardRequestDto("Test Title", "Test Content", "test_image.png");

        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> boardController.createBoard(customUserDetails, -1L, createBoardRequestDto));
        assertEquals("Category ID must be greater than or equal to 0", exception.getTrace());
    }

    @Test
    @DisplayName("게시글 생성 실패: DTO null")
    void shouldThrowExceptionWhenCreateBoardWithNullDto() {
        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> boardController.createBoard(customUserDetails, 1L, null));
        assertEquals("CreateBoardRequestDto cannot be null", exception.getTrace());
    }

    @Test
    @DisplayName("게시글 업데이트 성공")
    void shouldUpdateBoardSuccessfully() {
        // Arrange
        UpdateBoardRequestDto updateBoardRequestDto = new UpdateBoardRequestDto("Updated Title", "Updated Content");
        when(boardPacadeService.updateBoard(anyLong(), anyLong(), any(UpdateBoardRequestDto.class))).thenReturn(mockBoard);
        when(boardPacadeService.convertBoardToBoardResponseDto(any(Board.class))).thenReturn(mockBoardResponseDto);

        // Act
        ApiResponse<BoardResponseDto> response = boardController.updateBoard(customUserDetails, 1L, updateBoardRequestDto);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockBoardResponseDto, response.getData());
        verify(boardPacadeService, times(1)).updateBoard(1L, 1L, updateBoardRequestDto);
        verify(boardPacadeService, times(1)).convertBoardToBoardResponseDto(mockBoard);
    }

    @Test
    @DisplayName("게시글 업데이트 실패: 유효하지 않은 게시글 ID")
    void shouldThrowExceptionWhenUpdateBoardWithInvalidBoardId() {
        // Arrange
        UpdateBoardRequestDto updateBoardRequestDto = new UpdateBoardRequestDto("Updated Title", "Updated Content");

        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> boardController.updateBoard(customUserDetails, -1L, updateBoardRequestDto));
        assertEquals("Board ID must be greater than or equal to 0", exception.getTrace());
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void shouldDeleteBoardSuccessfully() {
        // Arrange
        when(boardPacadeService.deleteBoard(anyLong(), anyLong())).thenReturn(true);

        // Act
        ApiResponse<Boolean> response = boardController.deleteBoard(customUserDetails, 1L);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(boardPacadeService, times(1)).deleteBoard(1L, 1L);
    }

    @Test
    @DisplayName("게시글 삭제 실패: 유효하지 않은 게시글 ID")
    void shouldThrowExceptionWhenDeleteBoardWithInvalidBoardId() {
        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> boardController.deleteBoard(customUserDetails, -1L));
        assertEquals("Board ID must be greater than or equal to 0", exception.getTrace());
    }

    @Test
    @DisplayName("게시글 고정 활성화 성공 (관리자/운영자 전용)")
    void shouldActivateBoardsPinSuccessfully() {
        // Arrange
        RequestBoardPin requestBoardPin = new RequestBoardPin(Arrays.asList(1L, 2L));
        doNothing().when(boardService).activatePinWithBoard(anyList());

        // Act
        ApiResponse<Boolean> response = boardController.activateBoardsPin(customUserDetails, requestBoardPin);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(boardService, times(1)).activatePinWithBoard(requestBoardPin.getBoardIds());
    }

    @Test
    @DisplayName("게시글 고정 비활성화 성공 (관리자/운영자 전용)")
    void shouldDeactivateBoardsPinSuccessfully() {
        // Arrange
        RequestBoardPin requestBoardPin = new RequestBoardPin(Arrays.asList(1L, 2L));
        doNothing().when(boardService).deactivatePinWithBoard(anyList());

        // Act
        ApiResponse<Boolean> response = boardController.deActivateBoardsPin(customUserDetails, requestBoardPin);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(boardService, times(1)).deactivatePinWithBoard(requestBoardPin.getBoardIds());
    }

    @Test
    @DisplayName("게시글 좋아요 성공")
    void shouldLikeBoardSuccessfully() {
        // Arrange
        when(boardPacadeService.likeBoardById(anyLong(), anyLong())).thenReturn(true);

        // Act
        ApiResponse<Boolean> response = boardController.likeBoard(customUserDetails, 1L);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(boardPacadeService, times(1)).likeBoardById(1L, 1L);
    }
}
