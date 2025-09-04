package kimp.community.service.impl;

import kimp.community.dao.BoardCountDao;
import kimp.community.dao.CategoryDao;

import kimp.community.dto.category.CategoryDto;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;
import kimp.community.entity.BoardCount;
import kimp.community.entity.Category;
import kimp.community.service.CategoryService;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;
    private final BoardCountDao boardCountDao;

    public CategoryServiceImpl(CategoryDao categoryDao, BoardCountDao boardCountDao){
        this.categoryDao = categoryDao;
        this.boardCountDao = boardCountDao;
    }

    @Override
    public List<Category> getAllCategories(){
        try {
            List<Category> categories = categoryDao.getAllCategory();
            return categories;
        }catch(Exception e){
            throw new KimprunException(KimprunExceptionEnum.REQUEST_ACCEPTED, "Not have data", HttpStatus.ACCEPTED, "trace");
        }
    }

    @Override
    public Category getCategoryByID(Long id){
        Category category = categoryDao.getCategoryById(id);

        return category;
    }
    
    public Category getCategoryByIDWithBoardCount(Long id){
        Category category = categoryDao.getCategoryByIdWithBoardCount(id);

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

    // DTO 반환 메소드들 (Controller용)
    @Override
    public List<CategoryDto> getAllCategoriesDto() {
        try {
            List<Category> categories = categoryDao.getAllCategory();
            return convertCategoryListToDto(categories);
        }catch(Exception e){
            throw new KimprunException(KimprunExceptionEnum.REQUEST_ACCEPTED, "Not have data", HttpStatus.ACCEPTED, "trace");
        }
    }
    
    @Override
    public CategoryDto getCategoryByIdDto(Long id) {
        Category category = getCategoryByID(id);
        return convertCategoryToDto(category);
    }
    
    @Override
    public CategoryDto updatedCategoryDto(UpdateCategoryRequestDto updateCategoryRequestDto) {
        Category category = updatedCategory(updateCategoryRequestDto);
        return convertCategoryToDto(category);
    }
    
    @Override
    @Transactional
    public void initializeCategories(List<String> categoryNames) {
        log.info("Category 배치 초기화 시작 - {} 개 카테고리", categoryNames.size());
        
        // 1. 기존 모든 Category를 한 번에 조회
        List<Category> existingCategories = categoryDao.getAllCategory();
        List<String> existingNames = existingCategories.stream()
            .map(Category::getCategoryName)
            .toList();
        
        // 2. 새로 생성해야 할 카테고리들 필터링
        List<String> newCategoryNames = categoryNames.stream()
            .filter(name -> !existingNames.contains(name))
            .toList();
            
        // 3. 배치로 새로운 Category 생성
        if (!newCategoryNames.isEmpty()) {
            for (String name : newCategoryNames) {
                CreateCategoryRequestDto createDto = new CreateCategoryRequestDto(name);
                createCategory(createDto);
            }
            
            log.info("Category {} 개 배치 생성 완료", newCategoryNames.size());
        } else {
            log.info("모든 Category가 이미 존재함");
        }
    }
}
