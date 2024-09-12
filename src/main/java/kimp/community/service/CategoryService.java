package kimp.community.service;

import kimp.community.dto.category.CategoryDto;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;

import java.util.List;

public interface CategoryService {

    public List<CategoryDto> getAllCategories();

    public CategoryDto getCategoryByID(Long id);

    public CategoryDto createCategory(CreateCategoryRequestDto categoryDto);

    public CategoryDto updatedCategory(UpdateCategoryRequestDto updateCategoryRequestDto);

    public Boolean deleteCategory(Long id);
}
