package unit.kimp.community.controller;

import kimp.community.controller.CommentController;
import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.dto.comment.request.RequestUpdateCommentDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.entity.Comment;
import kimp.community.service.BoardPacadeService;
import kimp.community.service.CommentPacadeService;
import kimp.community.service.CommentService;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @Mock
    private BoardPacadeService boardPacadeService;

    @Mock
    private CommentPacadeService commentPacadeService;

    @Mock
    private CustomUserDetails customUserDetails;

    private Comment mockComment;
    private ResponseCommentDto mockResponseCommentDto;

    @BeforeEach
    void setUp() {
        mockComment = new Comment();
        mockResponseCommentDto = new ResponseCommentDto();

        lenient().when(customUserDetails.getId()).thenReturn(1L);
    }

    @Test
    @DisplayName("댓글 조회 성공")
    void shouldReturnCommentsSuccessfully() {
        // Arrange
        List<Comment> comments = Arrays.asList(mockComment);
        List<ResponseCommentDto> responseDtos = Arrays.asList(mockResponseCommentDto);
        when(boardPacadeService.getCommentsDto(any())).thenReturn(new PageImpl<>(responseDtos, Pageable.unpaged(), 1));

        // Act
        ApiResponse<List<ResponseCommentDto>> response = commentController.getComment(1L, 0);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(responseDtos, response.getData());
        verify(boardPacadeService, times(1)).getCommentsDto(any());
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void shouldCreateCommentSuccessfully() {
        // Arrange
        RequestCreateCommentDto createCommentDto = new RequestCreateCommentDto();
        when(boardPacadeService.createCommentDto(any())).thenReturn(mockResponseCommentDto);

        // Act
        ApiResponse<ResponseCommentDto> response = commentController.createComment(customUserDetails, 1L, createCommentDto);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockResponseCommentDto, response.getData());
        verify(boardPacadeService, times(1)).createCommentDto(any());
    }

    @Test
    @DisplayName("댓글 업데이트 성공")
    void shouldUpdateCommentSuccessfully() {
        // Arrange
        RequestUpdateCommentDto updateCommentDto = new RequestUpdateCommentDto();
        when(commentService.updateCommentDto(anyLong(), any(RequestUpdateCommentDto.class))).thenReturn(mockResponseCommentDto);

        // Act
        ApiResponse<ResponseCommentDto> response = commentController.updateComment(customUserDetails, updateCommentDto);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockResponseCommentDto, response.getData());
        verify(commentService, times(1)).updateCommentDto(1L, updateCommentDto);
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void shouldDeleteCommentSuccessfully() {
        // Arrange
        when(commentService.deleteComment(anyLong(), anyLong())).thenReturn(true);

        // Act
        ApiResponse<Boolean> response = commentController.deleteComment(customUserDetails, 1L);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(commentService, times(1)).deleteComment(1L, 1L);
    }

    @Test
    @DisplayName("댓글 좋아요 성공")
    void shouldLikeCommentSuccessfully() {
        // Arrange
        when(commentPacadeService.commentLikeById(anyLong(), anyLong())).thenReturn(true);

        // Act
        ApiResponse<Boolean> response = commentController.likeComment(customUserDetails, 1L);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(commentPacadeService, times(1)).commentLikeById(1L, 1L);
    }
}
