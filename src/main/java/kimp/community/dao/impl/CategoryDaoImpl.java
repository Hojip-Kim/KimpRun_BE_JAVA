package kimp.community.dao.impl;

import kimp.community.dao.CategoryDao;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;
import kimp.community.entity.Category;
import kimp.community.repository.CategoryRepository;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import org.springframework.http.HttpStatus;
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
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, "No categories found in database", HttpStatus.NOT_FOUND, "CategoryDaoImpl.getAllCategory");
        }

        return categories;
    }

    @Override
    public Category getCategoryById(Long id){
        Optional<Category> optionalCategory = this.categoryRepository.findById(id);

        if(optionalCategory.isEmpty()){
            throw new KimprunException(KimprunExceptionEnum.INTERNAL_SERVER_ERROR, "Not have category Id : " + id, HttpStatus.ACCEPTED, "trace");
        }

        return optionalCategory.get();

    }

    @Override
    public Category getCategoryByIdWithBoardCount(Long id){
        Optional<Category> optionalCategory = this.categoryRepository.findByIdWithBoardCount(id);

        if(optionalCategory.isEmpty()){
            throw new KimprunException(KimprunExceptionEnum.INTERNAL_SERVER_ERROR, "Not have category Id : " + id, HttpStatus.ACCEPTED, "trace");
        }

        return optionalCategory.get();
    }

    @Override
    public Category createCategory(CreateCategoryRequestDto createCategoryRequestDto){
        Optional<Category> optionalCategory = this.categoryRepository.findByCategoryName(createCategoryRequestDto.getCategoryName());

        if(optionalCategory.isPresent()){
            return optionalCategory.get();
        }

        return this.categoryRepository.save(new Category(createCategoryRequestDto.getCategoryName()));
    }

    @Override
    public Category updateCategory(UpdateCategoryRequestDto updateCategoryRequestDto){
        if(this.categoryRepository.findByCategoryName(updateCategoryRequestDto.getCategoryName()).isPresent()){
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_ALREADY_EXISTS_EXCEPTION, "Category name already exists: " + updateCategoryRequestDto.getCategoryName(), HttpStatus.CONFLICT, "CategoryDaoImpl.updateCategory");
        }
        Category category = getCategoryById(updateCategoryRequestDto.getCategoryId());

        category.updateCategoryName(updateCategoryRequestDto.getCategoryName());

        return this.categoryRepository.save(category);

    }

    @Override
    public Boolean deleteCategoryById(Long id){
        Boolean isDeleted = true;

        Category category = getCategoryById(id);

        try {
            this.categoryRepository.deleteById(category.getId());
        }catch (Exception e){
            isDeleted = false;
        }

        return isDeleted;
    }


}
