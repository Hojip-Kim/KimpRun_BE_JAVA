package kimp.community.controller;

import jakarta.servlet.http.HttpServletRequest;
import kimp.community.dto.category.CategoryDto;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;
import kimp.community.entity.Category;
import kimp.community.service.CategoryPacadeService;
import kimp.community.service.CategoryService;
import kimp.security.user.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryPacadeService categoryPacadeService;

    public CategoryController(CategoryService categoryService, CategoryPacadeService categoryPacadeService) {
        this.categoryService = categoryService;
        this.categoryPacadeService = categoryPacadeService;
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

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public CategoryDto createCategory(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreateCategoryRequestDto createCategoryRequestDto){

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        Category category = categoryPacadeService.createCategory(customUserDetails.getId(),createCategoryRequestDto);

        return categoryService.convertCategoryToDto(category);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping
    public CategoryDto patchCategory(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UpdateCategoryRequestDto updateCategoryRequestDto){

        Category category = categoryService.updatedCategory(updateCategoryRequestDto);

        return new CategoryDto(category.getId(), category.getCategoryName());
    }

    @PreAuthorize("hasRole('MANAGER')")
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
