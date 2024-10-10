package kimp.community.controller;

import jakarta.servlet.http.HttpServletRequest;
import kimp.community.dto.category.CategoryDto;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;
import kimp.community.entity.Category;
import kimp.community.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO : Category 관리의 경우 userDetails ROLES가 관리자 등급인 경우에만 접근허용하도록 조치.
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getAllCategories(HttpServletRequest request){

        List<Category> category = categoryService.getAllCategories();

        return categoryService.convertCategoryListToDto(category);
    }

    @GetMapping("/{id}")
    public CategoryDto getCategory(HttpServletRequest request, @PathVariable Long id){
        if(id < 0){
            throw new IllegalArgumentException("category Id is not available.");
        }

        Category category = categoryService.getCategoryByID(id);

        return new CategoryDto(category.getId(), category.getCategoryName());
    }
    @PostMapping
    public CategoryDto createCategory(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreateCategoryRequestDto createCategoryRequestDto){

        Category category = categoryService.createCategory(createCategoryRequestDto);

        return categoryService.convertCategoryToDto(category);
    }

    @PatchMapping
    public CategoryDto patchCategory(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UpdateCategoryRequestDto updateCategoryRequestDto){

        Category category = categoryService.updatedCategory(updateCategoryRequestDto);

        return new CategoryDto(category.getId(), category.getCategoryName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id){
        if(id < 0){
            throw new IllegalArgumentException("category Id is not available");
        }
        boolean deleted = categoryService.deleteCategory(id);
        if(!deleted){
            return ResponseEntity.notFound().build(); // 404 반환
           }

        return ResponseEntity.noContent().build(); // 204 반환
    }

}
