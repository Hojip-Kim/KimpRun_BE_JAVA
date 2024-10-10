package kimp.community.dao.impl;

import kimp.community.dao.CategoryDao;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;
import kimp.community.entity.Category;
import kimp.community.repository.CategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryDaoImpl implements CategoryDao {

    private final CategoryRepository categoryRepository;

    public CategoryDaoImpl(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategory(){
        List<Category> categories = categoryRepository.findAll();

        if(categories.isEmpty()){
            throw new IllegalArgumentException("database error : categories not have");
        }

        return categories;
    }

    @Override
    public Category getCategoryById(Long id){
        Optional<Category> optionalCategory = this.categoryRepository.findById(id);

        if(optionalCategory.isEmpty()){
            throw new IllegalArgumentException("Category not found for id : " + id);
        }

        return optionalCategory.get();

    }

    @Override
    public Category createCategory(CreateCategoryRequestDto createCategoryRequestDto){
        Optional<Category> optionalCategory = this.categoryRepository.findByCategoryName(createCategoryRequestDto.getCategoryName());

        if(optionalCategory.isPresent()){
            throw new IllegalArgumentException("Already have " + optionalCategory.get().getCategoryName() + " name");
        }

        return this.categoryRepository.save(new Category(createCategoryRequestDto.getCategoryName()));
    }

    @Override
    public Category updateCategory(UpdateCategoryRequestDto updateCategoryRequestDto){
        if(this.categoryRepository.findByCategoryName(updateCategoryRequestDto.getCategoryName()).isPresent()){
            throw new IllegalArgumentException("Already have " + updateCategoryRequestDto.getCategoryName() + " name");
        }
        Category category = getCategoryById(updateCategoryRequestDto.getCategoryId());

        category.updateCategoryName(updateCategoryRequestDto.getCategoryName());

        return this.categoryRepository.save(category);

    }

    @Override
    public Boolean deleteCategoryById(Long id){
        Boolean isDeleted = false;

        Category category = getCategoryById(id);

        try {
            this.categoryRepository.deleteById(category.getId());
        }catch (Exception e){
            isDeleted = false;
        }

        return isDeleted;
    }


}
