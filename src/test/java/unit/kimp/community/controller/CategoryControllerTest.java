package unit.kimp.community.controller;

import kimp.community.controller.CategoryController;
import kimp.community.dto.category.CategoryDto;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;
import kimp.community.entity.Category;
import kimp.community.service.CategoryPacadeService;
import kimp.community.service.CategoryService;
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
import org.springframework.mock.web.MockHttpServletRequest;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryPacadeService categoryPacadeService;

    @Mock
    private CustomUserDetails customUserDetails;

    private MockHttpServletRequest request;
    private Category mockCategory;
    private CategoryDto mockCategoryDto;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        request = new MockHttpServletRequest();
        mockCategory = new Category("Test Category");
        // Use reflection to set the id field as it's not exposed by a setter
        Field idField = Category.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(mockCategory, 1L);
        
        mockCategoryDto = new CategoryDto(1L, "Test Category");

        lenient().when(customUserDetails.getId()).thenReturn(1L);
    }

    @Test
    @DisplayName("모든 카테고리 조회 성공")
    void shouldReturnAllCategoriesSuccessfully() {
        // Arrange
        List<Category> categories = Arrays.asList(mockCategory, new Category("Another Category"));
        List<CategoryDto> categoryDtos = Arrays.asList(mockCategoryDto, new CategoryDto(2L, "Another Category"));
        when(categoryService.getAllCategories()).thenReturn(categories);
        when(categoryService.convertCategoryListToDto(categories)).thenReturn(categoryDtos);

        // Act
        ApiResponse<List<CategoryDto>> response = categoryController.getAllCategories(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(categoryDtos, response.getData());
        verify(categoryService, times(1)).getAllCategories();
        verify(categoryService, times(1)).convertCategoryListToDto(categories);
    }

    @Test
    @DisplayName("ID로 카테고리 조회 성공")
    void shouldReturnCategoryByIdSuccessfully() {
        // Arrange
        when(categoryService.getCategoryByID(anyLong())).thenReturn(mockCategory);

        // Act
        ApiResponse<CategoryDto> response = categoryController.getCategory(request, 1L);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockCategoryDto.getId(), response.getData().getId());
        assertEquals(mockCategoryDto.getCategoryName(), response.getData().getCategoryName());
        verify(categoryService, times(1)).getCategoryByID(1L);
    }

    @Test
    @DisplayName("ID로 카테고리 조회 실패: 유효하지 않은 ID")
    void shouldThrowExceptionWhenGetCategoryWithInvalidId() {
        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> categoryController.getCategory(request, -1L));
        assertEquals("Category ID must be greater than or equal to 0", exception.getMessage());
    }

    @Test
    @DisplayName("카테고리 생성 성공 (관리자/운영자 전용)")
    void shouldCreateCategorySuccessfully() {
        // Arrange
        CreateCategoryRequestDto createRequest = new CreateCategoryRequestDto("New Category");
        when(categoryPacadeService.createCategory(anyLong(), any(CreateCategoryRequestDto.class))).thenReturn(mockCategory);
        when(categoryService.convertCategoryToDto(any(Category.class))).thenReturn(mockCategoryDto);

        // Act
        ApiResponse<CategoryDto> response = categoryController.createCategory(customUserDetails, createRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockCategoryDto, response.getData());
        verify(categoryPacadeService, times(1)).createCategory(1L, createRequest);
        verify(categoryService, times(1)).convertCategoryToDto(mockCategory);
    }

    @Test
    @DisplayName("카테고리 업데이트 성공 (관리자/운영자 전용)")
    void shouldUpdateCategorySuccessfully() {
        // Arrange
        UpdateCategoryRequestDto updateRequest = new UpdateCategoryRequestDto(1L, "Updated Category");
        when(categoryService.updatedCategory(any(UpdateCategoryRequestDto.class))).thenReturn(mockCategory);

        // Act
        ApiResponse<CategoryDto> response = categoryController.patchCategory(customUserDetails, updateRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockCategoryDto.getId(), response.getData().getId());
        assertEquals(mockCategoryDto.getCategoryName(), response.getData().getCategoryName());
        verify(categoryService, times(1)).updatedCategory(updateRequest);
    }

    @Test
    @DisplayName("카테고리 삭제 성공 (관리자/운영자 전용)")
    void shouldDeleteCategorySuccessfully() {
        // Arrange
        when(categoryService.deleteCategory(anyLong())).thenReturn(true);

        // Act
        ApiResponse<Boolean> response = categoryController.deleteCategory(customUserDetails, 1L);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    @DisplayName("카테고리 삭제 실패: 유효하지 않은 ID")
    void shouldThrowExceptionWhenDeleteCategoryWithInvalidId() {
        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> categoryController.deleteCategory(customUserDetails, -1L));
        assertEquals("Category ID must be greater than or equal to 0", exception.getMessage());
    }
}
