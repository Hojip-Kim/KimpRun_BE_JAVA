package kimp.community.service.impl;

import kimp.community.dao.CategoryDao;

import kimp.community.dto.category.CategoryDto;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;
import kimp.community.entity.Category;
import kimp.community.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao){
        this.categoryDao = categoryDao;
    }

    @Override
    public List<CategoryDto> getAllCategories(){
        List<Category> categories = categoryDao.getAllCategory();

        List<CategoryDto> categoryDtos = new ArrayList<>();

        for(Category category : categories){
            categoryDtos.add(new CategoryDto(category.getId(), category.getCategoryName()));
        }

        return categoryDtos;
    }

    @Override
    public CategoryDto getCategoryByID(Long id){
        Category category = categoryDao.getCategoryById(id);

        return new CategoryDto(category.getId(), category.getCategoryName());
    }

    @Override
    public CategoryDto createCategory(CreateCategoryRequestDto createCategoryDto) {
        Category category = categoryDao.createCategory(createCategoryDto);

        return new CategoryDto(category.getId(), category.getCategoryName());
    }

    @Override
    public CategoryDto updatedCategory(UpdateCategoryRequestDto updateCategoryRequestDto) {
        Category category = categoryDao.updateCategory(updateCategoryRequestDto);

        return new CategoryDto(category.getId(), category.getCategoryName());
    }

    @Override
    public Boolean deleteCategory(Long id) {

        return categoryDao.deleteCategoryById(id);
    }


}
