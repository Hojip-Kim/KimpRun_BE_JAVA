package unit.kimp.community.controller;

import kimp.community.controller.BoardController;
import kimp.community.dto.board.request.BoardLikeRequest;
import kimp.community.dto.board.request.CreateBoardRequestDto;
import kimp.community.dto.board.request.RequestBoardPin;
import kimp.community.dto.board.request.UpdateBoardRequestDto;
import kimp.community.dto.board.response.BoardResponseDto;
import kimp.community.dto.board.response.BoardWithCommentResponseDto;
import kimp.community.entity.Board;
import kimp.community.service.BoardService;
import kimp.community.service.BoardPacadeService;
import kimp.common.dto.PageRequestDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {

    @InjectMocks
    private BoardController boardController;

    @Mock
    private BoardService boardService;

    @Mock
    private BoardPacadeService boardPacadeService;


    @Mock
    private CustomUserDetails customUserDetails;

    private Board mockBoard;
    private BoardResponseDto mockBoardResponseDto;
    private BoardWithCommentResponseDto mockBoardWithCommentResponseDto;

    @BeforeEach
    void setUp() {
        mockBoard = new Board();
        mockBoardResponseDto = new BoardResponseDto();
        mockBoardWithCommentResponseDto = new BoardWithCommentResponseDto(
            1L, 1L, 1L, "Test Category", "Test User", "Test Title", "Test Content",
            0, 0, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), 0, false
        );

        lenient().when(customUserDetails.getId()).thenReturn(1L);
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
        assertEquals("Board ID must be non-negative", exception.getMessage());
    }


    @Test
    @DisplayName("전체 카테고리 게시글 페이지 조회 성공")
    void shouldReturnAllCategoryBoardsPage() {
        // Arrange
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(1);
        pageRequestDto.setSize(10);
        
        Page<BoardResponseDto> mockPage = new PageImpl<>(Arrays.asList(mockBoardResponseDto), Pageable.ofSize(10), 1);
        when(boardPacadeService.getAllBoardDtoPage(any(PageRequestDto.class))).thenReturn(mockPage);

        // Act
        ApiResponse<Page<BoardResponseDto>> response = boardController.getBoardsPageWithPage(1L, pageRequestDto);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(mockPage, response.getData());
        verify(boardPacadeService, times(1)).getAllBoardDtoPage(pageRequestDto);
    }

    @Test
    @DisplayName("카테고리별 게시글 페이지 조회 성공")
    void shouldReturnBoardsPageWithCategoryId() {
        // Arrange
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(1);
        pageRequestDto.setSize(10);
        
        Page<BoardResponseDto> mockPage = new PageImpl<>(Arrays.asList(mockBoardResponseDto), Pageable.ofSize(10), 1);
        when(boardPacadeService.getBoardDtoPageWithCategoryId(anyLong(), any(PageRequestDto.class))).thenReturn(mockPage);

        // Act
        ApiResponse<Page<BoardResponseDto>> response = boardController.getBoardsPageWithPage(2L, pageRequestDto);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(mockPage, response.getData());
        verify(boardPacadeService, times(1)).getBoardDtoPageWithCategoryId(2L, pageRequestDto);
    }

    @Test
    @DisplayName("카테고리별 게시글 페이지 조회 실패: 유효하지 않은 카테고리 ID")
    void shouldThrowExceptionWhenGetBoardsPageWithInvalidCategoryId() {
        // Arrange
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(1);
        
        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> boardController.getBoardsPageWithPage(-1L, pageRequestDto));
        assertEquals("Category ID must be greater than or equal to 0", exception.getMessage());
    }

    @Test
    @DisplayName("카테고리별 게시글 페이지 조회 실패: 유효하지 않은 페이지 번호")
    void shouldThrowExceptionWhenGetBoardsPageWithInvalidPage() {
        // Arrange
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(0); // Invalid page number
        
        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> boardController.getBoardsPageWithPage(1L, pageRequestDto));
        assertEquals("Page number must be greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void shouldCreateBoardSuccessfully() {
        // Arrange
        CreateBoardRequestDto createBoardRequestDto = new CreateBoardRequestDto("Test Title", "Test Content", "test_image.png");
        when(boardPacadeService.createBoardDto(anyLong(), anyLong(), any(CreateBoardRequestDto.class))).thenReturn(mockBoardResponseDto);

        // Act
        ApiResponse<BoardResponseDto> response = boardController.createBoard(customUserDetails, 1L, createBoardRequestDto);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockBoardResponseDto, response.getData());
        verify(boardPacadeService, times(1)).createBoardDto(1L, 1L, createBoardRequestDto);
    }

    @Test
    @DisplayName("게시글 생성 실패: 유효하지 않은 카테고리 ID")
    void shouldThrowExceptionWhenCreateBoardWithInvalidCategoryId() {
        // Arrange
        CreateBoardRequestDto createBoardRequestDto = new CreateBoardRequestDto("Test Title", "Test Content", "test_image.png");

        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> boardController.createBoard(customUserDetails, -1L, createBoardRequestDto));
        assertEquals("Category ID must be greater than or equal to 0", exception.getMessage());
    }

    @Test
    @DisplayName("게시글 생성 실패: DTO null")
    void shouldThrowExceptionWhenCreateBoardWithNullDto() {
        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> boardController.createBoard(customUserDetails, 1L, null));
        assertEquals("CreateBoardRequestDto cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("게시글 업데이트 성공")
    void shouldUpdateBoardSuccessfully() {
        // Arrange
        UpdateBoardRequestDto updateBoardRequestDto = new UpdateBoardRequestDto("Updated Title", "Updated Content");
        when(boardPacadeService.updateBoard(anyLong(), anyLong(), any(UpdateBoardRequestDto.class))).thenReturn(mockBoardResponseDto);

        // Act
        ApiResponse<BoardResponseDto> response = boardController.updateBoard(customUserDetails, 1L, updateBoardRequestDto);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockBoardResponseDto, response.getData());
        verify(boardPacadeService, times(1)).updateBoard(1L, 1L, updateBoardRequestDto);
    }

    @Test
    @DisplayName("게시글 업데이트 실패: 유효하지 않은 게시글 ID")
    void shouldThrowExceptionWhenUpdateBoardWithInvalidBoardId() {
        // Arrange
        UpdateBoardRequestDto updateBoardRequestDto = new UpdateBoardRequestDto("Updated Title", "Updated Content");

        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> boardController.updateBoard(customUserDetails, -1L, updateBoardRequestDto));
        assertEquals("Board ID must be greater than or equal to 0", exception.getMessage());
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
        assertEquals("Board ID must be greater than or equal to 0", exception.getMessage());
    }

    @Test
    @DisplayName("게시글 고정 활성화 성공 (관리자/운영자 전용)")
    void shouldActivateBoardsPinSuccessfully() {
        // Arrange
        RequestBoardPin requestBoardPin = new RequestBoardPin(Arrays.asList(1L, 2L));
        when(boardService.activatePinWithBoard(anyList())).thenReturn(Arrays.asList(mockBoard));

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
        when(boardService.deactivatePinWithBoard(anyList())).thenReturn(Arrays.asList(mockBoard));

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
        ApiResponse<Boolean> response = boardController.likeBoard(customUserDetails, new BoardLikeRequest(1L));

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(boardPacadeService, times(1)).likeBoardById(1L, 1L);
    }
}
