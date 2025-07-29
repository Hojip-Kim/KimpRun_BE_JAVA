package kimp.community.controller;

import jakarta.servlet.http.HttpServletRequest;
import kimp.community.dto.category.CategoryDto;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;
import kimp.community.entity.Category;
import kimp.community.service.CategoryPacadeService;
import kimp.community.service.CategoryService;
import kimp.exception.response.ApiResponse;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import kimp.security.user.CustomUserDetails;
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
    public ApiResponse<List<CategoryDto>> getAllCategories(HttpServletRequest request){

        List<Category> category = categoryService.getAllCategories();
        List<CategoryDto> result = categoryService.convertCategoryListToDto(category);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryDto> getCategory(HttpServletRequest request, @PathVariable Long id){
        if(id < 0){
            throw new KimprunException(KimprunExceptionEnum.INVALID_ID_PARAMETER_EXCEPTION, "Category ID must be greater than or equal to 0", HttpStatus.BAD_REQUEST, "CategoryController.getCategory");
        }

        Category category = categoryService.getCategoryByID(id);
        CategoryDto result = new CategoryDto(category.getId(), category.getCategoryName());
        return ApiResponse.success(result);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR')")
    @PostMapping
    public ApiResponse<CategoryDto> createCategory(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody CreateCategoryRequestDto createCategoryRequestDto){

        CustomUserDetails customUserDetails = (CustomUserDetails) UserDetails;

        Category category = categoryPacadeService.createCategory(customUserDetails.getId(),createCategoryRequestDto);
        CategoryDto result = categoryService.convertCategoryToDto(category);
        return ApiResponse.success(result);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR')")
    @PatchMapping
    public ApiResponse<CategoryDto> patchCategory(@AuthenticationPrincipal UserDetails UserDetails, @RequestBody UpdateCategoryRequestDto updateCategoryRequestDto){

        Category category = categoryService.updatedCategory(updateCategoryRequestDto);
        CategoryDto result = new CategoryDto(category.getId(), category.getCategoryName());
        return ApiResponse.success(result);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR')")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteCategory(@AuthenticationPrincipal UserDetails UserDetails, @PathVariable Long id){
        if(id < 0){
            throw new KimprunException(KimprunExceptionEnum.INVALID_ID_PARAMETER_EXCEPTION, "Category ID must be greater than or equal to 0", HttpStatus.BAD_REQUEST, "CategoryController.deleteCategory");
        }
        boolean deleted = categoryService.deleteCategory(id);
        return ApiResponse.success(deleted);
    }

}
