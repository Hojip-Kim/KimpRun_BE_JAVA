package kimp.community.service;

import kimp.community.dto.category.CategoryDto;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;
import kimp.community.entity.BoardCount;
import kimp.community.entity.Category;

import java.util.List;

public interface CategoryService {

    public List<Category> getAllCategories();

    public Category getCategoryByID(Long id);

    public Category createCategory(CreateCategoryRequestDto categoryDto);

    public BoardCount createBoardCount(Category category);

    public Category updatedCategory(UpdateCategoryRequestDto updateCategoryRequestDto);

    public Boolean deleteCategory(Long id);

    public CategoryDto convertCategoryToDto(Category category);

    public List<CategoryDto> convertCategoryListToDto(List<Category> categories);
}
