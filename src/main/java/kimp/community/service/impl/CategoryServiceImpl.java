package kimp.community.service.impl;

import jakarta.transaction.Transactional;
import kimp.community.dao.BoardCountDao;
import kimp.community.dao.CategoryDao;

import kimp.community.dto.category.CategoryDto;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;
import kimp.community.entity.BoardCount;
import kimp.community.entity.Category;
import kimp.community.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;
    private final BoardCountDao boardCountDao;

    public CategoryServiceImpl(CategoryDao categoryDao, BoardCountDao boardCountDao){
        this.categoryDao = categoryDao;
        this.boardCountDao = boardCountDao;
    }

    @Override
    public List<Category> getAllCategories(){
        List<Category> categories = categoryDao.getAllCategory();

        return categories;
    }

    @Override
    public Category getCategoryByID(Long id){
        Category category = categoryDao.getCategoryById(id);

        return category;
    }

    @Transactional
    @Override
    public Category createCategory(CreateCategoryRequestDto createCategoryDto) {
        Category category = categoryDao.createCategory(createCategoryDto);

        BoardCount boardCount = createBoardCount(category);

        category.setBoardCount(boardCount);

        return category;
    }

    @Override
    public BoardCount createBoardCount(Category category){

        BoardCount boardCount = boardCountDao.createBoardCount(category);

        return boardCount;
    }

    @Override
    public Category updatedCategory(UpdateCategoryRequestDto updateCategoryRequestDto) {
        Category category = categoryDao.updateCategory(updateCategoryRequestDto);

        return category;
    }

    @Override
    public Boolean deleteCategory(Long id) {

        return categoryDao.deleteCategoryById(id);
    }

    @Override
    public CategoryDto convertCategoryToDto(Category category) {
        return new CategoryDto(category.getId(),category.getCategoryName());
    }

    @Override
    public List<CategoryDto> convertCategoryListToDto(List<Category> categories) {

        List<CategoryDto> categoryDtoList = categories.stream()
                .map(category -> new CategoryDto(category.getId(), category.getCategoryName()))
                .collect(Collectors.toList());

        return categoryDtoList;
    }
}
