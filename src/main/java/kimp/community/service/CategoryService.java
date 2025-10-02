package kimp.community.service;

import kimp.community.dto.category.CategoryDto;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;
import kimp.community.entity.BoardCount;
import kimp.community.entity.Category;
import kimp.community.vo.DeleteCategoryVo;
import kimp.community.vo.GetCategoryVo;
import kimp.community.vo.UpdateCategoryVo;

import java.util.List;

public interface CategoryService {

    public List<Category> getAllCategories();

    public Category getCategoryByID(Long id);

    public Category createCategory(CreateCategoryRequestDto categoryDto);

    public BoardCount createBoardCount(Category category);

    public Category updatedCategory(UpdateCategoryRequestDto updateCategoryRequestDto);

    public Boolean deleteCategory(DeleteCategoryVo vo);

    public CategoryDto convertCategoryToDto(Category category);

    public List<CategoryDto> convertCategoryListToDto(List<Category> categories);

    // DTO 반환 메소드들 (Controller용)
    public List<CategoryDto> getAllCategoriesDto();

    public CategoryDto getCategoryByIdDto(GetCategoryVo vo);

    public CategoryDto updatedCategoryDto(UpdateCategoryVo vo);

    // Batch methods for initialization
    void initializeCategories(List<String> categoryNames);
}
