package kimp.community.dao;

import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;
import kimp.community.entity.Category;

import java.util.List;

public interface CategoryDao {

    public List<Category> getAllCategory();
    public Category getCategoryById(Long id);
    public Category getCategoryByIdWithBoardCount(Long id);
    public Category createCategory(CreateCategoryRequestDto createCategoryRequestDto);
    public Category updateCategory(UpdateCategoryRequestDto updateCategoryRequestDto);
    public Boolean deleteCategoryById(Long id);
}
